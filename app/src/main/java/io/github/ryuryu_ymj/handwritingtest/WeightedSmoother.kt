package io.github.ryuryu_ymj.handwritingtest

import android.graphics.PointF
import kotlin.math.hypot

class WeightedSmoother : StrokeSmoother {
    private var touchX = 0f
    private var touchY = 0f
    private var nibX = 0f
    private var nibY = 0f
    private var nibDX = 0f
    private var nibDY = 0f
    private var time = 0L

    private var points = mutableListOf<PointF>()

    override fun beginTouch(stroke: Stroke, x: Float, y: Float, t: Long) {
        touchX = x
        touchY = y
        nibX = x
        nibY = y
        nibDX = 0f
        nibDY = 0f
        time = t
        stroke.begin(x, y)

        points.add(PointF(x, y))
    }

    override fun moveTouch(stroke: Stroke, x: Float, y: Float, t: Long) {
        touchX = x
        touchY = y
        animateStroke(stroke, t)
    }

    override fun animateStroke(stroke: Stroke, t: Long): Boolean {
        val omega = 0.01f
        val n = 10
        repeat(n) {
            val dt = (t - time).toFloat() / n
            nibDX -= ((touchX - nibX) * omega * omega + nibDX * omega * 2) * dt
            nibDY -= ((touchY - nibY) * omega * omega + nibDY * omega * 2) * dt
            nibX += nibDX * dt
            nibY += nibDY * dt
//            Log.d(TAG, "$t, $dt, $touchX, $touchY, $nibX, $nibY, $nibDX, $nibDY")
        }

        stroke.extend(nibX, nibY)
        points.add(PointF(touchX, touchY))
        return hypot(nibX - touchX, nibY - touchY) > 10
    }

    override fun endTouch(stroke: Stroke) {
        stroke.end()
    }
}