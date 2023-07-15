package io.github.ryuryu_ymj.handwritingtest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import androidx.lifecycle.ViewModel

class DrawViewModel : ViewModel() {
  lateinit var offScreenBitmap: Bitmap
    private set
  private lateinit var offScreenCanvas: Canvas
  lateinit var touchPointsBitmap: Bitmap
    private set
  private lateinit var touchPointsCanvas: Canvas
  private val touchPoints = mutableListOf<PointF>()
  var lastStroke: Stroke? = null
    private set
  private val smoother: StrokeSmoother = DampedSmoother()
  private val pointPaint =
      Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = true
      }

  fun setCanvasSize(w: Int, h: Int) {
    offScreenBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    offScreenCanvas = Canvas(offScreenBitmap)
    touchPointsBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    touchPointsCanvas = Canvas(touchPointsBitmap)
  }

  fun beginTouch(x: Float, y: Float, t: Long) {
    touchPoints.add(PointF(x, y))
    touchPointsCanvas.drawPoint(x, y, pointPaint)
    lastStroke = Stroke().also { smoother.beginTouch(it, x, y, t) }
  }

  fun moveTouch(x: Float, y: Float, t: Long) {
    touchPoints.add(PointF(x, y))
    touchPointsCanvas.drawPoint(x, y, pointPaint)
    lastStroke?.let { smoother.moveTouch(it, x, y, t) }
  }

  fun endTouch() {
    lastStroke?.let { smoother.endTouch(it) }
    lastStroke?.draw(offScreenCanvas)
    lastStroke = null
  }
}
