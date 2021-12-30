package com.zynaps.indigo.effects

import com.zynaps.indigo.Argb
import com.zynaps.indigo.Bitmap
import java.util.concurrent.Callable
import java.util.concurrent.ForkJoinPool
import kotlin.math.max
import kotlin.math.min

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Blur(private val source: Bitmap, private val destination: Bitmap = source) {

    var radius = 4
        set(value) {
            field = value.coerceIn(1, Int.MAX_VALUE)
            radiusChanged(field)
        }

    var steps = 2
        set(value) {
            field = value.coerceIn(1, Int.MAX_VALUE)
        }

    private var tmp = IntArray(source.data.size)

    private val dv = IntArray(256 * (radius + radius + 1))
    private val v = (0 until source.height).map { Callable { horiz(it * source.width, source.width - 1) } }
    private val h = (0 until source.width).map { Callable { vert(it, source.height - 1) } }

    fun apply() {
        for (times in 0 until steps) {
            ForkJoinPool.commonPool().invokeAll(v)
            ForkJoinPool.commonPool().invokeAll(h)
        }
    }

    private fun radiusChanged(radius: Int) {
        for (it in 0 until 256 * (radius + radius + 1)) {
            dv[it] = it / (radius + radius + 1)
        }
    }

    private fun pack(red: Int, grn: Int, blu: Int) = red.shl(16) or grn.shl(8) or blu

    private fun horiz(offset: Int, width: Int) {
        var rsum = 0
        var gsum = 0
        var bsum = 0
        var rgb = 0xFFFFFF and source.data[offset]
        if (rgb != 0) {
            rsum = radius * Argb.red(rgb)
            gsum = radius * Argb.grn(rgb)
            bsum = radius * Argb.blu(rgb)
        }

        for (i in 0..radius) {
            rgb = 0xFFFFFF and source.data[offset + i]
            if (rgb == 0) continue
            rsum += Argb.red(rgb)
            gsum += Argb.grn(rgb)
            bsum += Argb.blu(rgb)
        }

        var last = pack(dv[rsum], dv[gsum], dv[bsum])
        for (x in 0..width) {
            tmp[offset + x] = last
            val p1 = 0xFFFFFF and source.data[offset + min(width, x + radius + 1)]
            val p2 = 0xFFFFFF and source.data[offset + max(0, x - radius)]
            if (p1 == 0 && p2 == 0) continue
            rsum += Argb.red(p1) - Argb.red(p2)
            gsum += Argb.grn(p1) - Argb.grn(p2)
            bsum += Argb.blu(p1) - Argb.blu(p2)
            last = pack(dv[rsum], dv[gsum], dv[bsum])
        }
    }

    private fun vert(offset: Int, height: Int) {
        val srcWidth = source.width
        val baseRgb = tmp[offset]
        val baseRed = Argb.red(baseRgb)
        val baseGrn = Argb.grn(baseRgb)
        val baseBlu = Argb.blu(baseRgb)
        var rsum = 0
        var gsum = 0
        var bsum = 0
        for (i in 0 until radius) {
            rsum += baseRed
            gsum += baseGrn
            bsum += baseBlu
            val sumRgb = tmp[offset + i * srcWidth] and 0xFFFFFF
            if (sumRgb == 0) continue
            rsum += Argb.red(sumRgb)
            gsum += Argb.grn(sumRgb)
            bsum += Argb.blu(sumRgb)
        }
        val sumRgb = tmp[offset + radius * srcWidth]
        rsum += Argb.red(sumRgb)
        gsum += Argb.grn(sumRgb)
        bsum += Argb.blu(sumRgb)
        var last = pack(dv[rsum], dv[gsum], dv[bsum])
        for (y in 0..height) {
            destination.data[offset + y * srcWidth] = last
            val dst1 = tmp[offset + srcWidth * min(height, y + radius + 1)] and 0xFFFFFF
            val dst2 = tmp[offset + srcWidth * max(0, y - radius)] and 0xFFFFFF
            if (dst1 == 0 && dst2 == 0) continue
            if (dst1 != 0) {
                rsum += Argb.red(dst1)
                gsum += Argb.grn(dst1)
                bsum += Argb.blu(dst1)
            }
            if (dst2 != 0) {
                rsum -= Argb.red(dst2)
                gsum -= Argb.grn(dst2)
                bsum -= Argb.blu(dst2)
            }
            last = pack(dv[rsum], dv[gsum], dv[bsum])
        }
    }

    init {
        radiusChanged(radius)
    }
}
