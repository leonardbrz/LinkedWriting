package com.improveworkflow.drawing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val attr =
        //val myCanvasView = MyCanvasView(this)

        setContentView(R.layout.activity_drawing)
        //setContentView(R.layout.activity_main)
        //finding and setting toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.thisspecifictoolbar)
        setSupportActionBar(toolbar)
        //home navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}