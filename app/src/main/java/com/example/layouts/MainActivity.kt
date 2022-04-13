package com.example.layouts


import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.layouts.adater.NewsAdapter
import com.example.layouts.api.NewsAPI
import com.example.layouts.databinding.DipsAcademyPostsBinding
import com.example.layouts.databinding.MyPostsBinding
import com.example.layouts.databinding.RecyclerViewBinding
import com.example.layouts.viewmodel.MainViewModel
import com.example.layouts.viewmodel.MainViewModelFactory


class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    lateinit var myPostsBinding: MyPostsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myPostsBinding = MyPostsBinding.inflate(layoutInflater)
        setContentView(myPostsBinding.root)
//        val newsApi = NewsAPI.getInstance()
//        val repository = NewsRepository(newsApi)
//        mainViewModel = ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]
//         recyclerview1.recyclerView1.layoutManager= LinearLayoutManager(this)
//        recyclerview1.recyclerView1.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
//        val adapter = NewsAdapter()
//        recyclerview1.recyclerView1.adapter = adapter
//            mainViewModel.newsdata.observe(this, {
//
//                if (it.isEmpty()) {
//                    recyclerview1.shimmerLayout.visibility = VISIBLE
//                    recyclerview1.recyclerView1.visibility = View.GONE
//                } else {
//                    recyclerview1.shimmerLayout.visibility = View.GONE
//                    recyclerview1.recyclerView1.visibility = VISIBLE
//                    adapter.setData(it)
//                }
//
//            })
//
//             mainViewModel.getNews()


        myPostsBinding.searchSheetBtn.setOnClickListener {
            val bottomSheet = SearchBottomSheet()
            bottomSheet.show(supportFragmentManager, "search bottom sheet")
        }
        supportActionBar!!.hide()

    }





//        val newThreadBtn:FloatingActionButton=findViewById(R.id.create_thread_btn)
//        newThreadBtn.setOnClickListener{
//            val bottomSheet=NewThreadBottomSheet()
//            bottomSheet.show(supportFragmentManager,"create thread sheet")
//        }
//        shareImag.setOnClickListener{
//            val postforward=PostForwardBottomSheet()
//            postforward.show(supportFragmentManager,"post forward bottom sheet")
//        }



//    override fun onResume() {
//        super.onResume()
//        recyclerview1.shimmerLayout.startShimmer()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        recyclerview1.shimmerLayout.stopShimmer()
//    }

}
