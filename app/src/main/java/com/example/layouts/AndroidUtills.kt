package com.example.layouts

import android.content.Context


/**
 * Created by mohammad sajjad on 10-05-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

class AndroidUtills {
    private var density = 1f

  fun dp(value: Float, context: Context): Int {
        if (density == 1f) {
            checkDisplaySize(context)
        }
        return if (value == 0f) {
            0
        } else Math.ceil((density * value).toDouble()).toInt()
    }


    private fun checkDisplaySize(context: Context) {
        try {
            density = context.resources.displayMetrics.density
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}