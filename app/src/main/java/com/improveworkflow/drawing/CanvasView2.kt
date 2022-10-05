package com.improveworkflow.drawing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat

class CanvasView2 (context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    enum class EditMode{
        DRAW, MARK, ERASE, LINK
    }

    private lateinit var editMode: EditMode

    fun handleCanvas(canvas: Canvas){
        when(editMode){
            EditMode.DRAW -> Draw(canvas)
        }
    }

    fun Draw(canvas: Canvas){

    }
}