package com.improveworkflow.drawing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.plus
import kotlin.math.E


private const val STROKE_WIDTH = 12f //has to be float
private const val ERASING_RADIUS = 20f

class MyCanvasView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap


    var isDrawing:Boolean = true
    var isErasing:Boolean = false
    var alternativeDrawMode:Boolean = false

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

        // Draw any current squiggle
        canvas.drawPath(chunkyPath, paint)

        //drawing = Path()

        //List of squiggles making the painting
        /*
        for(mPath: Path in mDrawing){
            canvas.drawPath(mPath, paint)
        }

         */

        //when done this way it is terribly inefficient
        Log.d("Linked List Drawing", "There are currently " + myPath.pathSegments.count())

        //ideas:
        /*
            rasterize or chunk the path segments,
            recalculate the path segments every frame,
            reset the "drawing" Path before drawing the new paths in it
            only adding new paths to the drawing --> leaving the same problem that I had before
            there are some more little things i should try, but for now i should really focus on some other functionalities or i might end up with a nothingburger of an app
         */

        for(i in myPath.pathSegments.indices){
            drawing.addPath(myPath.pathSegments[i].mPath)
            //canvas.drawPath((myPath.pathSegments[i].mPath), paint)
            Log.d("Linked List Drawing", "Segment should be drawn")
        }

        canvas.drawPath(drawing, paint)

        // when this is done, the whole drawing gets removed
        drawing.reset()

        // Draw the drawing so far
        //canvas.drawPath(drawing, paint)
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
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = ERASING_RADIUS
    }

    // Current Path
    private var path = Path()
    private var currentPath = Path()
    private var chunkyPath = Path()
    private var eraserPath = Path()

    // Painting so far
    private var drawing = Path()

    //Using Lists in Lists in Lists might just be a bad idea
    private var myPath:MyPath = MyPath(mutableListOf())

    // List of Paths representing the whole Painting
    private var mDrawing: MutableList<Path> = mutableListOf()
    private var markedForErasure: MutableList<Path> = mutableListOf()

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f
    private var someDebugTracker = 0

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop/5f

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
        path      .reset()
        eraserPath.reset()
        chunkyPath.reset()

        path      .moveTo(motionTouchEventX, motionTouchEventY)
        eraserPath.moveTo(motionTouchEventX, motionTouchEventY)
        chunkyPath.moveTo(motionTouchEventX, motionTouchEventY)

        currentX = motionTouchEventX
        currentY = motionTouchEventY

        someDebugTracker = 0

        if(isErasing){
            circularErase(motionTouchEventX, motionTouchEventY, ERASING_RADIUS)
        }
    }

    private fun touchMove()  {
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)

        if(dx >= touchTolerance || dy >= touchTolerance) {

           if(isDrawing) {

               path.quadTo(
                   currentX,
                   currentY,
                   (motionTouchEventX + currentX) / 2,
                   (motionTouchEventY + currentY) / 2
               )

               currentX = motionTouchEventX
               currentY = motionTouchEventY
               //Draw the path in the extra bitmap to cache it
               extraCanvas.drawPath(path, paint)
           }

           if (alternativeDrawMode) {
               chunkyPath.reset()

               chunkyPath.moveTo(motionTouchEventX, motionTouchEventY)
               chunkyPath.quadTo(currentX, currentY, (motionTouchEventX + currentX) /2, (motionTouchEventY + currentY) /2)

               myPath.addSegment(Segment(currentX, currentY, chunkyPath))

               currentX = motionTouchEventX
               currentY = motionTouchEventY

           }

           if(isErasing){
               circularErase(motionTouchEventX, motionTouchEventY, ERASING_RADIUS)
           }
       }
       invalidate()
   }

   private fun touchUp() {
       // Add the current path to the drawing so far
       Log.d("Linked List Drawing",
           "The amount of new small paths that should have been added is: $someDebugTracker"
       )
       //currentPath = Path(path)
       mDrawing.add(currentPath)
       // Rewind the current path for the next touch
       path.reset()
       eraserPath.reset()
   }

   private fun circularErase(x:Float, y:Float, radius:Float) {
       //there needs to be an easier way to compute this.
       //this is really inefficient
       var marked: MutableList<Segment> = mutableListOf()
       for(mSegment:Segment in myPath.pathSegments){
           if(kotlin.math.sqrt((mSegment.posX - x)*(mSegment.posX - x) + (mSegment.posY - y) * (mSegment.posY - y)) < radius){
               marked.add(mSegment)
           }
       }
       /*
       for(s in marked){
           myPath.pathSegments.remove(s)
       }
        */

   }
   //endregion
}

// region code dump
/*
path.quadTo(currentX, currentY, (motionTouchEventX + currentX) /2, (motionTouchEventY + currentY) /2)
currentX = motionTouchEventX
currentY = motionTouchEventY

extraCanvas.drawPath(path, eraser)
*/
// endregion
