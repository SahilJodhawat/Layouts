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
import com.example.layouts.databinding.RecyclerViewBinding
import com.example.layouts.viewmodel.MainViewModel
import com.example.layouts.viewmodel.MainViewModelFactory
import com.facebook.shimmer.ShimmerFrameLayout

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView
    lateinit var recyclerview1 : RecyclerViewBinding
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerview1 = RecyclerViewBinding.inflate(layoutInflater)
        setContentView(recyclerview1.root)
        val newsApi = NewsAPI.getInstance()
        val repository = NewsRepository(newsApi)
        mainViewModel = ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]
         recyclerview1.recyclerView1.layoutManager= LinearLayoutManager(this)
        recyclerview1.recyclerView1.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        val adapter = NewsAdapter()
        recyclerview1.recyclerView1.adapter = adapter
        mainViewModel.newsdata.observe(this, {
            val value: String = mainViewModel.newsdata.toString()
            if (value.isEmpty()) {
                recyclerview1.shimmerLayout.visibility = VISIBLE
                recyclerview1.recyclerView1.visibility = View.GONE
            } else {
                recyclerview1.shimmerLayout.visibility = View.GONE
                recyclerview1.recyclerView1.visibility = VISIBLE
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
        recyclerview1.shimmerLayout.startShimmer()
    }

    override fun onPause() {
        super.onPause()
        recyclerview1.shimmerLayout.stopShimmer()
    }

}
