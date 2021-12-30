package com.zynaps.indigo.geometry

import com.zynaps.indigo.math.Vector2

internal object Triangulate {

    fun fill(vertices: List<Vector2>): IntArray {
        val bucket = vertices.mapIndexed { index, vertex -> Vec2(index, vertex.x, vertex.y) }.toMutableList()
        val clockwise = (bucket + bucket.first()).windowed(2).map { (a, b) -> (b.x - a.x) * (b.y + a.y) }.sum() >= 0

        var index = 0
        var offset = 0
        val indices = IntArray((bucket.size - 2) * 3)
        while (bucket.size > 2) {
            val v1 = bucket[(index + 0) % bucket.size]
            val v2 = bucket[(index + 1) % bucket.size]
            val v3 = bucket[(index + 2) % bucket.size]
            val cross = if (clockwise) sign(v1, v2, v3) else sign(v3, v2, v1)
            val valid = !bucket.filter { it != v1 && it != v2 && it != v3 }.any { triangleContains(it, v1, v2, v3) }
            if (cross <= 0 && valid) {
                indices[offset++] = v1.index
                indices[offset++] = v2.index
                indices[offset++] = v3.index
                bucket.remove(v2)
            } else {
                index++
            }
        }
        return indices
    }

    private fun sign(v1: Vec2, v2: Vec2, v3: Vec2) = (v2.x - v1.x) * (v3.y - v1.y) - (v2.y - v1.y) * (v3.x - v1.x)

    private fun triangleContains(point: Vec2, v1: Vec2, v2: Vec2, v3: Vec2): Boolean {
        val (d1, d2, d3) = arrayListOf(sign(point, v1, v2), sign(point, v2, v3), sign(point, v3, v1))
        return (d1 >= 0 && d2 >= 0 && d3 >= 0) || (d1 <= 0 && d2 <= 0 && d3 <= 0)
    }

    private data class Vec2(val index: Int, val x: Float, val y: Float)
}
