package com.zynaps.indigo

import com.zynaps.indigo.math.Scalar.ceil
import kotlin.math.max

internal class Edge {
    var y1 = 0
    var y2 = 0

    var x = 0f
    var xStep = 0f

    var tu = 0f
    var tuStep = 0f

    var tv = 0f
    var tvStep = 0f

    fun configure(g: Gradients, a: Point, b: Point): Int {
        y1 = max(0, ceil(a.y))
        val height = ceil(b.y) - y1

        if (height > 0) {
            val yPreStep = y1 - a.y
            xStep = (b.x - a.x) / (b.y - a.y)
            x = yPreStep * xStep + a.x

            tu = yPreStep * g.udy + (x - a.x) * g.udx + a.u
            tuStep = xStep * g.udx + g.udy

            tv = yPreStep * g.vdy + (x - a.x) * g.vdx + a.v
            tvStep = xStep * g.vdx + g.vdy

            y2 = y1 + height
        }

        return height
    }
}
