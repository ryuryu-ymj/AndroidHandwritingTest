package io.github.ryuryu_ymj.handwritingtest

import androidx.lifecycle.ViewModel
import io.github.ryuryu_ymj.handwritingtest.smoother.DampedSmoother
import io.github.ryuryu_ymj.handwritingtest.smoother.TouchSmoother

class PaperViewModel : ViewModel() {
  private val pen = Pen()
  private val smoother: TouchSmoother = DampedSmoother()
  val strokes = mutableListOf<Stroke>()

  fun onStylusDown(x: Float, y: Float, pressure: Float, time: Long) {
    val stroke = Stroke()
    strokes.add(stroke)
    smoother.beginStroke(stroke, x, y, pressure, time, pen)
  }

  fun onStylusMove(x: Float, y: Float, pressure: Float, time: Long) {
    smoother.extendStroke(strokes.last(), x, y, pressure, time, pen)
  }

  fun onStylusEnd() {
    smoother.endStroke(strokes.last(), pen)
  }
}
