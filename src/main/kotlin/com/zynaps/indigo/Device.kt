/*
 * Copyright (c) 2021 Jean d'Arc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.zynaps.indigo

import com.zynaps.indigo.math.Scalar.ceil
import com.zynaps.indigo.samplers.ColorSampler
import com.zynaps.indigo.tools.DynamicArray
import com.zynaps.indigo.tools.DynamicPool
import java.util.concurrent.Callable
import java.util.concurrent.ForkJoinPool
import kotlin.math.max
import kotlin.math.min

internal class Device(private val bitmap: Bitmap) {
    private var begin = true
    private val e0 = Edge()
    private val e1 = Edge()
    private val e2 = Edge()
    private val g = Gradients()

    private val pool = DynamicPool { Fragment() }
    private val spans = Array(bitmap.height) { DynamicArray<Fragment>() }
    private val tasks = renderTasks(bitmap, spans)

    var sampler: Sampler = ColorSampler.WHITE

    fun clear(color: Int) {
        bitmap.data.fill(color)
    }

    fun next() {
        begin = true
    }

    fun render() {
        ForkJoinPool.commonPool().invokeAll(tasks)
        spans.forEach { it.reset() }
        pool.reset()
    }

    fun rasterise(a: Point, b: Point, c: Point) {
        if (begin) { begin = false; g.configure(a, b, c) }

        if (max(a.x, max(b.x, c.x)) < 0f || min(a.x, min(b.x, c.x)) >= bitmap.width) return
        if (max(a.y, max(b.y, c.y)) < 0f || min(a.y, min(b.y, c.y)) >= bitmap.height) return

        if ((a.y - b.y) * (c.x - a.x) > (a.y - c.y) * (b.x - a.x)) {
            rast(c, b, a)
        } else {
            rast(a, b, c)
        }
    }

    private fun rast(a: Point, b: Point, c: Point) {
        if (a.y < b.y) when {
            c.y < a.y -> frag(c, a, b, e1, e0, e2, e0)
            b.y < c.y -> frag(a, b, c, e1, e0, e2, e0)
            else -> frag(a, c, b, e0, e1, e0, e2)
        } else when {
            c.y < b.y -> frag(c, b, a, e0, e1, e0, e2)
            a.y < c.y -> frag(b, a, c, e0, e1, e0, e2)
            else -> frag(b, c, a, e1, e0, e2, e0)
        }
    }

    private fun frag(a: Point, b: Point, c: Point, e00: Edge, e01: Edge, e10: Edge, e11: Edge) {
        if (e0.configure(g, a, c) > 0) {
            if (e1.configure(g, a, b) > 0 && e1.y1 < bitmap.height) {
                val fragment = pool.next().configure(sampler, g, e00, e01)
                val min = min(bitmap.height, e1.y2)
                for (y in e1.y1 until min) {
                    spans[y].add(fragment)
                }
            }
            if (e2.configure(g, b, c) > 0 && e2.y1 < bitmap.height) {
                val fragment = pool.next().configure(sampler, g, e10, e11)
                val min = min(bitmap.height, e2.y2)
                for (y in e2.y1 until min) {
                    spans[y].add(fragment)
                }
            }
        }
    }

    private fun renderTasks(bitmap: Bitmap, spans: Array<DynamicArray<Fragment>>) = (0 until bitmap.height).map { y ->
        Callable {
            val offset = y * bitmap.width
            for (i in 0 until spans[y].size) {
                val fragment = spans[y][i]
                val delta = y.toFloat() - fragment.y
                val lx = fragment.left(delta)
                val x1 = max(0, ceil(lx))
                val x2 = min(bitmap.width, ceil(fragment.right(delta)))
                if (x1 < bitmap.width && x2 > x1) {
                    val preStep = x1 - lx
                    var tuOverZ = fragment.udx * preStep + fragment.u(delta)
                    var tvOverZ = fragment.vdx * preStep + fragment.v(delta)
                    for (x in offset + x1 until offset + x2) {
                        val sample = fragment.sampler.sample(tuOverZ, tvOverZ)
                        tuOverZ += fragment.udx
                        tvOverZ += fragment.vdx

                        val alpha = sample ushr 24
                        if (alpha <= 0) continue

                        bitmap.data[x] = if (alpha == 255) sample else fastBlend(bitmap.data[x], sample, alpha)
                    }
                }
            }
        }
    }

    companion object {
        private fun fastBlend(x: Int, y: Int, a: Int): Int {
            val n = 255 - a
            val blu = (n * Argb.blu(x) ushr 8) + (a * Argb.blu(y) ushr 8)
            val grn = (n * Argb.grn(x) ushr 8) + (a * Argb.grn(y) ushr 8)
            val red = (n * Argb.red(x) ushr 8) + (a * Argb.red(y) ushr 8)
            return Argb.OPAQUE or red.shl(16) or grn.shl(8) or blu
        }
    }
}
