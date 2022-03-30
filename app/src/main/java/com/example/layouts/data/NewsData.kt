package com.example.layouts.data

import com.google.gson.annotations.SerializedName


/**
 * Created by mohammad sajjad on 25-03-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

data class NewsData (

    @SerializedName("author"      ) var author      : String? = null,
    @SerializedName("content"     ) var content     : String? = null,
    @SerializedName("date"        ) var date        : String? = null,
    @SerializedName("imageUrl"    ) var imageUrl    : String? = null,
    @SerializedName("readMoreUrl" ) var readMoreUrl : String? = null,
    @SerializedName("time"        ) var time        : String? = null,
    @SerializedName("title"       ) var title       : String? = null,
    @SerializedName("url"         ) var url         : String? = null

)