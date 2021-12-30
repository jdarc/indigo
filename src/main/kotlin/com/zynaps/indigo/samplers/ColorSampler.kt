package com.zynaps.indigo.samplers

import com.zynaps.indigo.Argb
import com.zynaps.indigo.Sampler

class ColorSampler(private val argb: Int) : Sampler {

    constructor(red: Int, grn: Int, blue: Int, alpha: Int = 255) : this(Argb.pack(red, grn, blue, alpha))

    override fun sample(u: Float, v: Float) = argb

    companion object {
        val WHITE = ColorSampler(com.zynaps.indigo.Argb.WHITE)
    }
}
