package com.zynaps.indigo.tools

import java.util.function.Supplier

internal class DynamicPool<T>(initialCapacity: Int = 8, private val generator: Supplier<T>) {
    private var elements = generate(generator, initialCapacity.coerceAtLeast(8))
    private var index = 0

    fun next(): T {
        if (index == elements.size) {
            elements += generate(generator, elements.size shr 1)
            println(elements.size)
        }
        return elements[index++]
    }

    fun reset() {
        index = 0
    }

    @Suppress("UNCHECKED_CAST")
    private fun generate(generator: Supplier<T>, capacity: Int): Array<T> {
        val dest = arrayOfNulls<Any?>(capacity) as Array<T>
        (0 until capacity).mapIndexed { index, _ -> dest[index] = generator.get() }
        return dest
    }
}
