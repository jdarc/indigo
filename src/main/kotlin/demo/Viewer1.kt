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

import com.zynaps.indigo.*
import com.zynaps.indigo.effects.Blur
import com.zynaps.indigo.effects.Gamma
import com.zynaps.indigo.graph.Node
import com.zynaps.indigo.graph.Shape
import com.zynaps.indigo.math.Transform
import com.zynaps.indigo.math.Vector2
import com.zynaps.indigo.samplers.BitmapLinearSampler
import com.zynaps.indigo.samplers.CheckeredSampler
import com.zynaps.indigo.samplers.ColorSampler
import java.awt.*
import java.io.File
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

class Viewer1 : Canvas(), Viewer {
    private val puzzleShape = loadPoly()
    private val squareShape = buildSquareShape()
    private val textures = loadTextures()
    private val samplers = textures.map { BitmapLinearSampler(it) }
    private val nodes = generateNodes(squareShape)
    private val nodeTree = buildNodeTree()
    private val nodeShapes = buildNodeShapeTree()
    private val bitmap: Bitmap
    private val viewport: Viewport
    private val painter: Painter
    private val controller: Controller
    private val fpsCounter: FPSCounter

    private val gamma: Gamma
    private val blur: Blur

    init {
        val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
        val width = (gd.displayMode.width * 5) / 6
        val height = (gd.displayMode.height * 7) / 8

        font = Font("Space Mono", Font.PLAIN, 15)

        background = Color(0x54445b)
        size = Dimension(width, height)
        ignoreRepaint = true

        fpsCounter = FPSCounter()

        val hw = width * 0.125f
        val hh = height * 0.125f
        viewport = Viewport()
        viewport.set(-hw, hw, hh, -hh)
        controller = Controller(viewport, this)
        bitmap = Bitmap((width * 1.0).toInt(), (height * 1.0).toInt())
        painter = Painter(bitmap)

        gamma = Gamma(bitmap)
        blur = Blur(bitmap)
    }

    override fun update(seconds: Double) {
        if (seconds <= 0) return

        fpsCounter.add(seconds)
        controller.update()

        painter.viewport = viewport
        painter.clear(background.rgb)

        drawRect()

        drawNodes(seconds.toFloat())
//        drawLines()
//        drawLine(210f, -210f, 420f, -200f)
//        drawPolygon(puzzleShape.vertices, puzzleShape.uvs, puzzleShape.faces)
//        drawShapes(puzzleShape)
//        drawNodeShapes(seconds.toFloat())
//        drawNodeTree(seconds.toFloat())

        painter.render()

        doEffects(false)
    }

    private fun drawNodeShapes(seconds: Float) {
        nodeShapes.update(seconds)
        nodeShapes.draw(painter)
    }

    private fun drawNodeTree(seconds: Float) {
        nodeTree.update(seconds)
        nodeTree.draw(painter)
    }

    private fun drawPolygon(vertices: Array<Vector2>, uvs: Array<Vector2>, faces: Array<Face>) {
        painter.transform = Transform.translation(-300f, 0f) * Transform.scaling(100f, 100f)
        painter.sampler = samplers[0]
        painter.poly(vertices, uvs, faces)
    }

    private fun drawShapes(shape: Shape) {
        val ticks = System.nanoTime() / 1000000000f
        val scale = Vector2(5f, 5f)
        painter.sampler = samplers[0]
        for (y in -250..250 step 10) {
            for (x in -250..250 step 10) {
                painter.transform = Transform.create(Vector2(x.toFloat(), y.toFloat()), 30 * ticks, scale)
                shape.draw(painter)
            }
        }
    }

    private fun drawRect() {
        painter.transform = Transform.IDENTITY
        val c1 = Argb.OPAQUE or Argb.blend(0x5F8AA2, Argb.BLACK, 8)
        val c2 = Argb.OPAQUE or Argb.blend(0x5F8AA2, Argb.BLACK, 64)
        painter.sampler = CheckeredSampler(c1, c2, 25f)
        painter.rect(-250f, 250f, 500f, 500f)
    }

