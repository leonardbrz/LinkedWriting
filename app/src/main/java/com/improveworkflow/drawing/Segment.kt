package com.improveworkflow.drawing

import android.graphics.Color
import android.graphics.Path
//import kotlinx.serialization.Serializable

//TODO make use of hashtables or something so segments may be easily that could also help with pointing to the next Segment in the line
//that would allow for some nice features, maybe

//@Serializable
data class Segment (val x:Float,val y:Float,var path: Path) {
    fun toByteArray(): ByteArray {
        var byteArray:ByteArray = byteArrayOf()
        byteArray.plus(posX.toInt().toByte())
        byteArray.plus(posY.toInt().toByte())
        byteArray.plus(mPath.toString().toByte())
        return byteArray
    }

    var posX:Float
    var posY:Float
    var mPath:Path

    init{
        posX = x
        posY = y
        mPath = path
    }


}