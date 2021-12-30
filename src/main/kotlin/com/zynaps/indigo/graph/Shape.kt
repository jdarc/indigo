package com.zynaps.indigo.graph

import com.zynaps.indigo.Face
import com.zynaps.indigo.Painter
import com.zynaps.indigo.math.Aabb
import com.zynaps.indigo.math.Vector2

class Shape(val vertices: Array<Vector2>, val uvs: Array<Vector2>, val faces: Array<Face>) {
    val bounds = if (vertices.isEmpty()) Aabb() else vertices.fold(Aabb()) { acc, v -> acc.aggregate(v) }

    fun draw(painter: Painter) {
        if (this == NONE) return
        painter.poly(vertices, uvs, faces)
    }

    companion object {
        val NONE = Shape(emptyArray(), emptyArray(), emptyArray())
    }
}
