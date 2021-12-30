package com.zynaps.indigo.geometry

import com.zynaps.indigo.math.Vector2

object CatmullRom {

    fun subdivide(points: List<Vector2>, subdivisions: Int, closed: Boolean = false) = if (closed) {
        subdivide(points.takeLast(3) + points, subdivisions).drop(subdivisions).dropLast(subdivisions)
    } else {
        subdivide(points, subdivisions)
    }

    private fun subdivide(points: List<Vector2>, subdivisions: Int) = (0 until points.size - 1).flatMap { i ->
        val p0 = points[(i - 1).coerceAtLeast(0)]
        val p1 = points[i]
        val p2 = points[i + 1]
        val p3 = points[(i + 2).coerceAtMost(points.size - 1)]
        val crs = CatmullRomSpline2D(p0, p1, p2, p3)
        (0..subdivisions).map { crs.q(it / subdivisions.toFloat()) }
    }

    private class CatmullRomSpline2D(p0: Vector2, p1: Vector2, p2: Vector2, p3: Vector2) {
        private val splineXValues = CatmullRomSpline(p0.x, p1.x, p2.x, p3.x)
        private val splineYValues = CatmullRomSpline(p0.y, p1.y, p2.y, p3.y)
        fun q(t: Float) = Vector2(splineXValues.q(t), splineYValues.q(t))
    }

    private class CatmullRomSpline(val p0: Float, val p1: Float, val p2: Float, val p3: Float) {
        fun q(t: Float): Float {
            val a = 2f * p1
            val b = (p2 - p0) * t
            val c = (2f * p0 - 5f * p1 + 4f * p2 - p3) * t * t
            val d = (3f * p1 - p0 - 3f * p2 + p3) * t * t * t
            return 0.5f * (a + b + c + d)
        }
    }
}
