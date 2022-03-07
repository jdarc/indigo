package com.zynaps.indigo.geometry

import com.zynaps.indigo.math.Vector2
import kotlin.math.*


object CatmullRom {

    fun subdivide(points: List<Vector2>, closed: Boolean = false) = if (closed) {
        subdivide(points.takeLast(2) + points + points.take(2))
    } else {
        subdivide(points)
    }

    private fun subdivide(points: List<Vector2>) = (1 until points.size - 2).flatMap { i ->
        val p0 = points[i - 1]
        val p1 = points[i]
        val p2 = points[i + 1]
        val p3 = points[i + 2]

        val amount = max(10, ceil(Vector2.distance(p0, p1) / 10.0).toInt())

        val crs = CatmullRomSpline2D(p0, p1, p2, p3)
        (1..amount).map { crs.q(it / amount.toFloat()) }
    }

    private class CatmullRomSpline2D(p0: Vector2, p1: Vector2, p2: Vector2, p3: Vector2) {
        val alpha = 0.5f
        val tension = 0f

        val a: Vector2
        val b: Vector2
        val c: Vector2
        val d: Vector2

        fun q(t: Float): Vector2 {
            val px = a.x * t * t * t + b.x * t * t + c.x * t + d.x
            val py = a.y * t * t * t + b.y * t * t + c.y * t + d.y
            return Vector2(px, py)
        }

        init {
            val t0 = 0f
            val t1 = t0 + Vector2.distance(p0, p1).pow(alpha)
            val t2 = t1 + Vector2.distance(p1, p2).pow(alpha)
            val t3 = t2 + Vector2.distance(p2, p3).pow(alpha)

            val t12 = t1 - t2
            val t21 = t2 - t1

            val m1x = (1f - tension) * t21 * ((p0.x - p1.x) / (t0 - t1) - (p0.x - p2.x) / (t0 - t2) + (p1.x - p2.x) / t12)
            val m1y = (1f - tension) * t21 * ((p0.y - p1.y) / (t0 - t1) - (p0.y - p2.y) / (t0 - t2) + (p1.y - p2.y) / t12)
            val m2x = (1f - tension) * t21 * ((p1.x - p2.x) / t12 - (p1.x - p3.x) / (t1 - t3) + (p2.x - p3.x) / (t2 - t3))
            val m2y = (1f - tension) * t21 * ((p1.y - p2.y) / t12 - (p1.y - p3.y) / (t1 - t3) + (p2.y - p3.y) / (t2 - t3))

            val m1 = Vector2(m1x, m1y)
            val m2 = Vector2(m2x, m2y)

            a = (p1 - p2) * 2f + m1 + m2
            b = (p1 - p2) * -3f - m1 - m1 - m2
            c = m1
            d = p1
        }
    }
}
