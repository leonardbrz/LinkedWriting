package com.improveworkflow.drawing

import android.graphics.Color

class QuickColors(val color:Color, val lastUsed:Int) {

    companion object {
        private var lastUsed = 0
    }
}