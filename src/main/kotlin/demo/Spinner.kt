package demo

import com.zynaps.indigo.graph.Component
import com.zynaps.indigo.graph.Node

class Spinner(private var rotation: Float, private val delta: Float) : Component {

    override fun update(node: Node, seconds: Float) {
        rotation += delta * seconds
        node.rotation = rotation
    }
}