    private fun drawNodes(seconds: Float) {
        nodes.update(seconds)
        nodes.draw(painter)
    }

    private fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, width: Float = 1f) {
        painter.transform = Transform.IDENTITY
        painter.sampler = ColorSampler(0xFF, 0x45, 0xAB)
        painter.line(x1, y1, x2, y2, width)
    }

    private fun drawLines() {
        painter.transform = Transform.IDENTITY
        val rng = Random(System.nanoTime())
        for (i in 0 until 100) {
            painter.sampler = ColorSampler(255.shl(24) or rng.nextInt(0xFFFFFF))
            val x0 = rng.nextDouble(-200.0, 200.0).toFloat()
            val y0 = rng.nextDouble(-200.0, 200.0).toFloat()
            val x1 = rng.nextDouble(-200.0, 200.0).toFloat()
            val y1 = rng.nextDouble(-200.0, 200.0).toFloat()
            painter.line(x0, y0, x1, y1, 5f)
        }
    }

    private fun doEffects(process: Boolean) {
        if (!process) return
        blur.apply()
        gamma.apply()
    }

    override fun render() {
        if (bufferStrategy == null) {
            createBufferStrategy(3)
            return
        }

        if (bufferStrategy.contentsLost()) return

        val g = bufferStrategy.drawGraphics as Graphics2D
        g.font = font

        bitmap.draw(g, 0, 0, width, height)

        g.paint = Color(0.0F, 0.0F, 0.0F, 0.5F)
        g.fillRect(0, 0, 300, 116)
        g.paint = Color.YELLOW
        g.drawString("Frames/Sec: ${fpsCounter.average()}", 20, 20)

        val total = humanReadableByteCount(Runtime.getRuntime().totalMemory())
        val used = humanReadableByteCount(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
        g.drawString("Memory: $used of ${total}M", 20, 48)

        val (x, y) = Vector2(controller.x, controller.y) * viewport.screenToViewport(bitmap.size)
        g.drawString("X: $x", 20, 78)
        g.drawString("Y: $y", 20, 104)

        g.dispose()

        bufferStrategy.show()
    }

    override fun handleEvent(event: AWTEvent) = controller.handleEvent(event)

    private fun humanReadableByteCount(bytes: Long) = "${bytes / 1048576}"

    private fun buildSquareShape(): Shape {
        val v = arrayOf(Vector2(-0.5f, 0.5f), Vector2(-0.5f, -0.5f), Vector2(0.5f, -0.5f), Vector2(0.5f, 0.5f))
        val vt = arrayOf(Vector2(0f, 0f), Vector2(0f, 1f), Vector2(1f, 1f), Vector2(1f, 0f))
        return Shape(v, vt, arrayOf(Face(0, 1, 2).uvs(0, 1, 2), Face(2, 3, 0).uvs(2, 3, 0)))
    }

    private fun generateNodes(shape: Shape): Node {
        val root = Node()
        val rng = Random(1973)
        root.nodes.addAll((0 until 500).map {
            val node = Node(shape)
            node.sampler = samplers[rng.nextInt(samplers.size)]
            node.position = Vector2(rng.nextDouble(-140.0, 140.0).toFloat(), rng.nextDouble(-80.0, 80.0).toFloat())
            node.scale = Vector2.ONE * rng.nextDouble(5.0, 30.0).toFloat()
            node.components.add(Spinner(rng.nextDouble(1.0, 180.0).toFloat(), rng.nextDouble(1.0, 180.0).toFloat()))
            node
        })
        return root
    }

    private fun loadTextures(): Array<Bitmap> {
        val images = "grinch.png,chocolate.png,doggy.png,dog.png,hippo.png,positive.png,serum.png".split(",")
        val loader = Loader(File(javaClass.classLoader.getResource("images").toURI()).path)
        return images.map { Bitmap.convert(loader.loadImage(it)) }.toTypedArray()
    }

    private fun buildNodeShapeTree(): Node {
        val root = Node()

        val scale = Vector2(5f, 5f)
        for (y in -250..250 step 10) {
            for (x in -250..250 step 10) {
                val child = Node(puzzleShape)
                child.sampler = samplers[0]
                child.scale = scale
                child.position = Vector2(x.toFloat(), y.toFloat())
                child.components.add(Spinner(ThreadLocalRandom.current().nextFloat(0f, 360f), 30f))
                root.nodes.add(child)
            }
        }
        return root
    }

    private fun buildNodeTree(): Node {
        val root = Node()
        root.scale = Vector2(100f, 100f)
        root.position = Vector2(150f, -50f)

        val child = Node(squareShape)
        child.sampler = samplers[0]
        child.scale = Vector2(1f, 1f)
        child.rotation = 45f
        child.position = Vector2(0f, 0f)
        child.components.add(Spinner(child.rotation, -15f))

        val subchild0 = Node(puzzleShape)
        subchild0.sampler = samplers[1]
        subchild0.position = Vector2(-0.5f, 0.5f)
        subchild0.scale = Vector2(0.25f, 0.25f)
        subchild0.rotation = 45f
        subchild0.components.add(Spinner(subchild0.rotation, 220f))

        val subchild1 = Node(puzzleShape)
        subchild1.sampler = samplers[2]
        subchild1.position = Vector2(0.5f, 0.5f)
        subchild1.scale = Vector2(0.25f, 0.25f)
        subchild1.rotation = -85f
        subchild1.components.add(Spinner(subchild1.rotation, 100f))

        val subchild2 = Node(puzzleShape)
        subchild2.sampler = samplers[4]
        subchild2.position = Vector2(0.5f, -0.5f)
        subchild2.scale = Vector2(0.25f, 0.25f)
        subchild2.rotation = 5f
        subchild2.components.add(Spinner(subchild2.rotation, 40f))

        val subchild3 = Node(puzzleShape)
        subchild3.position = Vector2(-0.5f, -0.5f)
        subchild3.sampler = samplers[3]
        subchild3.scale = Vector2(0.25f, 0.25f)
        subchild3.rotation = 145f
        subchild3.components.add(Spinner(subchild3.rotation, -30f))

        child.nodes.addAll(listOf(subchild0, subchild1, subchild2, subchild3))

        root.nodes.add(child)
        return root
    }

    private fun loadPoly(): Shape {
        val loader = Loader(File(javaClass.classLoader.getResource("polygons").toURI()).path)
        val data = loader.loadText("poly.obj")
        val vertices = mutableListOf<Vector2>()
        val uvs = mutableListOf<Vector2>()
        val indices = mutableListOf<Face>()
        data.split(System.lineSeparator()).filter { it.isNotEmpty() }.forEach {
            val split = it.split(" ")
            val key = split[0]
            if (key == "v") {
                vertices.add(Vector2(-split[1].toFloat(), -split[2].toFloat()))
            }
            if (key == "vt") {
                uvs.add(Vector2(split[1].toFloat(), split[2].toFloat()))
            }
            if (key == "f") {
                val f1 = split[1].split("/")
                val f2 = split[2].split("/")
                val f3 = split[3].split("/")
                val i0 = f1[0].toInt() - 1
                val i1 = f2[0].toInt() - 1
                val i2 = f3[0].toInt() - 1
                val t0 = f1[1].toInt() - 1
                val t1 = f2[1].toInt() - 1
                val t2 = f3[1].toInt() - 1
                indices.add(Face(i0, i1, i2).uvs(t0, t1, t2))
            }
        }
        return Shape(vertices.toTypedArray(), uvs.toTypedArray(), indices.toTypedArray())
    }
}
