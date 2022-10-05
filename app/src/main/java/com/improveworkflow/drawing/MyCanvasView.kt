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


private const val STROKE_WIDTH = 12f //has to be float
private const val ERASING_RADIUS = 20f

class MyCanvasView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    var isErasing: Boolean = false

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (::extraBitmap.isInitialized) extraBitmap.recycle()

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)

    }
    //gets called each frame that the canvas gets drawn on the screen
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //canvas.drawBitmap(extraBitmap,0f,0f, null)

        if(isErasing){
            // If erase mode is active erase along path
            canvas.clipOutPath(path)

        } else {
            // Draw any current squiggle
            canvas.drawPath(path, paint)
        }

        // Draw the drawing so far
        canvas.drawPath(drawing, paint)

    }

    //region methods and variables for simpler drawing

    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        // Stroking style
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }
    private val eraser = Paint().apply {
        color = backgroundColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        // Stroking style
        style = Paint.Style.FILL // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }
    // Current Path
    private var path = Path()
    // Path representing the drawing so far
    private val drawing = Path()

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    override fun onTouchEvent(event: MotionEvent) : Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        //at this point we should decide

        when(event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP   -> touchUp()
        }
        return true
    }

    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove()  {
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)
        if(dx >= touchTolerance || dy >= touchTolerance) {
            if(!isErasing){
                path.quadTo(currentX, currentY, (motionTouchEventX + currentX) /2, (motionTouchEventY + currentY) /2)
                currentX = motionTouchEventX
                currentY = motionTouchEventY
                //Draw the path in the extra bitmap to cache it
                extraCanvas.drawPath(path, paint)
            } else {
                path.quadTo(currentX, currentY, (motionTouchEventX + currentX) /2, (motionTouchEventY + currentY) /2)
                currentX = motionTouchEventX
                currentY = motionTouchEventY
                extraCanvas.drawPath(path, eraser)
            }
        }
        invalidate()
    }

    private fun touchUp() {
        // Add the current path to the drawing so far
        if(!isErasing) drawing.addPath(path)
        // Rewind the current path for the next touch
        path.reset()
    }
    //endregion

    //region
    private fun erase() {

    }

    private fun loadCanvas() {

    }

    private fun saveCanvas() {

    }
    //endregion

}