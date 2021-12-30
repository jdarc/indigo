package com.zynaps.indigo.effects

import com.zynaps.indigo.Argb
import com.zynaps.indigo.Bitmap
import java.util.concurrent.Callable
import java.util.concurrent.ForkJoinPool
import kotlin.math.pow

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Gamma(private val src: Bitmap, private val dst: Bitmap = src) {

    private val tasks = generateTasks()

    private val lut = IntArray(256)

    var gamma = 1.2f
        set(value) {
            field = value.coerceIn(Float.MIN_VALUE, Float.MAX_VALUE)
            updateLut()
        }

    fun apply() {
        ForkJoinPool.commonPool().invokeAll(tasks)
    }

    private fun generateTasks() = (0 until src.height).map { y ->
        Callable {
            val offset = y * src.width
            for (x in offset until offset + src.width) {
                val pixel = src.data[x]
                val red = lut[Argb.red(pixel)]
                val grn = lut[Argb.grn(pixel)]
                val blu = lut[Argb.blu(pixel)]
                dst.data[x] = Argb.alpha(pixel).shl(24) or red.shl(16) or grn.shl(8) or (blu)
            }
        }
    }

    private fun updateLut() {
        for (i in lut.indices) {
            lut[i] = (255f * (i / 255f).pow(1f / gamma)).toInt()
        }
    }

    init {
        updateLut()
    }
}
