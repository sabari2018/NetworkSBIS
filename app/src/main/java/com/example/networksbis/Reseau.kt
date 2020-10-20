package com.example.networksbis

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat

class Reseau(context: Context, attributeSet: AttributeSet):View(context,attributeSet) {

    /**
     * Pour dessiner les connexions
     */
    private var path = Path()

    private var touchEventX = 0f
    private var touchEventY = 0f

    private var mode:String = ""
    private var toUpdate:Boolean = false

    private var objetFrom:Objet?=null
    private var objetTo:Objet?=null
    private var currentObjet:Objet?=null

    private lateinit var rect: RectF
    private  var graph= Graph();

    private val paint = Paint().apply {
        color = ResourcesCompat.getColor(resources, R.color.colorAccent,null)
        isAntiAlias = true

        isDither = true
        style = Paint.Style.FILL_AND_STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 12f
    }

     private val labelPaint = Paint().apply {
            color = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark,null)
            style = Paint.Style.FILL
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
     }


    private val paintLine = Paint().apply {
        color = ResourcesCompat.getColor(resources, R.color.colorPrimary,null)

        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 12f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //dessin
        graph.objets.forEach { obj ->
            canvas?.drawRect(obj.thing, paint)
            canvas?.drawText(obj.label, obj.posX + 80f, obj.posY +40f, labelPaint)
         }
        graph.connexions.forEach { conx ->
            canvas?.drawLine(conx.oDebut.thing.centerX().toFloat(), conx.oDebut.thing.centerY().toFloat(), conx.oFin.thing.centerX().toFloat(), conx.oFin.thing.centerY().toFloat(), paintLine)
        }
        canvas?.drawPath(path, paintLine)
        invalidate()
    }

    fun createObject(label: String) {
        rect = RectF(touchEventX, touchEventY, 70+touchEventX , 70+touchEventY)
        graph.objets.add(Objet(rect, label,touchEventX,touchEventY))
        invalidate()
    }

    fun createConnexion(event: MotionEvent): Boolean{
        touchEventX = event.x
        touchEventY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()
        touchEventX = event.x
        touchEventY = event.y

        if (getActualMode() == "connexion"){
            createConnexion(event)
        }
        else if (getActualMode() == "update"){
            onUpdate(event)
        }
        return super.onTouchEvent(event)
    }
    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun getActualMode():String{
        return mode
    }
    fun setActualMode(m:String){mode =m}

    private fun touchMove() {
        path.lineTo(touchEventX, touchEventY)

        invalidate()
    }

    private fun touchStart() {
        path.reset()
        path.moveTo(touchEventX, touchEventY)
        objetFrom = getObject(touchEventX,touchEventY)
    }

    private fun touchUp() {
        objetTo = getObject(touchEventX,touchEventY)

        if(objetFrom==null || objetTo==null){
            path.reset()
        }else{
            if(objetFrom!=null && objetTo!=null){
                if(objetFrom==objetTo){
                    path.reset()
                }else{
                    graph.connexions.add(Connexion(objetFrom!!, objetTo!!))
                }
            }
        }
        path.reset()
    }

    fun onUpdate(event: MotionEvent) : Boolean{
        touchEventX = event.x
        touchEventY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchUpdateStart()
            MotionEvent.ACTION_MOVE -> touchUpdateMove()
            MotionEvent.ACTION_UP -> touchUpdateUp()
        }
        return true
    }

    private fun touchUpdateStart() {
        currentObjet = getObject(touchEventX, touchEventY)
        if(currentObjet!=null){
            toUpdate=true
        }
    }

    private fun touchUpdateUp() {

        if(currentObjet!=null && toUpdate){

            currentObjet!!.posX = touchEventX
            currentObjet!!.posY = touchEventY

            invalidate()
        }
    }

    private fun getObject(x:Float, y:Float): Objet? {
        graph.objets.forEach { obj ->
            if (obj.thing.contains(x, y))
                return obj
        }
        return null
    }

    private fun touchUpdateMove() {
        if(currentObjet!=null && toUpdate) {
            currentObjet!!.thing.set(touchEventX, touchEventY, touchEventX + 70, touchEventY + 70)
        }
    }

     fun resetNetwork(){
        graph= Graph()
        invalidate()
    }

    fun isNetworkEmpty():Boolean{
        return graph.objets.isEmpty() && graph.connexions.isEmpty()
    }
}