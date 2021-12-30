package com.zynaps.indigo.samplers

import com.zynaps.indigo.Sampler

@Suppress("unused")
class CheckeredSampler(private val c1: Sampler, private val c2: Sampler, scale: Float = 16f) : Sampler {

    private val scale = scale.coerceAtLeast(1f)

    constructor(c1: Int, c2: Int, scale: Float = 16f) : this(ColorSampler(c1), ColorSampler(c2), scale)

    override fun sample(u: Float, v: Float): Int {
        val i = (u * scale).toInt() + (v * scale).toInt() and 1
        return i * c1.sample(u, v) or (1 - i) * c2.sample(u, v)
    }
}
