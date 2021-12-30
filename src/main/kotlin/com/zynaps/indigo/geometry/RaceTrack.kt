package com.zynaps.indigo.geometry

import com.zynaps.indigo.math.Transform
import com.zynaps.indigo.math.Vector2
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.pow


internal object RaceTrack {

    fun jitter(vertices: List<Vector2>) : List<Vector2> {
        val rng = ThreadLocalRandom.current()
        val rSet = arrayOfNulls<Vector2>(vertices.size * 2)
        val difficulty = 0.25f //the closer the value is to 0, the harder the track should be. Grows exponentially.
        val maxDisp = 50f // Again, this may change to fit your units.
        for (i in vertices.indices) {
            val dispLen = rng.nextDouble(0.0, 1.0).pow(difficulty.toDouble()).toFloat() * maxDisp
            val disp = Vector2(0f, 1f) * Transform.rotation(rng.nextFloat(0.0f, 1.0f) * 360f) * dispLen
            rSet[i * 2 + 0] = vertices[i]
            rSet[i * 2 + 1] = (vertices[i] + vertices[(i + 1) % vertices.size]) / 2f + disp
        }
        return rSet.filterNotNull()
    }

    fun generate(vertices: List<Vector2>, subdivisions: Int = 16): List<Vector2> {
        return CatmullRom.subdivide(vertices, subdivisions, true)
    }
}
