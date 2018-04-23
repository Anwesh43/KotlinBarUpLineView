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
}