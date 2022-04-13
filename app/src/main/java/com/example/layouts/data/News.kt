package com.example.layouts.data


/**
 * Created by mohammad sajjad on 25-03-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

import com.google.gson.annotations.SerializedName


data class News (

    @SerializedName("category" ) var category : String?         = null,
    @SerializedName("data"     ) var data     : ArrayList<NewsData> = arrayListOf(),
    @SerializedName("success"  ) var success  : Boolean?        = null
dd
)


