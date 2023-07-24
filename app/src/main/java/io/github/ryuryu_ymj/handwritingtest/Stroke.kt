package io.github.ryuryu_ymj.handwritingtest

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log

data class Point(val x: Float, val y: Float, val radius: Float)

class Stroke(private val width: Float = 40f) {
  private val points = mutableListOf<Point>()
  private val path = Path()
  private val paint =
      Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        isAntiAlias = true
      }

  fun begin(x: Float, y: Float, pressure: Float) {
    points.add(Point(x, y, pressure * width / 2))
    Log.d(TAG, "BEGIN: $pressure")
  }

  fun extend(x: Float, y: Float, pressure: Float) {
    val p1 = points.last()
    val p2 = Point(x, y, pressure * width / 2)
    var dx = p2.x - p1.x
    var dy = p2.y - p1.y
    val dr = kotlin.math.hypot(dx, dy)
    dx /= dr
    dy /= dr
    if (dx.isFinite() && dy.isFinite()) {
      path.moveTo(p1.x + dy * p1.radius, p1.y - dx * p1.radius)
      path.lineTo(p1.x - dy * p1.radius, p1.y + dx * p1.radius)
      path.lineTo(p2.x - dy * p2.radius, p2.y + dx * p2.radius)
      path.lineTo(p2.x + dy * p2.radius, p2.y - dx * p2.radius)
      path.close()

      points.add(p2)
      Log.d(TAG, "EXTEND: $pressure")
    }
  }

  fun end() {}

  fun draw(canvas: Canvas) {
    canvas.drawPath(path, paint)
    for (p in points) canvas.drawCircle(p.x, p.y, p.radius, paint)
  }
}
