package com.zynaps.indigo

import com.zynaps.indigo.draw.LineDrawer
import com.zynaps.indigo.draw.PolyDrawer
import com.zynaps.indigo.draw.RectangleDrawer
import com.zynaps.indigo.math.Transform
import com.zynaps.indigo.math.Vector2

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Painter(private val bitmap: Bitmap) {
    private val screen = Transform.scaling(1f, -1f) * Transform.translation(0.5f * bitmap.width, -0.5f * bitmap.height)
    private val device = Device(bitmap)
    private var view = Transform.IDENTITY
    private var tx = Transform.IDENTITY

    internal val a = Point()
    internal val b = Point()
    internal val c = Point()

    var sampler
        get() = device.sampler
        set(value) {
            device.sampler = value
        }

    var transform = Transform.IDENTITY
        set(value) {
            field = value
            tx = screen * view * transform
        }

    var viewport = Viewport()
        set(value) {
            field = value
            view = value.viewportToScreen(bitmap.size)
            tx = screen * view * transform
        }

    fun clear(color: Int = Argb.WHITE) {
        device.clear(color)
    }

    fun line(x1: Float, y1: Float, x2: Float, y2: Float, width: Float = 1f) {
        device.next()
        LineDrawer.draw(this, x1, y1, x2, y2, width)
    }

    fun rect(x: Float, y: Float, width: Float, height: Float) {
        device.next()
        RectangleDrawer.draw(this, x, y, width, height)
    }

    fun poly(vertices: Array<Vector2>, uvs: Array<Vector2>, faces: Array<Face>) {
        device.next()
        PolyDrawer.draw(this, vertices, uvs, faces)
    }

    fun render() {
        device.render()
    }

    internal fun draw(a: Point, b: Point, c: Point) {
        device.rasterise(a, b, c)
    }

    internal fun transform(p: Point, v: Vector2, vt: Vector2) {
        transform(p, v.x, v.y, vt.x, vt.y)
    }

    internal fun transform(p: Point, x: Float, y: Float, u: Float, v: Float) {
        p.x = tx.m00 * x + tx.m01 * y + tx.m02
        p.y = tx.m10 * x + tx.m11 * y + tx.m12
        p.u = u
        p.v = v
    }
}
