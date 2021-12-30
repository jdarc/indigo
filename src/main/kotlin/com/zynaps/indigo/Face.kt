package com.zynaps.indigo

class Face(a: Int = 0, b: Int = 0, c: Int = 0) {

    var v0 = 0; private set
    var v1 = 0; private set
    var v2 = 0; private set
    
    var vt0 = 0; private set
    var vt1 = 0; private set
    var vt2 = 0; private set

    fun vertices(a: Int, b: Int, c: Int): Face {
        v0 = a.coerceAtLeast(0)
        v1 = b.coerceAtLeast(0)
        v2 = c.coerceAtLeast(0)
        return this
    }

    fun uvs(a: Int, b: Int, c: Int): Face {
        vt0 = a.coerceAtLeast(0)
        vt1 = b.coerceAtLeast(0)
        vt2 = c.coerceAtLeast(0)
        return this
    }

    init {
        vertices(a, b, c)
    }
}
