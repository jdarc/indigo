package com.zynaps.indigo

import kotlin.math.max
import kotlin.math.min

@Suppress("MemberVisibilityCanBePrivate")
object Argb {
    const val OPAQUE = 255 shl 24

    const val WHITE = OPAQUE or 0xFFFFFF

    const val BLACK = OPAQUE or 0x000000

    fun alpha(argb: Int) = 255 and argb.ushr(24)

    fun red(argb: Int) = 255 and argb.ushr(16)

    fun grn(argb: Int) = 255 and argb.ushr(8)

    fun blu(argb: Int) = 255 and argb

    fun pack(red: Int, green: Int, blue: Int, alpha: Int): Int {
        return clamp(alpha).shl(24) or clamp(red)
            .shl(16) or clamp(green)
            .shl(8) or clamp(blue)
    }

    fun blend(x: Int, y: Int, a: Int) = when {
        a < 1 -> x
        a > 254 -> y
        else -> {
            val n = 255 - a
            val blu = (n * blu(x) ushr 8) + (a * blu(y) ushr 8)
            val grn = (n * grn(x) ushr 8) + (a * grn(y) ushr 8)
            val red = (n * red(x) ushr 8) + (a * red(y) ushr 8)
            val alp = (n * alpha(x) ushr 8) + (a * alpha(y) ushr 8)
            alp.shl(24) or red.shl(16) or grn.shl(8) or blu
        }
    }

    private fun clamp(v: Int) = max(0, min(255, v))
}
