package io.github.ryuryu_ymj.handwritingtest.smoother

class PressureSmoother(private val windowSize: Int = 100) {
  private val queue = ArrayDeque<Float>(windowSize)

  fun smooth(pressure: Float): Float {
    if (queue.size >= windowSize) {
      queue.removeFirst()
    }
    queue.addLast(pressure)
    return queue.average().toFloat()
  }
}
