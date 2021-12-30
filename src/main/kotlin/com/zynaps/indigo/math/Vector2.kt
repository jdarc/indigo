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
package com.zynaps.indigo.math

import com.zynaps.indigo.math.Scalar.EPSILON
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@Suppress("unused", "MemberVisibilityCanBePrivate")
data class Vector2(val x: Float, val y: Float) {

    val length get() = sqrt(lengthSquared)

    val lengthSquared get() = (x * x) + (y * y)

    operator fun unaryMinus() = Vector2(-x, -y)

    operator fun plus(rhs: Float) = Vector2(x + rhs, y + rhs)

    operator fun plus(rhs: Vector2) = Vector2(x + rhs.x, y + rhs.y)

    operator fun minus(rhs: Float) = Vector2(x - rhs, y - rhs)

    operator fun minus(rhs: Vector2) = Vector2(x - rhs.x, y - rhs.y)

    operator fun times(rhs: Float) = Vector2(x * rhs, y * rhs)

    operator fun times(rhs: Vector2) = Vector2(x * rhs.x, y * rhs.y)

    operator fun times(rhs: Transform): Vector2 {
        val tx = (rhs.m00 * x) + (rhs.m01 * y) + rhs.m02
        val ty = (rhs.m10 * x) + (rhs.m11 * y) + rhs.m12
        return Vector2(tx, ty)
    }

    operator fun div(rhs: Float) = Vector2(x / rhs, y / rhs)

    operator fun div(rhs: Vector2) = Vector2(x / rhs.x, y / rhs.y)

    fun dot(rhs: Vector2) = (x * rhs.x) + (y * rhs.y)

    fun equals(rhs: Vector2, epsilon: Float = EPSILON): Boolean {
        val ex = Scalar.equals(x, rhs.x, epsilon)
        val ey = Scalar.equals(y, rhs.y, epsilon)
        return ex && ey
    }

    companion object {
        val ONE = Vector2(1f, 1f)
        val ZERO = Vector2(0f, 0f)

        val POSITIVE_INFINITY = Vector2(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        val NEGATIVE_INFINITY = Vector2(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY)

        fun min(v1: Vector2, v2: Vector2) = Vector2(min(v1.x, v2.x), min(v1.y, v2.y))
        fun max(v1: Vector2, v2: Vector2) = Vector2(max(v1.x, v2.x), max(v1.y, v2.y))
    }
}
