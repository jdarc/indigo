package com.zynaps.indigo

internal class Gradients {
    var udx = 0f
    var udy = 0f
    var vdx = 0f
    var vdy = 0f

    fun configure(a: Point, c: Point, b: Point) {
        val acx = a.x - c.x
        val bcx = b.x - c.x
        val acy = a.y - c.y
        val bcy = b.y - c.y
        val tu0 = a.u - c.u
        val tu1 = b.u - c.u
        val tv0 = a.v - c.v
        val tv1 = b.v - c.v
        val oneOverDX = 1f / (bcx * acy - acx * bcy)
        udx = (tu1 * acy - tu0 * bcy) * oneOverDX
        udy = (tu0 * bcx - tu1 * acx) * oneOverDX
        vdx = (tv1 * acy - tv0 * bcy) * oneOverDX
        vdy = (tv0 * bcx - tv1 * acx) * oneOverDX
    }
}
