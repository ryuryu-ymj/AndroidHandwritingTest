package io.github.ryuryu_ymj.handwritingtest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import android.view.View

class DrawView(context: Context) : View(context) {
  private lateinit var model: DrawViewModel
  var drawTouchPoints = false

  fun setViewModel(model: DrawViewModel) {
    this.model = model
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    model.setCanvasSize(w, h)
  }

  /*
      override fun computeScroll() {
          val t = SystemClock.uptimeMillis()
          if (isTouching && model.animateStroke(t)) {
              postInvalidateOnAnimation()
          }
          Log.d(TAG, "DrawView compute scroll: $t")
      }
  */

  override fun onDraw(canvas: Canvas) {
    //        canvas.drawColor(Color.WHITE)
    if (drawTouchPoints) {
      canvas.drawBitmap(model.touchPointsBitmap, 0f, 0f, null)
    }
    canvas.drawBitmap(model.offScreenBitmap, 0f, 0f, null)
    model.lastStroke?.draw(canvas)
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    //        Log.d(TAG, "touch event: ${event.eventTime}")
    if (event.getToolType(event.actionIndex) == MotionEvent.TOOL_TYPE_STYLUS) {
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
          model.beginTouch(event.x, event.y, event.eventTime)
          Log.d("DrawView touch event", "DOWN,${event.eventTime},${event.x},${event.y}\n")
          invalidate()
          return true
        }
        MotionEvent.ACTION_MOVE -> {
          for (i in 0 until event.historySize) {
            model.moveTouch(
                event.getHistoricalX(i), event.getHistoricalY(i), event.getHistoricalEventTime(i))
            val log =
                "MOVE,${event.getHistoricalEventTime(i)}," +
                    "${event.getHistoricalX(i)}," +
                    "${event.getHistoricalY(i)}\n"
            Log.d("DrawView touch event", log)
          }
          model.moveTouch(event.x, event.y, event.eventTime)
          Log.d("DrawView touch event", "MOVE,${event.eventTime},${event.x},${event.y}\n")
          invalidate()
          return true
        }
        MotionEvent.ACTION_CANCEL,
        MotionEvent.ACTION_UP -> {
          model.endTouch()
          Log.d("DrawView touch event", "END")
          invalidate()
          return true
        }
      }
    }
    return super.onTouchEvent(event)
  }
}
