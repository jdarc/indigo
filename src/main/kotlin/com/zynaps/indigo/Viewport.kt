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

import com.zynaps.indigo.math.Aabb
import com.zynaps.indigo.math.Dimension
import com.zynaps.indigo.math.Transform
import com.zynaps.indigo.math.Vector2

@Suppress("MemberVisibilityCanBePrivate", "unused")
class Viewport {

    var left = -160f
        private set

    var right = 160f
        private set

    var top = 100f
        private set

    var bottom = -100f
        private set

    val center get() = Vector2((right + left) * 0.5f, (top + bottom) * 0.5f)

    val width get() = right - left
    val height get() = top - bottom

    fun set(left: Float, right: Float, top: Float, bottom: Float) {
        this.left = left
        this.right = right
        this.top = top
        this.bottom = bottom
    }

    fun contains(min: Vector2, max: Vector2): Boolean {
        if (min.x > right || max.x < left) return false
        if (min.y > top || max.y < bottom) return false
        return true
    }

    fun viewportToScreen(screen: Dimension): Transform {
        val cx = -(right + left) * 0.5f
        val cy = -(top + bottom) * 0.5f
        val m00 = screen.width / this.width
        val m11 = screen.height / this.height
        return Transform(m00, 0f, 0f, m11, m00 * cx, m11 * cy)
    }

    fun screenToViewport(screen: Dimension): Transform {
        val m00 = this.width / screen.width
        val m11 = this.height / screen.height
        return Transform(m00, 0f, 0f, -m11, left, top)
    }
}
