package io.github.ryuryu_ymj.handwritingtest

import androidx.lifecycle.ViewModel
import io.github.ryuryu_ymj.handwritingtest.smoother.DampedSmoother
import io.github.ryuryu_ymj.handwritingtest.smoother.TouchSmoother

class PaperViewModel : ViewModel() {
  val pen = Pen()
  private val smoother: TouchSmoother = DampedSmoother()
  val strokes = mutableListOf<Stroke>()

  fun beginTouch(x: Float, y: Float, pressure: Float, time: Long) {
    smoother.beginTouch(pen, x, y, pressure, time)
  }

  fun moveTouch(x: Float, y: Float, pressure: Float, time: Long) {
    smoother.moveTouch(pen, x, y, pressure, time)
  }

  fun endTouch() {
    smoother.endTouch(pen)
    pen.lastStroke?.let { strokes.add(it) }
    pen.end()
  }
}
