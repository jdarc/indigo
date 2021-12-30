package com.zynaps.indigo.samplers

import com.zynaps.indigo.Argb
import com.zynaps.indigo.Sampler

@Suppress("unused")
class AlphaMask(private val src: Sampler, private val mask: Sampler) : Sampler {

    override fun sample(u: Float, v: Float): Int {
        val sample = src.sample(u, v)
        val alpha = Argb.alpha(sample) * Argb.alpha(mask.sample(u, v)) and 0xFF00 shl 16
        return sample and 0xFFFFFF or alpha
    }
}
