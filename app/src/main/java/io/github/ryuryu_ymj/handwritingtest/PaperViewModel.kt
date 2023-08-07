package io.github.ryuryu_ymj.handwritingtest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.ryuryu_ymj.handwritingtest.smoother.DampedSmoother
import io.github.ryuryu_ymj.handwritingtest.smoother.TouchSmoother

class PaperViewModel : ViewModel() {
  lateinit var offScreenBitmap: Bitmap
    private set
  private lateinit var offScreenCanvas: Canvas
  lateinit var touchPointsBitmap: Bitmap
    private set
  private lateinit var touchPointsCanvas: Canvas
  var drawTouchPoints by mutableStateOf(false)
  val pen = Pen()
  private val smoother: TouchSmoother = DampedSmoother()
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

  fun beginTouch(x: Float, y: Float, pressure: Float, time: Long) {
    touchPointsCanvas.drawPoint(x, y, pointPaint)
    smoother.beginTouch(pen, x, y, pressure, time)
  }

  fun moveTouch(x: Float, y: Float, pressure: Float, time: Long) {
    touchPointsCanvas.drawPoint(x, y, pointPaint)
    smoother.moveTouch(pen, x, y, pressure, time)
  }

  fun endTouch() {
    smoother.endTouch(pen)
    pen.draw(offScreenCanvas)
    pen.end()
  }
}
