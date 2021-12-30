/*
 * Copyright (c) 2021 Jean d'Arc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package demo

import com.zynaps.indigo.Viewport
import com.zynaps.indigo.math.Dimension
import com.zynaps.indigo.math.Vector2
import java.awt.AWTEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

class Controller(private val viewport: Viewport, private val viewer: Viewer1) {
    private var last = Vector2(0f, 0f)
    private var mouse = Vector2(0f, 0f)
    private var view = Vector2(0f, 0f)
    private var zoom = 1f

    val x get() = mouse.x
    val y get() = mouse.y

    fun handleEvent(event: AWTEvent) {
        when (event.id) {
            MouseWheelEvent.MOUSE_WHEEL -> {
                val mouseEvent = event as MouseWheelEvent
                zoom += mouseEvent.wheelRotation.toFloat() / 50f
                mouse = Vector2(mouseEvent.x.toFloat(), mouseEvent.y.toFloat())
                last = toView(mouse)
            }
            MouseEvent.MOUSE_MOVED -> {
                val mouseEvent = event as MouseEvent
                mouse = Vector2(mouseEvent.x.toFloat(), mouseEvent.y.toFloat())
                last = toView(mouse)
            }
            MouseEvent.MOUSE_DRAGGED -> {
                val mouseEvent = event as MouseEvent
                mouse = Vector2(mouseEvent.x.toFloat(), mouseEvent.y.toFloat())
                view = toView(mouse) - last
            }
        }
    }

    fun update() {
        val center = viewport.center
        val l = view.x - (center.x - zoom * (center.x - viewport.left))
        val r = view.x - (center.x - zoom * (center.x - viewport.right))
        val t = view.y - (center.y - zoom * (center.y - viewport.top))
        val b = view.y - (center.y - zoom * (center.y - viewport.bottom))
        viewport.set(-l, -r, -t, -b)

        view = Vector2.ZERO
        zoom = 1f
    }

    private fun toView(v: Vector2): Vector2 {
        val screen = Dimension(viewer.width, viewer.height)
        return v * viewport.screenToViewport(screen)
    }
}
