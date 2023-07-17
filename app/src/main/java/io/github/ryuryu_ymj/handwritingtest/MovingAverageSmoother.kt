package io.github.ryuryu_ymj.handwritingtest

import android.graphics.PointF

class MovingAverageSmoother(private val windowSize: Int = 12) : StrokeSmoother {
  private val queue = ArrayDeque<PointF>(windowSize)

  override fun beginTouch(stroke: Stroke, x: Float, y: Float, t: Long) {
    queue.addLast(PointF(x, y))
    stroke.begin(x, y)
  }

  override fun moveTouch(stroke: Stroke, x: Float, y: Float, t: Long) {
    queue.addLast(PointF(x, y))
    val sx = queue.map { it.x }.average().toFloat()
    val sy = queue.map { it.y }.average().toFloat()
    stroke.extend(sx, sy)
    if (queue.size > windowSize) {
      queue.removeFirst()
    }
  }

  override fun endTouch(stroke: Stroke) {
    while (queue.isNotEmpty()) {
      val sx = queue.map { it.x }.average().toFloat()
      val sy = queue.map { it.y }.average().toFloat()
      stroke.extend(sx, sy)
      queue.removeFirst()
    }
    stroke.end()
  }
}
