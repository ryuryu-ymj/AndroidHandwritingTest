package io.github.ryuryu_ymj.handwritingtest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.OverScroller
import androidx.core.view.ViewCompat

class PaperView(context: Context) : View(context) {
  private lateinit var model: PaperViewModel
  lateinit var offScreenBitmap: Bitmap
    private set
  private lateinit var offScreenCanvas: Canvas

  private val paint =
      Paint().apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
      }
  private val paperFrame = RectF(0f, 0f, 2000f, 2000f)
  private val currentViewport = RectF()
  private val scrollerStartViewport = RectF()
  private val surfaceSizeBuffer = Point()

  private val scroller = OverScroller(context)
  private val gestureListener =
      object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
          scrollerStartViewport.set(currentViewport)
          scroller.forceFinished(true)
          ViewCompat.postInvalidateOnAnimation(this@PaperView)
          return true
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
          val viewportOffsetX = distanceX * currentViewport.width() / width
          val viewportOffsetY = distanceY * currentViewport.height() / height
          setCurrentViewport(
              currentViewport.left + viewportOffsetX,
              currentViewport.top + viewportOffsetY,
              currentViewport.width(),
              currentViewport.height())
          return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
          computeScrollSurfaceSize(surfaceSizeBuffer)
          scrollerStartViewport.set(currentViewport)
          val startX =
              (surfaceSizeBuffer.x * (scrollerStartViewport.left - paperFrame.left) /
                      paperFrame.width())
                  .toInt()
          val startY =
              (surfaceSizeBuffer.y * (scrollerStartViewport.top - paperFrame.top) /
                      paperFrame.height())
                  .toInt()
          scroller.forceFinished(true)
          scroller.fling(
              startX,
              startY,
              -velocityX.toInt(),
              -velocityY.toInt(),
              0,
              surfaceSizeBuffer.x - width,
              0,
              surfaceSizeBuffer.y - height,
              0,
              0,
          )
          ViewCompat.postInvalidateOnAnimation(this@PaperView)
          return true
        }
      }
  private val gestureDetector = GestureDetector(context, gestureListener)

  private val scalaGestureListener =
      object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private val viewportFocus = PointF()
        private var lastSpan = 0f

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
          lastSpan = detector.currentSpan
          return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
          val span = detector.currentSpan

          val newWidth = lastSpan / span * currentViewport.width()
          val newHeight = lastSpan / span * currentViewport.height()

          val focusX = detector.focusX
          val focusY = detector.focusY
          viewportFocus.set(
              currentViewport.left + currentViewport.width() * (focusX - left) / width,
              currentViewport.top + currentViewport.height() * (focusY - top) / height,
          )

          val nx = viewportFocus.x - newWidth * (focusX - left) / width
          val ny = viewportFocus.y - newHeight * (focusY - top) / height
          setCurrentViewport(nx, ny, newWidth, newHeight)

          lastSpan = span
          return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
          val w = (paperFrame.width() / currentViewport.width() * width).toInt()
          val h = (paperFrame.height() / currentViewport.height() * height).toInt()
          setOffscreenCanvasSize(w, h)
          ViewCompat.postInvalidateOnAnimation(this@PaperView)
        }
      }
  private val scaleGestureDetector = ScaleGestureDetector(context, scalaGestureListener)

  fun setViewModel(model: PaperViewModel) {
    this.model = model
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)

    if (oldw == 0 || oldh == 0) {
      setCurrentViewport(0f, 0f, w.toFloat(), h.toFloat())
    } else {
      setCurrentViewport(
          currentViewport.left,
          currentViewport.top,
          currentViewport.left + currentViewport.width() * w / oldw,
          currentViewport.top + currentViewport.height() * h / oldh,
      )
    }
    setOffscreenCanvasSize(
        (paperFrame.width() / currentViewport.width() * w).toInt(),
        (paperFrame.height() / currentViewport.height() * h).toInt())
  }

  override fun onDraw(canvas: Canvas) {
    //    canvas.drawBitmap(
    //        model.offScreenBitmap,
    //        -width * currentViewport.left / currentViewport.width(),
    //        -height * currentViewport.top / currentViewport.height(),
    //        null)
    val c = canvas.save()
    canvas.clipRect(0, 0, width, height)
    canvas.scale(width / currentViewport.width(), height / currentViewport.height())
    canvas.translate(-currentViewport.left, -currentViewport.top)

    canvas.drawBitmap(offScreenBitmap, null, paperFrame, null)
    model.pen.draw(canvas)
    canvas.drawRect(paperFrame, paint)

    canvas.restoreToCount(c)
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    //        Log.d(TAG, "touch event: ${event.eventTime}")
    if (event.getToolType(event.actionIndex) == MotionEvent.TOOL_TYPE_STYLUS) {
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
          model.beginTouch(
              screenXToPaperX(event.x), screenYToPaperY(event.y), event.pressure, event.eventTime)
          Log.d(
              "DrawView touch event",
              "DOWN,${event.eventTime},${event.x},${event.y},${event.pressure}\n")
          invalidate()
          return true
        }
        MotionEvent.ACTION_MOVE -> {
          for (i in 0 until event.historySize) {
            model.moveTouch(
                screenXToPaperX(event.getHistoricalX(i)),
                screenYToPaperY(event.getHistoricalY(i)),
                event.getHistoricalPressure(i),
                event.getHistoricalEventTime(i))
            val log =
                "MOVE,${event.getHistoricalEventTime(i)}," +
                    "${event.getHistoricalX(i)}," +
                    "${event.getHistoricalY(i)}," +
                    "${event.getHistoricalPressure(i)}\n"
            Log.d("DrawView touch event", log)
          }
          //          model.moveTouch(event.x, event.y, event.pressure, event.eventTime)
          //          Log.d(
          //              "DrawView touch event",
          //              "MOVE,${event.eventTime},${event.x},${event.y},${event.pressure}\n")
          invalidate()
          return true
        }
        MotionEvent.ACTION_CANCEL,
        MotionEvent.ACTION_UP -> {
          model.endTouch()

          offScreenCanvas.let {
            val c = it.save()
            it.scale(width / currentViewport.width(), height / currentViewport.height())
            model.strokes.last().draw(it)
            it.restoreToCount(c)
          }
          Log.d("DrawView touch event", "END")
          invalidate()
          return true
        }
      }
    } else {
      var ret = scaleGestureDetector.onTouchEvent(event)
      ret = gestureDetector.onTouchEvent(event) || ret
      if (ret) return true
    }
    return super.onTouchEvent(event)
  }

  override fun computeScroll() {
    super.computeScroll()

    if (scroller.computeScrollOffset()) {
      computeScrollSurfaceSize(surfaceSizeBuffer)
      val x = paperFrame.left + paperFrame.width() * scroller.currX / surfaceSizeBuffer.x
      val y = paperFrame.top + paperFrame.height() * scroller.currY / surfaceSizeBuffer.y
      setCurrentViewport(x, y, currentViewport.width(), currentViewport.height())
      ViewCompat.postInvalidateOnAnimation(this)
    }
  }

  private fun computeScrollSurfaceSize(out: Point) {
    out.set(
        (width * paperFrame.width() / currentViewport.width()).toInt(),
        (height * paperFrame.height() / currentViewport.height()).toInt())
  }

  private fun setCurrentViewport(x: Float, y: Float, width: Float, height: Float) {
    val nx =
        if (width < paperFrame.width()) {
          x.coerceIn(paperFrame.left, paperFrame.right - width)
        } else {
          paperFrame.centerX() - width / 2
        }
    val ny =
        if (height <= paperFrame.height()) {
          y.coerceIn(paperFrame.top, paperFrame.bottom - height)
        } else {
          paperFrame.centerY() - height / 2
        }

    currentViewport.set(nx, ny, nx + width, ny + height)
    ViewCompat.postInvalidateOnAnimation(this)
  }

  private fun setOffscreenCanvasSize(w: Int, h: Int) {
    offScreenBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    offScreenCanvas = Canvas(offScreenBitmap)
    offScreenCanvas.let {
      val c = it.save()
      it.scale(width / currentViewport.width(), height / currentViewport.height())
      for (stroke in model.strokes) {
        stroke.draw(it)
      }
      it.restoreToCount(c)
    }
  }

  private fun screenXToPaperX(screenX: Float) =
      currentViewport.left + currentViewport.width() * screenX / width
  private fun screenYToPaperY(screenY: Float) =
      currentViewport.top + currentViewport.height() * screenY / height
}
