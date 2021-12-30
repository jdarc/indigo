package com.zynaps.indigo.graph

import com.zynaps.indigo.Painter
import com.zynaps.indigo.Sampler
import com.zynaps.indigo.math.Aabb
import com.zynaps.indigo.math.Transform
import com.zynaps.indigo.math.Vector2
import com.zynaps.indigo.samplers.ColorSampler

class Node(val shape: Shape = Shape.NONE) {
    val nodes = mutableListOf<Node>()
    val components = mutableListOf<Component>()

    var sampler: Sampler = ColorSampler.WHITE

    var position = Vector2.ZERO
    var rotation = 0f
    var scale = Vector2.ONE

    private var transform = Transform.IDENTITY

    private var localBounds = Aabb()

    fun update(seconds: Float) {
        nodes.forEach { node -> node.update(seconds) }
        components.forEach { component -> component.update(this, seconds) }
    }

    fun draw(painter: Painter) {
        computeBounds(Transform.IDENTITY)
        drawNodes(painter)
    }

    private fun computeBounds(transform: Transform): Aabb {
        this.transform = transform * Transform.create(position, rotation, scale)
        localBounds.reset().aggregate(shape.bounds * this.transform)
        nodes.forEach { localBounds.aggregate(it.computeBounds(this.transform)) }
        return localBounds
    }

    private fun drawNodes(painter: Painter) {
        if (painter.viewport.contains(localBounds.minimum, localBounds.maximum)) {
            painter.sampler = sampler
            painter.transform = transform
            shape.draw(painter)
            nodes.forEach { it.drawNodes(painter) }
        }
    }
}
