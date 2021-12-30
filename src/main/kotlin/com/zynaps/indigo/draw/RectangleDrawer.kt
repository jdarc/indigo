package com.zynaps.indigo.draw

import com.zynaps.indigo.Painter

internal object RectangleDrawer {

    fun draw(p: Painter, x: Float, y: Float, width: Float, height: Float) {
        val w = x + width
        val h = y - height

        p.transform(p.a, x, y, 0f, 0f)
        p.transform(p.b, x, h, 0f, 1f)
        p.transform(p.c, w, h, 1f, 1f)
        p.draw(p.a, p.b, p.c)

        p.transform(p.b, w, y, 1f, 0f)
        p.draw(p.c, p.b, p.a)
    }
}
