package com.example.networksbis

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.rest_network -> {
                Toast.makeText(this@MainActivity,"Initialise le reseau", Toast.LENGTH_SHORT).show()
                true}
            R.id.add_object -> {
                Toast.makeText(this@MainActivity,"Ajouter un objet", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.add_connection ->{
                Toast.makeText(this@MainActivity,"Ajouter une connexion", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.modification ->{
                Toast.makeText(this@MainActivity,"Modification Objets/Connexions", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}