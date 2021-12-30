package com.zynaps.indigo.math

import kotlin.math.max
import kotlin.math.min

data class Aabb(
    private var xMin: Float = Float.POSITIVE_INFINITY,
    private var yMin: Float = Float.POSITIVE_INFINITY,
    private var xMax: Float = Float.NEGATIVE_INFINITY,
    private var yMax: Float = Float.NEGATIVE_INFINITY
) {

    val minimum get() = Vector2(xMin, yMin)
    val maximum get() = Vector2(xMax, yMax)

    fun reset(): Aabb {
        xMin = Float.POSITIVE_INFINITY
        yMin = Float.POSITIVE_INFINITY
        xMax = Float.NEGATIVE_INFINITY
        yMax = Float.NEGATIVE_INFINITY
        return this
    }

    fun aggregate(x: Float, y: Float): Aabb {
        if (x.isFinite()) {
            xMin = min(xMin, x)
            xMax = max(xMax, x)
        }
        if (y.isFinite()) {
            yMin = min(yMin, y)
            yMax = max(yMax, y)
        }
        return this
    }

    fun aggregate(v: Vector2) = aggregate(v.x, v.y)

    fun aggregate(bounds: Aabb) = aggregate(bounds.xMin, bounds.yMin).aggregate(bounds.xMax, bounds.yMax)

    operator fun times(transform: Transform): Aabb {
        val tx0 = (transform.m00 * xMin) + (transform.m01 * yMin) + transform.m02
        val ty0 = (transform.m10 * xMin) + (transform.m11 * yMin) + transform.m12
        val tx1 = (transform.m00 * xMax) + (transform.m01 * yMin) + transform.m02
        val ty1 = (transform.m10 * xMax) + (transform.m11 * yMin) + transform.m12
        val tx2 = (transform.m00 * xMax) + (transform.m01 * yMax) + transform.m02
        val ty2 = (transform.m10 * xMax) + (transform.m11 * yMax) + transform.m12
        val tx3 = (transform.m00 * xMin) + (transform.m01 * yMax) + transform.m02
        val ty3 = (transform.m10 * xMin) + (transform.m11 * yMax) + transform.m12
        return Aabb().aggregate(tx0, ty0).aggregate(tx1, ty1).aggregate(tx2, ty2).aggregate(tx3, ty3)
    }
}
