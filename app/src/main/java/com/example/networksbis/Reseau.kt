package com.example.networksbis

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat

class Reseau(context: Context):View(context) {

    /**
     * Pour dessiner les connexions
     */
    private var path = Path()

    private var touchEventX = 0f
    private var touchEventY = 0f

    private val paint = Paint().apply {
        color = ResourcesCompat.getColor(resources, R.color.colorPrimary,null)
        isAntiAlias = true

        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 12f
    }
    //RectF rectf = new RectF(200, 400, 200, 400)
    private lateinit var obj: Rect

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        obj = Rect(200, 400, 200, 400)
        canvas?.drawRect(obj, paint)
        //dessin
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchEventX = event.x
        touchEventY = event.y

        return true
    }
}