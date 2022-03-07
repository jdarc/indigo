package com.zynaps.indigo.geometry

import com.zynaps.indigo.math.Scalar
import com.zynaps.indigo.math.Transform
import com.zynaps.indigo.math.Vector2
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.*


internal object RaceTrack {

    fun generate(vertices: List<Vector2>): Pair<List<Vector2>, List<Vector2>> {
        var hull = Polygon.computeHull(vertices).map { vertices[it] }

        hull = jitter(hull, difficulty = 1f / 2f)
        hull = fixAngles(hull)

        val h = hull.toMutableList()
        for (i in 0 until 3) pushApart(h)

        val barycenter = Polygon.barycenter(hull)
        hull = hull.map { it - barycenter }

        return Pair(hull, CatmullRom.subdivide(hull, true))
    }

    // difficulty: the closer the value is to 0, the harder the track should be. Grows exponentially.
    private fun jitter(vertices: List<Vector2>, maxDisp: Float = 50f, difficulty: Float = 0.5f): List<Vector2> {
        val rng = ThreadLocalRandom.current()
        val rSet = mutableListOf<Vector2>()
        for (i in vertices.indices) {
            val dispLen = rng.nextFloat().pow(difficulty) * maxDisp
            val disp = Transform.rotation(rng.nextFloat(0f, 360f)) * Vector2(0f, dispLen)
            rSet.add(vertices[i])
            rSet.add((vertices[i] + vertices[(i + 1) % vertices.size]) / 2f + disp)
        }
        return rSet.toList()
    }

    private fun pushApart(dataSet: MutableList<Vector2>) {
        val dst = 50f
        val dst2 = dst * dst
        for (i in dataSet.indices) {
            for (j in i + 1 until dataSet.size) {
                if (Vector2.distanceSq(dataSet[i], dataSet[j]) < dst2) {
                    var h = dataSet[j] - dataSet[i]
                    h = Vector2.normalize(h) * (dst - h.length)
                    dataSet[j] = dataSet[j] + h
                    dataSet[i] = dataSet[i] - h
                }
            }
        }
    }

    private fun fixAngles(vertices: List<Vector2>): List<Vector2> {
        val dataSet = vertices.toMutableList()
        for (i in dataSet.indices) {
            val previous = if (i - 1 < 0) dataSet.size - 1 else i - 1
            val next = (i + 1) % dataSet.size
            var px = dataSet[i].x - dataSet[previous].x
            var py = dataSet[i].y - dataSet[previous].y
            val pl = sqrt((px * px + py * py))
            px /= pl
            py /= pl
            var nx = dataSet[i].x - dataSet[next].x
            var ny = dataSet[i].y - dataSet[next].y
            nx = -nx
            ny = -ny
            val nl = sqrt((nx * nx + ny * ny))
            nx /= nl
            ny /= nl
            val a = atan2(px * ny - py * nx, px * nx + py * ny)
            if (abs(Scalar.toDegrees(a)) <= 100f) continue
            val nA = Scalar.toRadians(100f * sign(a))
            val diff = nA - a
            val cos = cos(diff)
            val sin = sin(diff)
            var newX = nx * cos - ny * sin
            var newY = nx * sin + ny * cos
            newX *= nl
            newY *= nl
            dataSet[next] = Vector2(dataSet[i].x + newX, dataSet[i].y + newY)
        }
        return dataSet
    }
}
