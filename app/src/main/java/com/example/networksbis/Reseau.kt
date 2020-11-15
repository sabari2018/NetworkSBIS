package com.example.networksbis

import android.content.Context
import android.content.DialogInterface
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.example.networksbis.entity.Connexion
import com.example.networksbis.entity.Graph
import com.example.networksbis.entity.Objet

/*Sadou BARRY - Issa SANOGO*/

class Reseau(context: Context, attributeSet: AttributeSet):View(context,attributeSet) {

    /**
     * Pour dessiner les connexions
     */
    private var path = Path()

    private var touchEventX = 0f
    private var touchEventY = 0f

    private var touchX = 0f
    private var touchY = 0f

    private var mode:String = ""

    private var objetFrom: Objet?=null
    private var objetTo: Objet?=null
    private var currentObjet: Objet?=null
    private var selectedRadioItem = -1

    private lateinit var rect: RectF
    private  var graph= Graph()



   /* private val paint = Paint().apply {
        color = ResourcesCompat.getColor(resources, R.color.colorAccent,null)
        isAntiAlias = true

        isDither = true
        style = Paint.Style.FILL_AND_STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 12f
    }*/

    private val labelPaint = Paint().apply {
            color = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark,null)
            style = Paint.Style.FILL
            textSize = 50f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
     }

    private val paintLine = Paint().apply {
        color = ResourcesCompat.getColor(resources, R.color.colorPrimary,null)

       
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
            canvas?.drawRect(obj.thing, Paint().apply {
                color = obj.color
                isAntiAlias = true

                isDither = true
                style = Paint.Style.FILL_AND_STROKE
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
                strokeWidth = 12f
            })
            canvas?.drawText(obj.label, obj.posX + 110f, obj.posY +60f, labelPaint)
         }
        graph.connexions.forEach { conx ->
            canvas?.drawLine(conx.oDebut?.thing!!.centerX(), conx.oDebut?.thing!!.centerY(), conx.oFin?.thing!!.centerX(), conx.oFin?.thing!!.centerY(), paintLine)


            var label:String = conx.oDebut?.label+"-->"+conx.oFin?.label
            canvas?.drawText(label,
                conx.midX - (label.length)/2,
                conx.midY - (label.length)/2,
                labelPaint
            )
        }
        canvas?.drawPath(path, paintLine)
        invalidate()
    }

    fun createObject(label: String) {
        rect = RectF(touchEventX, touchEventY, 100+touchEventX , 100+touchEventY)
        graph.objets.add(
            Objet(
                rect,
                label,
                touchEventX,
                touchEventY,
                ResourcesCompat.getColor(resources, R.color.colorAccent,null)
            )
        )
        invalidate()
    }

    fun createConnexion(event: MotionEvent): Boolean{
        touchEventX = event.x
        touchEventY = event.y
        touchX = touchEventX
        touchY = touchEventY
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
       // path.lineTo(touchEventX, touchEventY)
        path.quadTo(touchX,touchY,touchEventX,touchEventY)
        invalidate()
    }

    private fun touchStart() {
        path.reset()
        //path.moveTo(touchEventX, touchEventY)
        path.moveTo(touchEventX, touchEventY)
        objetFrom = getObject(touchEventX,touchEventY)
    }

    private fun touchUp() {
        objetTo = getObject(touchEventX,touchEventY)

        if(objetFrom!=null && objetTo!=null){
            if(objetFrom==objetTo){
                path.reset()
            }else{
                graph.connexions.add(
                    Connexion(
                        objetFrom!!,
                        objetTo!!,
                        (objetFrom?.thing!!.centerX() + objetTo?.thing!!.centerX())/2,
                        (objetFrom?.thing!!.centerY() + objetTo?.thing!!.centerY())/2,
                        objetFrom?.label+"-->"+objetTo?.label
                    )
                )
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
    }

    private fun touchUpdateUp() {

      if(currentObjet!=null){

            currentObjet!!.posX = touchEventX
            currentObjet!!.posY = touchEventY

            invalidate()
        }
    }

    private fun touchUpdateMove() {
        if(currentObjet!=null) {
            currentObjet!!.thing.set(touchEventX, touchEventY, 100+touchEventX , 100+touchEventY)
        }
    }

    fun getObject(x:Float, y:Float): Objet? {
        graph.objets.forEach { obj ->
            if (obj.thing.contains(x, y))
                return obj
        }
        return null
    }

    fun getConnexion(midX:Float,midY:Float): Connexion?{
        graph.connexions.forEach { conx ->
            if (conx.midX == midX && conx.midY == midY){
                return conx
            }
        }
        return null
    }

    fun deleteObjet(objet: Objet){
        graph.objets.remove(objet)
        var listOfConnectionsToDel:MutableList<Connexion> = mutableListOf()
        graph.connexions.forEach { conx ->
            if (conx.oDebut == objet || conx.oFin == objet){
                listOfConnectionsToDel.add(conx)
            }
        }

        listOfConnectionsToDel.forEach { con ->
            graph.connexions.remove(con)
        }
    }

    fun resetNetwork(){
         graph.objets.clear()
         graph.connexions.clear()
         invalidate()
    }

    fun isNetworkEmpty():Boolean{
        return graph.objets.isEmpty() && graph.connexions.isEmpty()
    }

    fun getGraph(): Graph{
        return graph
    }

    fun setGraph(g: Graph?){
        if (g != null) {
            graph = g

            graph.connexions.forEach { conx ->
                conx.oDebut = getObject(conx.oDebut!!.posX,conx.oDebut!!.posY)
                conx.oFin = getObject(conx.oFin!!.posX,conx.oFin!!.posY)
            }
        }
        invalidate()
    }

    fun editObjectDialog(activity: MainActivity){
        var objet = getObject(touchEventX,touchEventY)
        var remove:Boolean = false
        if (objet != null){
            val builder = AlertDialog.Builder(context)
            val inflater =  activity.layoutInflater
            builder.setTitle(R.string.create_add_label_dialog)
            val dialogLayout = inflater.inflate(R.layout.custom_edit_object_dialog, null)
            val editText  = dialogLayout.findViewById<EditText>(R.id.label)
            val btnColor = dialogLayout.findViewById<Button>(R.id.btnColorPicker)
            val btnDelete = dialogLayout.findViewById<Button>(R.id.btnDelete)
            editText.setText(objet.label)

            btnColor.setOnClickListener(){
                showColorListDialog(objet)
            }

            btnDelete.setOnClickListener(){
                remove = true
            }

            builder.setView(dialogLayout)
            builder.setPositiveButton(R.string.yes) { dialogInterface, i ->
                objet.label= editText.text.toString()
                if (remove){
                    deleteObjet(objet)
                }
            }
            builder.setNegativeButton(R.string.no,null)
            builder.show()
        }
        invalidate()
    }

    fun editConnectionDialog(activity: MainActivity){
        var connexion = getConnexion(touchEventX,touchEventY)
        if (connexion != null){
            //ICI je vais editer les infos de la connexion
        }
    }

    fun showColorListDialog(objet: Objet) {

        val colors = arrayOf("Red", "Black", "Yellow", "Green","Blue")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Colors")

        builder.setSingleChoiceItems(colors, selectedRadioItem,
            DialogInterface.OnClickListener { dialog, choice ->

                selectedRadioItem = choice
                when(colors[choice]){
                    "Red" -> objet.color = ResourcesCompat.getColor(resources, R.color.red,null)
                    "Black" -> objet.color = ResourcesCompat.getColor(resources, R.color.black,null)
                    "Yellow" -> objet.color = ResourcesCompat.getColor(resources, R.color.yellow,null)
                    "Green" -> objet.color = ResourcesCompat.getColor(resources, R.color.green,null)
                    "Blue" -> objet.color = ResourcesCompat.getColor(resources, R.color.blue,null)
                }

            })
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }



}