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
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
//    lateinit var myPostsBinding: MyPostsBinding
//     lateinit var recyclerViewBinding : RecyclerViewBinding
     var isScrolling = false
   var currentItem  = 0
    var totalItem  = 0
     var scrolledItem  = 0
    var currentPage  = 1
    //lateinit var adapter : NewsAdapter
    lateinit var adapter : ChatAdapter
    val chatList = ArrayList<ChatModel>()
    lateinit var chatRecyclerView : RecyclerView
    val manager = LinearLayoutManager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        recyclerViewBinding = RecyclerViewBinding.inflate(layoutInflater)
        setContentView(R.layout.adding_data)
   chatRecyclerView = findViewById(R.id.chats_recycler_view)
        val chatEdt : EditText = findViewById(R.id.ent_msg)
        val chatBtn : Button = findViewById(R.id.snd_btn)
        val rcvdBtn : Button = findViewById(R.id.rvd_btn)
        val chtPrgsBar : ProgressBar = findViewById(R.id.chat_prgs_bar)
         chatRecyclerView.layoutManager = manager


getMsg(currentPage)
        adapter = ChatAdapter(chatList)
        chatRecyclerView.adapter = adapter



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
                FirebaseDatabase.getInstance().getReference().child("Chats").push().updateChildren(map)

            }
        })

        chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                currentItem = manager.childCount
                scrolledItem = manager.findFirstVisibleItemPosition()
                totalItem = manager.itemCount
                Log.d("count",totalItem.toString())
                if (isScrolling && (currentItem + scrolledItem == totalItem)){
                //    chatList.clear()
                    isScrolling = false
                    currentPage++
                    chtPrgsBar.visibility = VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed(object : Runnable{
                        override fun run() {
                            getMsg(currentPage)
                            chatList.addAll(chatList)
                            Log.d("kk",chatList.size.toString())
                            chtPrgsBar.visibility = GONE
                        }

                    },4000)





                }
            }
        })


//        Log.d("count",manager.itemCount.toString())
//        Log.d("countCurrent",manager.childCount.toString())
//        val query2 : Query = FirebaseDatabase.getInstance().reference.child("Chats").orderByKey().limitToLast(20)
//        query2.addChildEventListener(object : ChildEventListener{
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//            val model : ChatModel = snapshot.getValue(ChatModel::class.java)!!
//                chatList.add(model)
//            }
//
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
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

    private fun getMsg(pageNo : Int) {
        val ref = FirebaseDatabase.getInstance().getReference().child("Chats")
        val query = ref.limitToLast(pageNo * 20)
        query.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //ref.limitToLast(20)
                val model : ChatModel = snapshot.getValue(ChatModel::class.java)!!
                Log.d("data",snapshot.getValue().toString())
                //     if (model.type.equals("sender") || model.type.equals("receiver")){
                chatList.add(model)
                Log.d("size",chatList.size.toString())
                //        }


//                val adapter = ChatAdapter(chatList)
                adapter.notifyDataSetChanged()
//                chatRecyclerView.adapter = adapter
                val newMsgPosition: Int = chatList.size - 1
                // Notify recycler view insert one new data.
                // Notify recycler view insert one new data.

                adapter.notifyItemInserted(newMsgPosition)
                // Scroll RecyclerView to the last message.
                // Scroll RecyclerView to the last message.
               //  chatRecyclerView.scrollToPosition(newMsgPosition)
                // Empty the input edit text box.
                // Empty the input edit text box.








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

    }

}
