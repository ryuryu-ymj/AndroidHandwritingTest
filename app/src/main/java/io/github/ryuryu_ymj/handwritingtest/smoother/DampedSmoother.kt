package io.github.ryuryu_ymj.handwritingtest.smoother

import io.github.ryuryu_ymj.handwritingtest.Stroke
import kotlin.math.hypot
import kotlin.math.min

class DampedSmoother(private val omega: Float = 0.09f) : StrokeSmoother {
  private var touchX = 0f
  private var touchY = 0f
  private var touchPressure = 0f
  private var nibX = 0f
  private var nibY = 0f
  private var nibDX = 0f
  private var nibDY = 0f
  private var time = 0L
  private val error = 0.1f

  override fun beginTouch(stroke: Stroke, x: Float, y: Float, pressure: Float, time: Long) {
    touchX = x
    touchY = y
    touchPressure = pressure
    nibX = x
    nibY = y
    nibDX = 0f
    nibDY = 0f
    this.time = time
    stroke.begin(x, y, pressure)
  }

  override fun moveTouch(stroke: Stroke, x: Float, y: Float, pressure: Float, time: Long) {
    touchX = x
    touchY = y
    touchPressure = pressure
    while (this.time < time && hypot(nibX - touchX, nibY - touchY) > error) {
      val dt = min(2, time - this.time)
      this.time += dt
      moveNibPhysically(dt)
    }

    stroke.extend(nibX, nibY, pressure)
  }

  override fun endTouch(stroke: Stroke) {
    while (hypot(nibX - touchX, nibY - touchY) > error) {
      repeat(5) { moveNibPhysically(2) }
      touchPressure *= 0.8f
      stroke.extend(nibX, nibY, touchPressure)
    }

    stroke.end()
  }

  private fun moveNibPhysically(dt: Long) {
    nibDX -= ((nibX - touchX) * omega * omega + nibDX * omega * 2) * dt
    nibDY -= ((nibY - touchY) * omega * omega + nibDY * omega * 2) * dt
    nibX += nibDX * dt
    nibY += nibDY * dt
  }
}
