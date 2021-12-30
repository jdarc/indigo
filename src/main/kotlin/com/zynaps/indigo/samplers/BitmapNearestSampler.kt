package com.zynaps.indigo.samplers

import com.zynaps.indigo.Bitmap
import com.zynaps.indigo.Sampler

class BitmapNearestSampler(private val bitmap: Bitmap) : Sampler {
    private val w = bitmap.width - 0.5f
    private val h = bitmap.height - 0.5f

    override fun sample(u: Float, v: Float): Int {
        return bitmap.data[(v * h).toInt() * bitmap.width + (u * w).toInt()]
    }
}
