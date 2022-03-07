package com.zynaps.indigo.geometry

import com.zynaps.indigo.math.Vector2

object Polygon {

    fun barycenter(vertices: List<Vector2>) = vertices.fold(Vector2.ZERO) { acc, v -> (acc + v) } / vertices.size.toFloat()

    fun triangulate(vertices: List<Vector2>) = Triangulate.fill(vertices)

    fun computeHull(vertices: List<Vector2>) = ConvexHull.compute(vertices)

    fun raceTrack(vertices: List<Vector2>) = RaceTrack.generate(vertices)
}
