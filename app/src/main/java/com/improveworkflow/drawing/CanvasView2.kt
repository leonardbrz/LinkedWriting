package com.improveworkflow.drawing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat



private const val STROKE_WIDTH = 12f //has to be float
private const val ERASING_RADIUS = 30f

//MyCanvasView got to crowded, so i made a copy, and modified it using the ideas i thought could work

class CanvasView2(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    //instead of juggeling boleans
    var touchInterpretationMode:TouchInterpretationMode = TouchInterpretationMode.DRAW

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
        canvas.drawBitmap(extraBitmap,0f,0f, null)

        // Draw any current squiggle
        canvas.drawPath(chunkyPath, paint)

        // Draw the Data structure i made up for paths
        for(i in myPath.pathSegments.indices){
            canvas.drawPath((myPath.pathSegments[i].mPath), paint)
        }
    }

    // region variables

    private val drawColor = ResourcesCompat.getColor(resources, R.color.black, null)

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

    // Path Variables
    private var helperPath = Path()
    private var chunkyPath = Path()

    var myPath:MyPath = MyPath(mutableListOf())

    //init for Screen touch variables
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop/5f

    // endregion

    override fun onTouchEvent(event: MotionEvent) : Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when(event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP   -> touchUp()
        }
        return true
    }

    private fun touchStart() {
        chunkyPath.reset()
        chunkyPath.moveTo(motionTouchEventX, motionTouchEventY)

        currentX = motionTouchEventX
        currentY = motionTouchEventY

        if(touchInterpretationMode == TouchInterpretationMode.ERASE){
            circularErase(motionTouchEventX, motionTouchEventY, ERASING_RADIUS)
        }
    }

    //TODO: find the problem thats causign the line to be dotted: iI suspect it here

    private fun touchMove()  {
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)

        //only do something if the touch moved far enough
        if(dx >= touchTolerance || dy >= touchTolerance) {
            //could be Switch Case
            if (touchInterpretationMode == TouchInterpretationMode.DRAW) {
                chunkyPath.reset()

                //Make the path for the piece of line emerging during this move
                chunkyPath.moveTo(currentX, currentY)
                chunkyPath.quadTo(motionTouchEventX, motionTouchEventY, (motionTouchEventX + currentX) /2, (motionTouchEventY + currentY) /2)

                //needed to properly differentiate between the Paths so the Path held in Segment does not get reset????
                helperPath = Path(chunkyPath)
                myPath.addSegment(Segment(currentX, currentY, helperPath))

                //current means from the last painted frame in this cass
                currentX = motionTouchEventX
                currentY = motionTouchEventY
            }

            if(touchInterpretationMode == TouchInterpretationMode.ERASE){
                circularErase(motionTouchEventX, motionTouchEventY, ERASING_RADIUS)
            }
        }
        //needed so Canvas gets redrawn
        invalidate()
    }

    private fun touchUp() {
        //cleanup mostly
        helperPath = Path(chunkyPath)
        myPath.addSegment(Segment(currentX, currentY, helperPath))

        helperPath.reset()
        chunkyPath.reset()
    }

    private fun circularErase(x:Float, y:Float, radius:Float) {
        //can't delete from the List you are iterating so I chose to do this
        var marked: MutableList<Segment> = mutableListOf()
        for(mSegment:Segment in myPath.pathSegments){
            if(kotlin.math.sqrt((mSegment.posX - x)*(mSegment.posX - x) + (mSegment.posY - y) * (mSegment.posY - y)) < radius){
                marked.add(mSegment)
            }
        }
        for(n in marked.count() - 1 downTo 0){
            myPath.pathSegments.remove(marked[n])
        }
    }
}
// just the enum
enum class TouchInterpretationMode {
    ERASE, DRAW, MARK, LINK, HAND
}