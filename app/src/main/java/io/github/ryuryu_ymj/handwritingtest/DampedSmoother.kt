package io.github.ryuryu_ymj.handwritingtest

import android.util.Log

class DampedSmoother : StrokeSmoother {
  private var touchX = 0f
  private var touchY = 0f
  private var nibX = 0f
  private var nibY = 0f
  private var nibDX = 0f
  private var nibDY = 0f
  private var time = 0L

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
    if (t - time <= 0) return

    touchX = x
    touchY = y
    val omega = 0.09f
    val n = 10
    val dt = (t - time).coerceAtMost(1000 / 60).toFloat() / n
    repeat(n) {
      nibDX -= ((nibX - touchX) * omega * omega + nibDX * omega * 2) * dt
      nibDY -= ((nibY - touchY) * omega * omega + nibDY * omega * 2) * dt
      nibX += nibDX * dt
      nibY += nibDY * dt
      Log.d(TAG, "$t, $dt, $touchX, $touchY, $nibX, $nibY, $nibDX, $nibDY")
    }

    stroke.extend(nibX, nibY)
    //        Log.d(TAG, "${t - time}")
    time = t
  }

  override fun endTouch(stroke: Stroke) {
    stroke.end()
  }
}
