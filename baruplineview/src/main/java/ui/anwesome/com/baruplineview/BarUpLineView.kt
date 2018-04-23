package ui.anwesome.com.baruplineview

/**
 * Created by anweshmishra on 23/04/18.
 */

import android.content.*
import android.graphics.*
import android.view.View
import android.view.MotionEvent

class BarUpLineView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw (canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State (var prevScale : Float = 0f, var dir : Float = 0f, var j : Int = 0) {

        val scales : Array<Float> = arrayOf(0f, 0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += 0.1f * dir
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                    stopcb(prevScale)
                }

            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator (private var view : View, var animated : Boolean = false) {

        fun animate (updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BarUpLine (var i : Int, val state : State = State()) {

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            paint.color = Color.parseColor("#3949AB")
            paint.strokeWidth = Math.min(w, h) / 60
            paint.strokeCap = Paint.Cap.ROUND
            canvas.save()
            canvas.translate(w/2, h/2)
            canvas.drawRectLine(w/3, h/10, state.scales, paint)
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }

    data class Renderer(var view : BarUpLineView) {

        private val barUpLine : BarUpLine = BarUpLine(0)

        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            barUpLine.draw(canvas, paint)
            animator.animate {
                barUpLine.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            barUpLine.startUpdating {
                animator.start()
            }
        }
    }
}

fun Canvas.drawRectLine(w :Float, h : Float, scales : Array<Float>, paint : Paint) {
    paint.style = Paint.Style.FILL_AND_STROKE
    for (i in 0..1) {
        save()
        translate(0f, -h/2 * (1 - 2 * i) * scales[2])
        rotate(-90f * (1 - scales[0]))
        val x : Float = w * scales[1]
        val path : Path = Path()
        path.moveTo(0f, 0f)
        path.lineTo(0f, h * (1 - 2 * i))
        path.lineTo(x, h * (1 - 2 * i))
        path.lineTo(x, 0f)
        path.lineTo(0f, 0f)
        drawPath(path, paint)
        restore()
    }
}