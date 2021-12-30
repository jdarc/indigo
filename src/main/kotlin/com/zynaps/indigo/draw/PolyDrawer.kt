package com.zynaps.indigo.draw

import com.zynaps.indigo.Face
import com.zynaps.indigo.Painter
import com.zynaps.indigo.math.Vector2

internal object PolyDrawer {
    private val uvZero: (Array<Vector2>, Int) -> Vector2 = { _, _ -> Vector2.ZERO }
    private val uvArgs: (Array<Vector2>, Int) -> Vector2 = { arr, idx -> arr[idx] }

    fun draw(p: Painter, vertices: Array<Vector2>, uvs: Array<Vector2>, faces: Array<Face>) {
        val extractUv = if (uvs.isEmpty()) uvZero else uvArgs
        for (face in faces) {
            p.transform(p.a, vertices[face.v0], extractUv(uvs, face.vt0))
            p.transform(p.b, vertices[face.v1], extractUv(uvs, face.vt1))
            p.transform(p.c, vertices[face.v2], extractUv(uvs, face.vt2))
            p.draw(p.a, p.b, p.c)
        }
    }
//    fun draw(p: Painter, vertices: Array<Vector2>, uvs: Array<Vector2>, faces: Array<Face>) {
//        if (uvs.isEmpty()) {
//            for (face in faces) {
//                p.transform(p.a, vertices[face.v0], Vector2.ZERO)
//                p.transform(p.b, vertices[face.v1], Vector2.ZERO)
//                p.transform(p.c, vertices[face.v2], Vector2.ZERO)
//                p.draw(p.a, p.b, p.c)
//            }
//        } else {
//            for (face in faces) {
//                p.transform(p.a, vertices[face.v0], uvs[face.vt0])
//                p.transform(p.b, vertices[face.v1], uvs[face.vt1])
//                p.transform(p.c, vertices[face.v2], uvs[face.vt2])
//                p.draw(p.a, p.b, p.c)
//            }
//        }
//    }
}
