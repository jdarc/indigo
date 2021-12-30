package com.zynaps.indigo.geometry

import com.zynaps.indigo.math.Vector2

internal object ConvexHull {

    fun compute(vertices: List<Vector2>): IntArray {
        var rightMost = 0
        var highestXCoord = Float.NEGATIVE_INFINITY
        vertices.forEachIndexed { index, vertex ->
            if (vertex.x > highestXCoord || vertex.x == highestXCoord && vertex.y < vertices[rightMost].y) {
                highestXCoord = vertex.x
                rightMost = index
            }
        }

        var outCount = 0
        var indexHull = rightMost
        val hull = IntArray(vertices.size)
        do {
            hull[outCount] = indexHull
            var nextHullIndex = 0
            val poc = vertices[hull[outCount]]
            vertices.forEachIndexed { index, v2 ->
                if (nextHullIndex != indexHull) {
                    val v1 = vertices[nextHullIndex]
                    val e1x = v1.x - poc.x
                    val e1y = v1.y - poc.y
                    val e2x = v2.x - poc.x
                    val e2y = v2.y - poc.y
                    val c = e1x * e2y - e1y * e2x
                    if (c < 0f || c == 0f && e2x * e2x + e2y * e2y > e1x * e1x + e1y * e1y) {
                        nextHullIndex = index
                    }
                } else {
                    nextHullIndex = index
                }
            }
            outCount++
            indexHull = nextHullIndex
        } while (indexHull != rightMost)

        return Array(outCount) { hull[it] }.toIntArray()
    }
}
