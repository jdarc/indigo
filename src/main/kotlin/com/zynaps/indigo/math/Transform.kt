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
import com.zynaps.indigo.math.Scalar.equals
import com.zynaps.indigo.math.Scalar.toRadians
import kotlin.math.cos
import kotlin.math.sin

@Suppress("MemberVisibilityCanBePrivate", "unused")
data class Transform(val m00: Float, val m10: Float, val m01: Float, val m11: Float, val m02: Float, val m12: Float) {

    operator fun unaryMinus() = Transform(-m00, -m10, -m01, -m11, -m02, -m12)

    operator fun plus(rhs: Float) = Transform(
        m00 + rhs, m10 + rhs,
        m01 + rhs, m11 + rhs,
        m02 + rhs, m12 + rhs
    )

    operator fun plus(rhs: Transform) = Transform(
        m00 + rhs.m00, m10 + rhs.m10,
        m01 + rhs.m01, m11 + rhs.m11,
        m02 + rhs.m02, m12 + rhs.m12
    )

    operator fun minus(rhs: Float) = Transform(
        m00 - rhs, m10 - rhs,
        m01 - rhs, m11 - rhs,
        m02 - rhs, m12 - rhs
    )

    operator fun minus(rhs: Transform) = Transform(
        m00 - rhs.m00, m10 - rhs.m10,
        m01 - rhs.m01, m11 - rhs.m11,
        m02 - rhs.m02, m12 - rhs.m12
    )

    operator fun times(rhs: Float) = Transform(
        m00 * rhs, m10 * rhs,
        m01 * rhs, m11 * rhs,
        m02 * rhs, m12 * rhs
    )

    operator fun times(rhs: Transform): Transform {
        val t00 = (m00 * rhs.m00) + (m01 * rhs.m10)
        val t10 = (m10 * rhs.m00) + (m11 * rhs.m10)
        val t01 = (m00 * rhs.m01) + (m01 * rhs.m11)
        val t11 = (m10 * rhs.m01) + (m11 * rhs.m11)
        val t02 = (m00 * rhs.m02) + (m01 * rhs.m12) + m02
        val t12 = (m10 * rhs.m02) + (m11 * rhs.m12) + m12
        return Transform(t00, t10, t01, t11, t02, t12)
    }

    fun equals(rhs: Transform, epsilon: Float = EPSILON): Boolean {
        return equals(m00, rhs.m00, epsilon) && equals(m01, rhs.m01, epsilon) &&
               equals(m02, rhs.m02, epsilon) && equals(m10, rhs.m10, epsilon) &&
               equals(m11, rhs.m11, epsilon) && equals(m12, rhs.m12, epsilon)
    }

    companion object {

        val IDENTITY = Transform(1f, 0f, 0f, 1f, 0f, 0f)

        fun scaling(x: Float, y: Float) = Transform(x, 0f, 0f, y, 0f, 0f)

        fun scaling(v: Vector2) = scaling(v.x, v.y)

        fun translation(x: Float, y: Float) = Transform(1f, 0f, 0f, 1f, x, y)

        fun translation(v: Vector2) = Transform(1f, 0f, 0f, 1f, v.x, v.y)

        fun rotation(angle: Float): Transform {
            val cos = cos(toRadians(angle))
            val sin = sin(toRadians(angle))
            return Transform(cos, sin, -sin, cos, 0f, 0f)
        }

        fun create(position: Vector2, rotation: Float = 0f, scale: Vector2 = Vector2.ONE): Transform {
            val cos = cos(toRadians(rotation))
            val sin = sin(toRadians(rotation))
            val m00 = cos * scale.x
            val m10 = sin * scale.y
            val m01 = sin * scale.x
            val m11 = cos * scale.y
            return Transform(m00, m10, -m01, m11, position.x, position.y)
        }
    }
}
