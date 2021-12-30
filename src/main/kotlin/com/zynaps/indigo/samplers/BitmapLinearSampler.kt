package com.zynaps.indigo.samplers

import com.zynaps.indigo.Argb.alpha
import com.zynaps.indigo.Argb.blu
import com.zynaps.indigo.Argb.grn
import com.zynaps.indigo.Argb.red
import com.zynaps.indigo.Bitmap
import com.zynaps.indigo.Sampler
import kotlin.math.log2

@Suppress("unused")
class BitmapLinearSampler(bitmap: Bitmap) : Sampler {
    private val siz8 = (bitmap.width).shl(8).toFloat() - 255f
    private val bits = log2(bitmap.width.toFloat()).toInt()
    private val mask = bitmap.width * bitmap.height - 1
    private val data = pack(bitmap)

    override fun sample(u: Float, v: Float): Int {
        val x = (u * siz8).toInt()
        val y = (v * siz8).toInt()
        val bx = x and 0xFF
        val by = y and 0xFF
        val m = y.ushr(8) shl bits or x.ushr(8)
        val p1 = data[mask and m]
        val p2 = data[mask and m + 1]
        val weight1 = (256 - bx) * (256 - by) ushr 8
        val weight4 = bx * by ushr 8
        val weight3 = by - weight4
        val weight2 = bx - weight4
        val n1 = (p1 shr 0 and MASK) * weight1
        val n3 = (p1 shr 8 and MASK) * weight3
        val n2 = (p2 shr 0 and MASK) * weight2
        val n4 = (p2 shr 8 and MASK) * weight4
        val sum1 = n1 + n2 ushr 8
        val sum2 = n3 + n4 ushr 8
        val sum = sum1 + sum2
        return (sum.ushr(24) and 0xFF00FF00 or (sum and 0xFF00FF)).toInt()
    }

    private fun pack(bitmap: Bitmap): LongArray {
        val data = bitmap.data.map {
            alpha(it).toLong().shl(48) or grn(it).toLong().shl(32) or red(it).toLong().shl(16) or blu(it).toLong()
        }
        return data.mapIndexed { index, value -> value or data[mask and index + bitmap.width].shl(8) }.toLongArray()
    }

    companion object {
        private const val MASK = 0b11111111000000001111111100000000111111110000000011111111
    }
}
