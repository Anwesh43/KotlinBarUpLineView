package ui.anwesome.com.baruplineview

/**
 * Created by anweshmishra on 23/04/18.
 */

import android.app.Activity
import android.content.*
import android.graphics.*
import android.view.View
import android.view.MotionEvent

class BarUpLineView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw (canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State (var prevScale : Float = 0f, var dir : Float = 0f, var j : Int = 0, var delay : Int = 0) {

        val scales : Array<Float> = arrayOf(0f, 0f, 0f, 0f)

        val MAX_DELAY : Int = 10
        fun update(stopcb : (Float) -> Unit) {
            if (delay == 0) {
                scales[j] += 0.1f * dir
                if (Math.abs(scales[j] - prevScale) > 1) {
                    scales[j] = prevScale + dir
                    delay++
                }
            } else if (delay < MAX_DELAY) {
                delay++
                if (delay == MAX_DELAY) {
                    j += dir.toInt()
                    delay = 0
                    if (j == scales.size || j == -1) {
                        j -= dir.toInt()
                        dir = 0f
                        prevScale = scales[j]
                        stopcb(prevScale)
                    }
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

    companion object {
        fun create(activity : Activity) : BarUpLineView {
            val view : BarUpLineView = BarUpLineView(activity)
            activity.setContentView(view)
            return view
        }
    }
}

fun Canvas.drawRectLine(w :Float, h : Float, scales : Array<Float>, paint : Paint) {
    paint.style = Paint.Style.FILL_AND_STROKE
    for (i in 0..1) {
        save()
        translate(0f, -h/2 * (1 - 2 * i) * scales[3])
        rotate(-90f * (1 - scales[1]))
        val x : Float = w * scales[2] * (1 - 2 * i)
        val path : Path = Path()
        path.moveTo(0f, 0f)
        path.lineTo(0f, h * (1 - 2 * i) * scales[0])
        path.lineTo(x, h * (1 - 2 * i) * scales[0])
        path.lineTo(x, 0f)
        path.lineTo(0f, 0f)
        drawPath(path, paint)
        restore()
    }
}