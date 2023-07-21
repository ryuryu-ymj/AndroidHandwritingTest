package io.github.ryuryu_ymj.handwritingtest.smoother

// import io.github.ryuryu_ymj.handwritingtest.Stroke
// import kotlin.math.hypot
//
// class PulledStringSmoother(private val stringLength: Int = 30) : StrokeSmoother {
//  private var nibX = 0f
//  private var nibY = 0f
//
//  override fun beginTouch(stroke: Stroke, x: Float, y: Float, t: Long) {
//    nibX = x
//    nibY = y
//    stroke.begin(nibX, nibY)
//  }
//
//  override fun moveTouch(stroke: Stroke, x: Float, y: Float, t: Long) {
//    val dst = hypot(nibX - x, nibY - y)
//    if (dst > stringLength) {
//      val ratio = (dst - stringLength) / dst
//      nibX += (x - nibX) * ratio
//      nibY += (y - nibY) * ratio
//      stroke.extend(nibX, nibY)
//    }
//  }
//
//  override fun endTouch(stroke: Stroke) {
//    stroke.end()
//  }
// }
