package io.github.ryuryu_ymj.handwritingtest

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF

class Stroke {
  private val points = mutableListOf<PointF>()
  private val pointPaint =
      Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = true
      }

  fun begin(x: Float, y: Float) {
    points.add(PointF(x, y))
  }

  fun extend(x: Float, y: Float) {
    points.add(PointF(x, y))
  }

  fun end() {}

  fun draw(canvas: Canvas) {
    for (p in points) {
      canvas.drawPoint(p.x, p.y, pointPaint)
    }
  }
}
