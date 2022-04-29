package com.example.layouts.api


import com.example.layouts.data.News
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


/**
 * Created by mohammad sajjad on 25-03-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

interface NewsAPI {
    @GET("news?category=india")
    fun getnews():Call<News>
//
//    @GET("random/math?json")
//   suspend fun  getMathFacts():Response<JsonObject>
//
//    @GET("random/trivia?json")
//   suspend fun getTriviaFacts(): Response<JsonObject>
//
//    @GET("random/year?json")
//   suspend fun getYearFacts(): Response<JsonObject>
//
//    @GET("random/date?json")
//    suspend fun getDateFacts():Response<JsonObject>

    companion object{
        var newsApi:NewsAPI?=null

        fun getInstance():NewsAPI{
            if (newsApi==null){
                val retrofit= Retrofit.Builder().baseUrl("https://inshortsapi.vercel.app/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                newsApi=retrofit.create(NewsAPI::class.java)
            }
            return newsApi!!
        }

    }
}