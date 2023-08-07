package io.github.ryuryu_ymj.handwritingtest.smoother

import io.github.ryuryu_ymj.handwritingtest.Pen

interface TouchSmoother {
  fun beginTouch(pen: Pen, x: Float, y: Float, pressure: Float, time: Long)
  fun moveTouch(pen: Pen, x: Float, y: Float, pressure: Float, time: Long)
  fun endTouch(pen: Pen)
}
