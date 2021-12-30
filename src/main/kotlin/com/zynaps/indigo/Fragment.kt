package com.zynaps.indigo

import com.zynaps.indigo.samplers.ColorSampler

internal class Fragment {
    var sampler: Sampler = ColorSampler.WHITE

    var y = 0

    var udx = 0f
    var vdx = 0f

    private var leftX = 0f
    private var leftXStep = 0f

    private var rightX = 0f
    private var rightXStep = 0f

    private var u = 0f
    private var uStep = 0f

    private var v = 0f
    private var vStep = 0f

    fun configure(m: Sampler, g: Gradients, left: Edge, right: Edge): Fragment {
        sampler = m

        udx = g.udx
        vdx = g.vdx

        y = right.y1

        rightX = right.x
        rightXStep = right.xStep

        val delta = (right.y1 - left.y1).toFloat()

        leftX = left.x + left.xStep * delta
        leftXStep = left.xStep

        u = left.tu + left.tuStep * delta
        uStep = left.tuStep

        v = left.tv + left.tvStep * delta
        vStep = left.tvStep

        return this
    }

    fun left(delta: Float) = leftX + leftXStep * delta
    fun right(delta: Float) = rightX + rightXStep * delta

    fun u(delta: Float) = u + uStep * delta
    fun v(delta: Float) = v + vStep * delta
}
