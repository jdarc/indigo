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

@Suppress("unused")
internal object Scalar {
    const val EPSILON = 0.00005f

    fun equals(a: Float, b: Float, epsilon: Float = EPSILON) = !(a - b).isNaN() && kotlin.math.abs(a - b) <= epsilon

    fun clamp(value: Float, min: Float, max: Float) = kotlin.math.max(min, kotlin.math.min(max, value))

    fun ceil(a: Float) = 0x3FFFFFFF - (1073741823.0 - a).toInt()

    fun toRadians(degrees: Float) = degrees * 0.017453292f

    fun toDegrees(radians: Float) = radians * 57.29578f
}
