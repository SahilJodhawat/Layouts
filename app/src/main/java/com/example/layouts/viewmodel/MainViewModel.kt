package com.example.layouts.viewmodel


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.layouts.NewsRepository
import com.example.layouts.data.News
import com.example.layouts.data.NewsData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by mohammad sajjad on 25-03-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

class MainViewModel(private val newsRepository: NewsRepository):ViewModel() {
         val result = newsRepository.result
         val newsdata=MutableLiveData<ArrayList<NewsData>>()


       fun getNews() {
               result.clone().enqueue(object : Callback<News> {
                   override fun onResponse(call: Call<News>, response: Response<News>) {
                       if (response.isSuccessful) {
                           val data = response.body()
                           newsdata.postValue(data?.data)


                       } else {
                           Log.d("Error", response.errorBody().toString())
                       }

                   }

                   override fun onFailure(call: Call<News>, t: Throwable) {
                       Log.d("Error", t.message!!)
                   }

               })






       }
// fun getFacts(){
//     CoroutineScope(Dispatchers.IO).launch {
//         val mathfacts = async {
//             newsRepository.getMathFacts()
//         }.await()
//
//         val datefacts = async {
//             newsRepository.getDateFacts()
//         }.await()
//
//         val triviafacts = async {
//             newsRepository.getTriviaFacts()
//         }.await()
//
//         val yearfacts = async {
//             newsRepository.getYearFacts()
//         }.await()
//
//         if (mathfacts.isSuccessful && datefacts.isSuccessful && triviafacts.isSuccessful && yearfacts.isSuccessful){
//             Log.d("data",mathfacts.body().toString() +"\n"+datefacts.body().toString()
//             +"\n"+triviafacts.body().toString()+"\n"+yearfacts.body().toString())
//         }
//     }
//
// }


}


