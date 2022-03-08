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

import java.awt.AWTEvent
import java.awt.BorderLayout
import java.awt.Frame
import java.awt.Toolkit
import java.awt.event.AWTEventListener
import java.awt.event.KeyEvent
import java.awt.event.WindowEvent
import javax.swing.ImageIcon
import javax.swing.Timer
import kotlin.system.exitProcess

class MainFrame : Frame("Indigo"), AWTEventListener {
    private val timer: Timer
    private var tock: Long = 0
    private val viewer: Viewer

    init {
        layout = BorderLayout()
        viewer = Viewer2()
        ignoreRepaint = true
        background = viewer.background
        iconImage = ImageIcon(javaClass.getResource("/icon.png")).image
        add(viewer, BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(null)
        isResizable = false

        val mask = AWTEvent.MOUSE_WHEEL_EVENT_MASK or AWTEvent.MOUSE_EVENT_MASK or AWTEvent.MOUSE_MOTION_EVENT_MASK or AWTEvent.KEY_EVENT_MASK or AWTEvent.WINDOW_EVENT_MASK
        Toolkit.getDefaultToolkit().addAWTEventListener(this, mask)

        timer = Timer(60) {
            val elapsed = (System.nanoTime() - tock) / 1000000.0
            tock = System.nanoTime()
            viewer.update(elapsed / 1000.0)
            viewer.render()
        }
    }

    fun start() {
        isVisible = true
        if (!timer.isRunning) {
            tock = System.nanoTime()
            timer.restart()
        }
    }

    fun stop() {
        timer.stop()
        isVisible = false
        dispose()
        exitProcess(0)
    }

    override fun eventDispatched(event: AWTEvent) = when {
        event.id == WindowEvent.WINDOW_CLOSING -> stop()
        event is KeyEvent -> {
            when (event.keyCode) {
                KeyEvent.VK_ESCAPE -> stop()
            }
            viewer.handleEvent(event)
        }
        else -> viewer.handleEvent(event)
    }
}
