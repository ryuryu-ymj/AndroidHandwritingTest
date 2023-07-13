package io.github.ryuryu_ymj.handwritingtest

interface StrokeSmoother {
    fun beginTouch(stroke: Stroke, x: Float, y: Float, t: Long)
    fun moveTouch(stroke: Stroke, x: Float, y: Float, t: Long)
    fun animateStroke(stroke: Stroke, t: Long): Boolean
    fun endTouch(stroke: Stroke)
}