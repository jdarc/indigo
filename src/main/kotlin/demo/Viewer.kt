package demo

import java.awt.AWTEvent

interface Viewer {
    fun update(seconds: Double)
    fun render()
    fun handleEvent(event: AWTEvent)
}
