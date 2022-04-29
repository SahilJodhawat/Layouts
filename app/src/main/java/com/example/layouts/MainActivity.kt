package com.example.layouts


import ChatAdapter
import ChatModel
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.layouts.adater.NewsAdapter
import com.example.layouts.api.NewsAPI
import com.example.layouts.api.NewsAPI.Companion.newsApi

//import com.example.layouts.databinding.MyPostsBinding
//import com.example.layouts.databinding.RecyclerViewBinding
import com.example.layouts.viewmodel.MainViewModel
import com.example.layouts.viewmodel.MainViewModelFactory
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
//    lateinit var myPostsBinding: MyPostsBinding
//     lateinit var recyclerViewBinding : RecyclerViewBinding
     var isScrolling = false
     var currentItem : Int = 0
     var totalItem : Int = 0
     var scrolledItem : Int = 0
    //lateinit var adapter : NewsAdapter
    lateinit var adapter : ChatAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        recyclerViewBinding = RecyclerViewBinding.inflate(layoutInflater)
        setContentView(R.layout.adding_data)
  val chatRecyclerView : RecyclerView = findViewById(R.id.chats_recycler_view)
        val chatEdt : EditText = findViewById(R.id.ent_msg)
        val chatBtn : Button = findViewById(R.id.snd_btn)
        val rcvdBtn : Button = findViewById(R.id.rvd_btn)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        val chatList = ArrayList<ChatModel>()



  chatBtn.setOnClickListener(object : View.OnClickListener{
      override fun onClick(p0: View?) {
          val msg : String = chatEdt.text.toString()
          val map = HashMap<String,Any>()
          map.put("message",msg)
          map.put("type","sender")
          val key = FirebaseDatabase.getInstance().getReference().push().key
          FirebaseDatabase.getInstance().getReference().child("Chats").push().updateChildren(map)

      }

  })
        rcvdBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val msg : String = chatEdt.text.toString()
                val map = HashMap<String,Any>()
                map.put("message",msg)
                map.put("type","receiver")
                val key2 = FirebaseDatabase.getInstance().getReference().push().key
                FirebaseDatabase.getInstance().getReference().child("Chats").push().updateChildren(map)

            }
        })

        val ref : DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Chats")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val model : ChatModel = snapshot.getValue(ChatModel::class.java)!!
                Log.d("data",snapshot.getValue().toString())
                if (model.type.equals("sender") || model.type.equals("receiver")){
                    chatList.add(model)
                }
                val adapter = ChatAdapter(chatList)
                adapter.notifyDataSetChanged()
                chatRecyclerView.adapter = adapter
                val newMsgPosition: Int = chatList.size - 1
                // Notify recycler view insert one new data.
                // Notify recycler view insert one new data.

                adapter.notifyItemInserted(newMsgPosition)
                // Scroll RecyclerView to the last message.
                // Scroll RecyclerView to the last message.
                chatRecyclerView.scrollToPosition(newMsgPosition)
                // Empty the input edit text box.
                // Empty the input edit text box.

                chatEdt.setText("")
                chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                    }
                })


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

//ref.addValueEventListener(object : ValueEventListener{
//    override fun onDataChange(snapshot: DataSnapshot) {
//        val model = snapshot.getValue(ChatModel::class.java)!!
//        if (model.type.equals("sender") || model.type.equals("receiver")){
//            chatList.add(model)
//
//        }
//        val adapter = ChatAdapter(chatList)
//        adapter.notifyDataSetChanged()
//        chatRecyclerView.adapter = adapter
//        val newMsgPosition: Int = chatList.size - 1
//        // Notify recycler view insert one new data.
//        // Notify recycler view insert one new data.
//
//        adapter.notifyItemInserted(newMsgPosition)
//        // Scroll RecyclerView to the last message.
//        // Scroll RecyclerView to the last message.
//        chatRecyclerView.scrollToPosition(newMsgPosition)
//        // Empty the input edit text box.
//        // Empty the input edit text box.
//
//        chatEdt.setText("")
//    }
//
//    override fun onCancelled(error: DatabaseError) {
//        TODO("Not yet implemented")
//    }
//
//})






//        val newsApi = NewsAPI.getInstance()
//      val repository = NewsRepository(newsApi)
//       mainViewModel = ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]
//         recyclerViewBinding.recyclerView1.layoutManager= LinearLayoutManager(this)
//        recyclerViewBinding.recyclerView1.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
//         adapter = NewsAdapter()
//        recyclerViewBinding.recyclerView1.adapter = adapter
//            mainViewModel.newsdata.observe(this, {
//
//                if (it.isEmpty()) {
//                    recyclerViewBinding.shimmerLayout.visibility = VISIBLE
//                    recyclerViewBinding.recyclerView1.visibility = View.GONE
//                } else {
//
//                    recyclerViewBinding.shimmerLayout.visibility = View.GONE
//                    recyclerViewBinding.recyclerView1.visibility = VISIBLE
//                    adapter.setData(it)
//                }
//
//            })
//
//        mainViewModel.getNews()
//
//        recyclerViewBinding.recyclerView1.addOnScrollListener(object : RecyclerView.OnScrollListener(){
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
//                    isScrolling = true
//                }
//            }
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                currentItem = (recyclerViewBinding.recyclerView1.layoutManager as LinearLayoutManager).childCount
//                totalItem = (recyclerViewBinding.recyclerView1.layoutManager as LinearLayoutManager).itemCount
//                scrolledItem = (recyclerViewBinding.recyclerView1.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
//                if (isScrolling && (currentItem + scrolledItem == totalItem)){
//                    recyclerViewBinding.prgresBr.isEnabled
//                  recyclerViewBinding.prgresBr.visibility = VISIBLE
//  isScrolling = false
//                       FetchData()
//
//                }
//            }
//
//        })
//       // mainViewModel.getNews()
//
//
////        myPostsBinding.searchSheetBtn.setOnClickListener {
////            val bottomSheet = SearchBottomSheet()
////            bottomSheet.show(supportFragmentManager, "search bottom sheet")
////        }
////        supportActionBar!!.hide()
////
////        mainViewModel.getFacts()
//
//    }
//
//    private fun FetchData() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            mainViewModel.newsdata.observe(this@MainActivity, Observer {
//
//                it.addAll(it)
//
//                adapter.notifyDataSetChanged()
//                    recyclerViewBinding.prgresBr.visibility = GONE
//
//
//            })
//        },5000)
//    }
//
//
////        val newThreadBtn:FloatingActionButton=findViewById(R.id.create_thread_btn)
////        newThreadBtn.setOnClickListener{
////            val bottomSheet=NewThreadBottomSheet()
////            bottomSheet.show(supportFragmentManager,"create thread sheet")
////        }
////        shareImag.setOnClickListener{
////            val postforward=PostForwardBottomSheet()
////            postforward.show(supportFragmentManager,"post forward bottom sheet")
////        }
//
//
//
//    override fun onResume() {
//        super.onResume()
//            recyclerViewBinding.shimmerLayout.startShimmer()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        recyclerViewBinding.shimmerLayout.stopShimmer()
//
//    }

    }

}
