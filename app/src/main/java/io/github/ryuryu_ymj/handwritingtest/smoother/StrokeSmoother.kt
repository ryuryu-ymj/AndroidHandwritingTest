package io.github.ryuryu_ymj.handwritingtest.smoother

import io.github.ryuryu_ymj.handwritingtest.Stroke

interface StrokeSmoother {
  fun beginTouch(stroke: Stroke, x: Float, y: Float, t: Long)
  fun moveTouch(stroke: Stroke, x: Float, y: Float, t: Long)
  fun endTouch(stroke: Stroke)
}
