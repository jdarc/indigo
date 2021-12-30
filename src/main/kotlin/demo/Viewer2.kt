package demo

import com.zynaps.indigo.geometry.CatmullRom
import com.zynaps.indigo.geometry.Polygon
import com.zynaps.indigo.geometry.RaceTrack
import com.zynaps.indigo.math.Transform
import com.zynaps.indigo.math.Vector2
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.geom.Ellipse2D
import java.awt.geom.GeneralPath
import kotlin.random.Random

class Viewer2 : Canvas(), Viewer {
    private val vertices = mutableListOf<Vector2>()
    private var indices = IntArray(0)
    private var hull = emptyList<Vector2>()
    private var splines = emptyList<Vector2>()
    private var raceTrack = emptyList<Vector2>()

    init {
        val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
        val width = (gd.displayMode.width * 5) / 6
        val height = (gd.displayMode.height * 7) / 8

        font = Font("Space Mono", Font.PLAIN, 15)
        background = Color(0x54445b)
        size = Dimension(width, height)
        ignoreRepaint = true

        regenerate()
    }

    override fun update(seconds: Double) = Unit

    override fun render() {
        if (bufferStrategy == null) {
            createBufferStrategy(3)
            return
        }

        if (bufferStrategy.contentsLost()) return

        val g = bufferStrategy.drawGraphics as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
        g.clearRect(0, 0, width, height)
        g.font = font
        g.stroke = BasicStroke(1.2f)
        g.translate(width / 2.0, height / 2.0)

        val path = GeneralPath()

//        g.paint = Color.RED
//        path.reset()
//        for (i in indices.indices step 3) {
//            val v0 = vertices[indices[i + 0]]
//            val v1 = vertices[indices[i + 1]]
//            val v2 = vertices[indices[i + 2]]
//            path.moveTo(v0.x, v0.y)
//            path.lineTo(v1.x, v1.y)
//            path.lineTo(v2.x, v2.y)
//            path.closePath()
//        }
//        g.draw(path)
//
//        g.paint = Color.YELLOW
//        path.reset()
//        vertices.forEach {
//            path.append(Ellipse2D.Float(it.x - 3, it.y - 3, 6f, 6f), false)
//        }
//        g.fill(path)

        g.paint = Color.GREEN
        path.reset()
        path.moveTo(hull.first().x, hull.first().y)
        hull.drop(1).forEach { v -> path.lineTo(v.x, v.y) }
        path.closePath()
        g.draw(path)
        g.paint = Color.YELLOW
        path.reset()
        hull.forEach { path.append(Ellipse2D.Float(it.x - 3, it.y - 3, 6f, 6f), false) }
        g.fill(path)

        g.paint = Color.ORANGE
        path.reset()
        raceTrack.forEachIndexed { i, v ->
            val x = v.x * 1.1f
            val y = v.y * 1.1f
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        g.draw(path)

        g.dispose()

        bufferStrategy.show()
    }

    override fun handleEvent(event: AWTEvent) {
        when (event.id) {
            KeyEvent.KEY_TYPED -> regenerate()
        }
    }

    private fun regenerate() {
        vertices.clear()
        val rng = Random(System.nanoTime())
        var ang = 0f
        val steps = 20
        val dir = if (rng.nextDouble() > 0.5) -1.0 else 1.0
        val arc = (360.0 / steps * dir).toFloat()
        for (i in 0 until steps) {
            vertices.add(Vector2(rng.nextDouble(20.0, 400.0).toFloat(), 0f) * Transform.rotation(ang))
            ang += arc
        }
        indices = Polygon.triangulate(vertices)
        hull = Polygon.computeHull(vertices).map { vertices[it] }
        splines = CatmullRom.subdivide(hull, 16, true)

        vertices.clear()
        for (i in 0 until 10) {
            val x = rng.nextDouble(-250.0, 250.0).toFloat()
            val y = rng.nextDouble(-250.0, 250.0).toFloat()
            vertices.add(Vector2(x, y))
        }

        hull = Polygon.computeHull(vertices).map { vertices[it] }
        hull = RaceTrack.jitter(hull)
        raceTrack = RaceTrack.generate(hull)
    }
}
