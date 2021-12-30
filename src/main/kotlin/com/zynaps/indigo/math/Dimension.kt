package com.zynaps.indigo.math

data class Dimension(val width: Float, val height: Float) {
    constructor(width: Number, height: Number) : this(width.toFloat(), height.toFloat())
}
