package com.example.layouts

import androidx.lifecycle.MutableLiveData
import com.example.layouts.api.NewsAPI
import com.example.layouts.data.News
import com.example.layouts.data.NewsData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by mohammad sajjad on 25-03-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

class NewsRepository(val newsAPI: NewsAPI) {
val result = newsAPI.getnews()
//suspend fun getMathFacts(): Response<JsonObject> {
//        return newsAPI.getMathFacts()
//}
//        suspend fun getTriviaFacts(): Response<JsonObject> {
//                return newsAPI.getTriviaFacts()
//        }
//        suspend fun getDateFacts(): Response<JsonObject> {
//                return newsAPI.getDateFacts()
//        }
//        suspend fun getYearFacts(): Response<JsonObject> {
//                return newsAPI.getYearFacts()
//        }


}