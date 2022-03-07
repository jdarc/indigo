/*
 * Copyright © 2022. Jean d'Arc.
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

import com.zynaps.indigo.*
import com.zynaps.indigo.math.Transform
import com.zynaps.indigo.math.Vector2
import com.zynaps.indigo.samplers.CheckeredSampler
import java.awt.*

class Viewer3 : Canvas(), Viewer {
    private var painter: Painter
    private var bitmap: Bitmap
    private var viewport: Viewport

    init {
        val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
        val width = (gd.displayMode.width * 5) / 6
        val height = (gd.displayMode.height * 7) / 8

        font = Font("Space Mono", Font.PLAIN, 15)
        background = Color(0x54445b)
        size = Dimension(width, height)
        ignoreRepaint = true

        val hw = width * 0.125f
        val hh = height * 0.125f
        viewport = Viewport()
        viewport.set(-hw, hw, hh, -hh)
        bitmap = Bitmap((width * 1.0).toInt(), (height * 1.0).toInt())
        painter = Painter(bitmap)
    }

    override fun update(seconds: Double) {
        painter.viewport = viewport
        painter.clear(background.rgb)

            painter.transform = Transform.IDENTITY
            val c1 = Argb.OPAQUE or Argb.blend(0x5F8AA2, Argb.BLACK, 8)
            val c2 = Argb.OPAQUE or Argb.blend(0x5F8AA2, Argb.BLACK, 64)
            painter.sampler = CheckeredSampler(c1, c2, 25f)
            painter.rect(-250f, 250f, 500f, 500f)

        painter.render()
    }

    private fun drawPolygon(vertices: Array<Vector2>, uvs: Array<Vector2>, faces: Array<Face>) {
        painter.transform = Transform.translation(-300f, 0f) * Transform.scaling(100f, 100f)
        painter.poly(vertices, uvs, faces)
    }

    override fun render() {
        if (bufferStrategy == null) {
            createBufferStrategy(3)
            return
        }

        if (bufferStrategy.contentsLost()) return

        val g = bufferStrategy.drawGraphics as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED)
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
        g.font = font

        bitmap.draw(g, 0, 0, width, height)

        g.dispose()

        bufferStrategy.show()
    }

    override fun handleEvent(event: AWTEvent) {
        when (event.id) {
        }
    }
}
