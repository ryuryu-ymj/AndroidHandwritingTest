package io.github.ryuryu_ymj.handwritingtest.smoother

import io.github.ryuryu_ymj.handwritingtest.Pen
import kotlin.math.hypot
import kotlin.math.min

class DampedSmoother(private val omega: Float = 0.09f) : TouchSmoother {
  private var touchX = 0f
  private var touchY = 0f
  private var touchDX = 0f
  private var touchDY = 0f
  private var nibX = 0f
  private var nibY = 0f
  private var nibDX = 0f
  private var nibDY = 0f
  private var nibPressure = 0f
  private var time = 0L
  private val error = 0.1f

  override fun beginTouch(pen: Pen, x: Float, y: Float, pressure: Float, time: Long) {
    touchX = x
    touchY = y
    nibPressure = pressure
    nibX = x
    nibY = y
    nibDX = 0f
    nibDY = 0f
    this.time = time
    pen.begin(x, y, nibPressure)
  }

  override fun moveTouch(pen: Pen, x: Float, y: Float, pressure: Float, time: Long) {
    val w1 = 0.2f
    val duration = time - this.time
    val newTouchDX = (x - touchX) / duration
    val newTouchDY = (y - touchY) / duration
    touchDX = touchDX * (1 - w1) + newTouchDX * w1
    touchDY = touchDY * (1 - w1) + newTouchDY * w1

    touchX = x
    touchY = y
    moveNibPhysically(duration.toInt())
    this.time = time

    val w2 = 0.2f
    nibPressure = nibPressure * (1 - w2) + pressure * w2

    pen.move(nibX, nibY, nibPressure)
  }

  override fun endTouch(pen: Pen) {
    val firstDst = hypot(nibX - touchX, nibY - touchY)

    //    if (firstDst < 1f) {
    //      while (moveNibPhysically(12)) {
    //        pen.move(nibX, nibY, nibPressure)
    //      }
    //    } else {
    //      val nibDR = hypot(nibDX, nibDY)
    //      nibX += nibDX / nibDR * firstDst
    //      nibY += nibDY / nibDR * firstDst
    //      //      val pressure = nibPressure * 0.98f.pow(firstDst.toInt())
    //      pen.move(nibX, nibY, nibPressure)
    //    }
  }

  private fun moveNibPhysically(duration: Int): Boolean {
    var timeLeft = duration
    val omega = 0.05f
    val gamma = 0.45f // damping ratio
    while (timeLeft > 0) {
      val dst = hypot(nibX - touchX, nibY - touchY)
      if (dst <= error) return false
      val dt = min(4, timeLeft)
      timeLeft -= dt
      nibDX -= ((nibX - touchX) * omega * omega + nibDX * omega * gamma * 2) * dt
      nibDY -= ((nibY - touchY) * omega * omega + nibDY * omega * gamma * 2) * dt
      nibX += nibDX * dt
      nibY += nibDY * dt
    }
    return true
  }
}
