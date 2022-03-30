package com.example.layouts

import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.layouts.adater.NewsAdapter
import com.example.layouts.api.NewsAPI
import com.example.layouts.viewmodel.MainViewModel
import com.example.layouts.viewmodel.MainViewModelFactory
import com.facebook.shimmer.ShimmerFrameLayout

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_view)
        recyclerView = findViewById(R.id.recycler_view1)
        shimmerFrameLayout = findViewById(R.id.shimmer_layout)
        val newsApi = NewsAPI.getInstance()
        val repository = NewsRepository(newsApi)
        mainViewModel = ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        val adapter = NewsAdapter()
        recyclerView.adapter = adapter
        mainViewModel.newsdata.observe(this, {
            val value: String = mainViewModel.newsdata.toString()
            if (value.isEmpty()) {
                shimmerFrameLayout.visibility = VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                shimmerFrameLayout.visibility = View.GONE
                recyclerView.visibility = VISIBLE
                adapter.setData(it)
            }

        })

        mainViewModel.getNews()


//       val searchBtn:FloatingActionButton=findViewById(R.id.search_sheet_btn)
//        val shareImag:ImageView=findViewById(R.id.share_img)
//        searchBtn.setOnClickListener{
//              val bottomSheet=SearchBottomSheet()
//            bottomSheet.show(supportFragmentManager,"search bottom sheet")
//        }
//        val newThreadBtn:FloatingActionButton=findViewById(R.id.create_thread_btn)
//        newThreadBtn.setOnClickListener{
//            val bottomSheet=NewThreadBottomSheet()
//            bottomSheet.show(supportFragmentManager,"create thread sheet")
//        }
//        shareImag.setOnClickListener{
//            val postforward=PostForwardBottomSheet()
//            postforward.show(supportFragmentManager,"post forward bottom sheet")
//        }

    }

    override fun onResume() {
        super.onResume()
        shimmerFrameLayout.startShimmer()
    }

    override fun onPause() {
        super.onPause()
        shimmerFrameLayout.stopShimmer()
    }
}
