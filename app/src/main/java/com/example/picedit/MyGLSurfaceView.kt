package com.example.picedit

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import java.lang.Math.abs

class MyGLSurfaceView(context: Context): GLSurfaceView(context) {
    private var renderer: MyGLRenderer
    private var previousX = 0f
    private var previousY = 0f


    init{
        setEGLContextClientVersion(2)
        renderer = MyGLRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val x = event.x
        val y = event.y
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                previousX = x
                previousY = y
                renderer.setTouchPosition(x, y)
               // Log.d("sud call now ","sud call now")
                requestRender()
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY
                val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                val stepSize = 1.0f // Adjust this value to control the smoothness of the line

                if (distance >= stepSize) {

                    val steps = (distance / stepSize).toInt()
                    val xStep = dx / steps
                    val yStep = dy / steps
                    Log.d("check dist ","$previousX $previousY $x $y $distance   $steps")

                    for (i in 0 until steps) {
                        previousX += xStep
                        previousY += yStep
                        renderer.setTouchPosition(previousX, previousY)
                        Log.d("sud call now ","sud call now i = $i")
                        requestRender()
                    }
                }
            }
        }

        return true
    }

}