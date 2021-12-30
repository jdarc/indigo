package com.zynaps.indigo.tools

internal class DynamicArray<T>(initialCapacity: Int = 16) {
    private var elements = generate(initialCapacity.coerceAtLeast(16))
    private var index = 0

    val size get() = index

    operator fun get(index: Int) = elements[index]

    fun add(element: T): Int {
        if (index == elements.size) elements += generate(elements.size)
        elements[index] = element
        return index++
    }

    fun reset() {
        index = 0
    }

    @Suppress("UNCHECKED_CAST")
    private fun generate(capacity: Int) = arrayOfNulls<Any?>(capacity) as Array<T>
}
