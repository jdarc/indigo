package com.zynaps.indigo.draw

import com.zynaps.indigo.Painter
import kotlin.math.sqrt

internal object LineDrawer {

    fun draw(p: Painter, x1: Float, y1: Float, x2: Float, y2: Float, width: Float = 1f) {
        val x = y1 - y2
        val y = x2 - x1
        val l = 0.5f * width / sqrt(x * x + y * y)
        val dx = x * l
        val dy = y * l
        val v0x = x1 + dx
        val v0y = y1 + dy
        val v1x = x1 - dx
        val v1y = y1 - dy
        val v2x = x2 - dx
        val v2y = y2 - dy
        val v3x = x2 + dx
        val v3y = y2 + dy

        p.transform(p.a, v0x, v0y, 0f, 0f)
        p.transform(p.b, v1x, v1y, 0f, 1f)
        p.transform(p.c, v2x, v2y, 1f, 1f)
        p.draw(p.a, p.b, p.c)

        p.transform(p.b, v3x, v3y, 1f, 0f)
        p.draw(p.c, p.b, p.a)
    }
}
