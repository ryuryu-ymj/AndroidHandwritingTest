package io.github.ryuryu_ymj.handwritingtest

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.Log

class Stroke {
  private val path = Path()
  private val vertices = mutableListOf<Float>()
  private val previousPoint = PointF()
  private val paint =
      Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
      }

  fun begin(x: Float, y: Float) {
    previousPoint.set(x, y)
    path.moveTo(x, y + 400)
  }

  fun extend(x: Float, y: Float) {
    var dx = x - previousPoint.x
    var dy = y - previousPoint.y
    val dr = kotlin.math.hypot(dx, dy)
    dx /= dr
    dy /= dr
    if (dx.isFinite() && dy.isFinite()) {
      val radius = 3f
      vertices.add(previousPoint.x - dy * radius)
      vertices.add(previousPoint.y + dx * radius)
      vertices.add(previousPoint.x + dy * radius)
      vertices.add(previousPoint.y - dx * radius)
    }

    previousPoint.set(x, y)
    path.lineTo(x, y + 400)
    Log.d(TAG, "$dr, $dx, $dy")
  }

  fun end() {}

  fun draw(canvas: Canvas) {
    canvas.drawPath(path, paint)
    canvas.drawVertices(
        Canvas.VertexMode.TRIANGLE_STRIP,
        vertices.size,
        vertices.toFloatArray(),
        0,
        null,
        0,
        null,
        0,
        null,
        0,
        0,
        paint)
  }
}
