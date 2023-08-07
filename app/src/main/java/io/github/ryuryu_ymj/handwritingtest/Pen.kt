package io.github.ryuryu_ymj.handwritingtest

import android.graphics.Canvas

class Pen(private val strokeWidth: Float = 10f) {
  var lastStroke: Stroke? = null
    private set

  private fun pressureToRadius(pressure: Float) = strokeWidth * (0.3f + pressure * 0.4f) / 2

  fun draw(canvas: Canvas) {
    lastStroke?.draw(canvas)
  }

  fun begin(x: Float, y: Float, pressure: Float) {
    lastStroke = Stroke().also { it.begin(x, y, pressureToRadius(pressure)) }
  }

  fun move(x: Float, y: Float, pressure: Float) {
    lastStroke?.extend(x, y, pressureToRadius(pressure))
  }

  fun end() {
    lastStroke?.end()
    lastStroke = null
  }
}
