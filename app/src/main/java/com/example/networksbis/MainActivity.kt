package com.example.networksbis

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.system.Os.mkdir
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.networksbis.entity.Graph
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_main.*
import java.io.*
import java.util.*

/*Sadou BARRY - Issa SANOGO*/

class MainActivity : AppCompatActivity(), View.OnLongClickListener {

    private var isObjetMode: Boolean = false
    private var isConnexionMode: Boolean = false
    private var isUpdateMode: Boolean = false
    private var isEditObjectMode: Boolean = false
    private var isEditConnextionMode: Boolean = false
    lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        reseau.setOnLongClickListener(this)

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    fun setLabelDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle(R.string.create_add_label_dialog)
        val dialogLayout = inflater.inflate(R.layout.custom_add_label_dialog, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.label)
        builder.setView(dialogLayout)
        builder.setPositiveButton(R.string.yes) { dialogInterface, i -> reseau.createObject(editText.text.toString()) }
        builder.setNegativeButton(R.string.no,null)
        builder.show()
    }

    fun setEmailDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle(R.string.create_email_dialog)
        val dialogLayout = inflater.inflate(R.layout.custom_add_email_dialog, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.email_label)
        builder.setView(dialogLayout)
        builder.setPositiveButton(R.string.yes) {
                dialogInterface, i ->
                val b: Bitmap = Screenshot.takeScreenshotOfRootView(content)
                uri = saveImageToExternalStorage(b)
                sendNetwokByEmail(uri,editText.text.toString())
        }
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
    private fun setMode(obj:Boolean, conx:Boolean, upd:Boolean, edtObj:Boolean, edtCon:Boolean){
        isObjetMode = obj
        isConnexionMode = conx
        isUpdateMode = upd
        isEditObjectMode = edtObj
        isEditConnextionMode = edtCon
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
                setMode(obj = true, conx = false, upd = false, edtObj = false, edtCon = false)
                reseau.setActualMode("")
                Toast.makeText(this@MainActivity,R.string.add_object, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.add_connection ->{
                setMode(obj = false, conx = true, upd = false, edtObj = false, edtCon = false)
                reseau.setActualMode("connexion")
                Toast.makeText(this@MainActivity,R.string.add_connection, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.modification ->{
                setMode(obj = false, conx = false, upd = true, edtObj = false, edtCon = false)
                reseau.setActualMode("update")
                Toast.makeText(this@MainActivity,R.string.modification, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.backup ->{
                saveGraphInJSonFile()
                Toast.makeText(this@MainActivity,R.string.backup_network, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.restore ->{

                var graphRestored:Graph = getGraphFromBackup()
                reseau.setGraph(graphRestored)

                Toast.makeText(this@MainActivity,R.string.restore_network, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.sendByMail ->{

                setEmailDialog()

                true
            }
            R.id.import_map ->{

                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, 1)

                Toast.makeText(this@MainActivity,R.string.import_map, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.edit_object ->{
                setMode(obj = false, conx = false, upd = false, edtObj = true, edtCon = false)
                true
            }
            R.id.edit_connection ->{
                setMode(obj = false, conx = false, upd = false, edtObj = false, edtCon = true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK) {
                //val selectedImage: Uri? = data?.data
                uri = data?.data!!
                //imageview.setImageURI(selectedImage)

                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    content.background = Drawable.createFromStream(inputStream, "MonReseau")
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

            }
            1 -> if (resultCode == Activity.RESULT_OK) {
                //val selectedImage: Uri? = data?.data
                uri = data?.data!!
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    content.background = Drawable.createFromStream(inputStream, "MonReseau")
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }

    }

    private fun sendNetwokByEmail(uri:Uri, email:String) {
        try {

            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.data = Uri.parse("mailto:")
            emailIntent.type = "text/plain"

            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Network")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "My network")

            emailIntent.putExtra(Intent.EXTRA_STREAM, uri)

            this.startActivity(Intent.createChooser(emailIntent, "Sending..."))
        }
        catch (t: Throwable) {
            Toast.makeText(this, "Request failed try again: $t", Toast.LENGTH_LONG).show()
        }
    }

    override fun onLongClick(v: View?): Boolean {
       if (isObjetMode)
           setLabelDialog()
       else if (isEditObjectMode){
           reseau.editObjectDialog(this)
           Toast.makeText(this@MainActivity, "Edite objet", Toast.LENGTH_LONG).show()
       }
       else if (isEditConnextionMode){
           reseau.editConnectionDialog(this)
           Toast.makeText(this@MainActivity, "Connection objet", Toast.LENGTH_LONG).show()
       }
        return true
    }

    fun saveGraphInJSonFile(){
        val writer: Writer
        val gson = Gson()
        var mGraph= Graph()

        mGraph = reseau.getGraph()

        val jsonGraph = gson.toJson(mGraph)
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(storageDir,"graph.json")
        if (!storageDir!!.exists()){
            mkdir(storageDir.absolutePath,0)
        }
        if (!file.exists()){
            file.createNewFile()
        }

        writer = BufferedWriter(FileWriter(file))
        writer.write(jsonGraph)
        writer.close()
    }

    fun getGraphFromBackup(): Graph{

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(storageDir,"graph.json")
        var mGraph: Graph = Graph()
        if (!storageDir!!.exists() || !file.exists()){
            return mGraph
        }else{
            val bReader = BufferedReader(FileReader(file))
            val gson = Gson()
            mGraph = gson.fromJson(bReader.readText(),Graph::class.java)
        }
        return  mGraph
    }

    companion object Screenshot {
        private fun takeScreenshot(view: View): Bitmap {
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache(true)
            val b = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false
            return b
        }
        fun takeScreenshotOfRootView(v: View): Bitmap {
            return takeScreenshot(v.rootView)
        }
    }

    private fun saveImageToExternalStorage(bitmap:Bitmap):Uri{

        var path = getExternalFilesDir(this.getExternalFilesDir(null)?.absolutePath)

        val file = File(path, "${UUID.randomUUID()}.jpeg")

        try {
            val stream: OutputStream = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()

            stream.close()
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()

        }

        return Uri.parse(file.path)
    }

}