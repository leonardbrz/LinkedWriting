package com.improveworkflow.drawing

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var myCanvasView: CanvasView2
    //lateinit var myCanvasView: MyCanvasView
    lateinit var eraseButton: ImageButton
    lateinit var drawButton: ImageButton
    lateinit var testBrushButton: ImageButton
    lateinit var touchInterpretaionMode:TouchInterpretationMode
    lateinit var context:Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Setup for all the parts of views one might want to interact with later
        myCanvasView = findViewById(R.id.drawing)
        drawButton = findViewById(R.id.draw_button)
        drawButton.setOnClickListener(){setDrawMode()}
        eraseButton = findViewById(R.id.erase_button)
        eraseButton.setOnClickListener(){setEraseMode()}

        //The documentation said this needs to be here for a Drawer to work
        drawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //this does nothing as of jet
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //Methods for influencing the mode of CanvasView2
    private fun setEraseMode(){
        myCanvasView.touchInterpretationMode = TouchInterpretationMode.ERASE
    }

    private fun setDrawMode(){
        myCanvasView.touchInterpretationMode = TouchInterpretationMode.DRAW
    }

    /*

    // Checks if a volume containing external storage is available
    // for read and write.
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    // Checks if a volume containing external storage is available to at least read.
    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }


    override fun onDestroy() {
        SaveCanvasView()
        super.onDestroy()
    }

    fun SaveCanvasView() {
        if(isExternalStorageWritable()){

            val externalStorageVolumes: Array<out File> =
                ContextCompat.getExternalFilesDirs(applicationContext, null)
            val primaryExternalStorage = externalStorageVolumes[0]
            val filename = "myfile"
            //val fileContents = myCanvasView.myPath

            val jsonList = Json.encodeToString(myCanvasView.myPath)

            context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(jsonList.toByteArray())
            }
        }
    }

    fun LoadCanvasView() {
        if(isExternalStorageReadable()){
            val jsonList = context.openFileInput("myfile").bufferedReader().toString()
            //val obj = Json.decodeFromString<MyPath>(obj, jsonList)
        }
    }

     */
}