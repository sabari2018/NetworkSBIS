package com.example.networksbis

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), View.OnLongClickListener {

    private var isObjetMode: Boolean = false
    private var isConnexionMode: Boolean = false
    private var isUpdateMode: Boolean = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        
        reseau.setOnLongClickListener(this)

    }

    fun setLabelDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle(R.string.create_add_label_dialog)
        val dialogLayout = inflater.inflate(R.layout.custom_add_dialog, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.label)
        builder.setView(dialogLayout)
        builder.setPositiveButton(R.string.yes) { dialogInterface, i -> reseau.createObject(editText.text.toString()) }
        builder.setNegativeButton(R.string.no,null)
        builder.show()
    }

    fun confirmResetDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.confirm_dialog_title)
        builder.setMessage(R.string.confirm_dialog_msg)

        builder.setPositiveButton(R.string.yes) { dialog, which ->
            reseau.resetNetwork()
            Toast.makeText(applicationContext,R.string.confirm_dialog_sucess_msg, Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(R.string.no,null)

        builder.show()
    }

    private fun setMode(obj:Boolean, conx:Boolean, upd:Boolean){
        isObjetMode = obj
        isConnexionMode = conx
        isUpdateMode = upd
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.rest_network -> {
                if (!reseau.isNetworkEmpty())
                    confirmResetDialog()
                else
                    Toast.makeText(this@MainActivity,R.string.msg_network_empty, Toast.LENGTH_SHORT).show()
                true}
            R.id.add_object -> {
                setMode(obj = true, conx = false, upd = false)
                reseau.setActualMode("")
                Toast.makeText(this@MainActivity,R.string.add_object, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.add_connection ->{
                setMode(obj = false, conx = true, upd = false)
                reseau.setActualMode("connexion")
                Toast.makeText(this@MainActivity,R.string.add_connection, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.modification ->{
                setMode(obj = false, conx = false, upd = true)
                reseau.setActualMode("update")
                Toast.makeText(this@MainActivity,R.string.modification, Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onLongClick(v: View?): Boolean {
       if (isObjetMode)
           setLabelDialog()

        return true
    }

}