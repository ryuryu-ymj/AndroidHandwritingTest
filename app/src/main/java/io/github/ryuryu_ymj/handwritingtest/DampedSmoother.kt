package io.github.ryuryu_ymj.handwritingtest

import kotlin.math.hypot
import kotlin.math.min

class DampedSmoother : StrokeSmoother {
  private var touchX = 0f
  private var touchY = 0f
  private var nibX = 0f
  private var nibY = 0f
  private var nibDX = 0f
  private var nibDY = 0f
  private var time = 0L
  private val error = 0.1f

  override fun beginTouch(stroke: Stroke, x: Float, y: Float, t: Long) {
    touchX = x
    touchY = y
    nibX = x
    nibY = y
    nibDX = 0f
    nibDY = 0f
    time = t
    stroke.begin(x, y)
  }

  override fun moveTouch(stroke: Stroke, x: Float, y: Float, t: Long) {
    touchX = x
    touchY = y
    while (time < t && hypot(nibX - touchX, nibY - touchY) > error) {
      val dt = min(2, t - time)
      time += dt
      moveNibPhysically(dt)
    }

    stroke.extend(nibX, nibY)
  }

  override fun endTouch(stroke: Stroke) {
    while (hypot(nibX - touchX, nibY - touchY) > error) {
      repeat(5) { moveNibPhysically(2) }
      stroke.extend(nibX, nibY)
    }

    stroke.end()
  }

  private fun moveNibPhysically(dt: Long) {
    val omega = 0.09f
    nibDX -= ((nibX - touchX) * omega * omega + nibDX * omega * 2) * dt
    nibDY -= ((nibY - touchY) * omega * omega + nibDY * omega * 2) * dt
    nibX += nibDX * dt
    nibY += nibDY * dt
  }
}
