package com.improveworkflow.drawing

import android.graphics.Color
import android.graphics.Path
//import kotlinx.serialization.Serializable

//@Serializable
class MyPath (iPathSegments: MutableList<Segment>) {
    var pathSegments: MutableList<Segment> = mutableListOf()

    init{
        pathSegments = iPathSegments
    }

    fun addSegment(segment: Segment){
        pathSegments.add(segment)
    }

    fun addSegment(x:Float, y:Float, path: Path){
        pathSegments.add(Segment(x,y,path))
    }

    fun splitPathAt(segment: Segment): Any{
        var markedSegment:Int = 0
        var splitPath:MyPath = MyPath(mutableListOf())

        for(mSegment in pathSegments.indices){
            if(pathSegments[mSegment] == segment){
                markedSegment = mSegment
                splitPath.pathSegments = pathSegments.subList(markedSegment + 1, pathSegments.count())
                pathSegments = pathSegments.subList(0, markedSegment - 1)
                return splitPath
            }
        }

        return false
    }

    /*
    fun toByteArray(): ByteArray {
        var byteArray:ByteArray = byteArrayOf()
        for(s:Segment in pathSegments){
            byteArray.plus(s.toByteArray())
        }
        return byteArray
    }

     */
}
