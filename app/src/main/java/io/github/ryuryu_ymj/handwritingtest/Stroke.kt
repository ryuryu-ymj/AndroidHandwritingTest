package io.github.ryuryu_ymj.handwritingtest

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

class Stroke {
  private val path = Path()
  private val paint =
      Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = true
      }

  fun begin(x: Float, y: Float) {
    path.moveTo(x, y)
  }

  fun extend(x: Float, y: Float) {
    path.lineTo(x, y)
  }

  fun end() {}

  fun draw(canvas: Canvas) {
    //    for (p in points) {
    //      canvas.drawPoint(p.x, p.y, paint)
    //    }
    canvas.drawPath(path, paint)
  }
}
