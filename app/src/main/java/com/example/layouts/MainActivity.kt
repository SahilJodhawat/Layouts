package com.example.layouts


import ChatAdapter
import ChatModel
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//import com.example.layouts.databinding.MyPostsBinding
//import com.example.layouts.databinding.RecyclerViewBinding
import com.example.layouts.viewmodel.MainViewModel
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(),ChatAdapter.QuoteClickListener{
    private lateinit var mainViewModel: MainViewModel

    //    lateinit var myPostsBinding: MyPostsBinding
//     lateinit var recyclerViewBinding : RecyclerViewBinding
    var isScrolling = false
    var currentItem = 0
    var totalItem = 0
    var scrolledItem = 0
    var currentPage = 10
    var previousItem = 0
    var endResult = false
var first = true


    //lateinit var adapter : NewsAdapter
    lateinit var adapter: ChatAdapter
    val chatList = ArrayList<ChatModel>()
    val tempChatList = ArrayList<ChatModel>()
    lateinit var chatRecyclerView: RecyclerView
    lateinit var chatBtn : Button
    lateinit var chatEdt : EditText
    lateinit var chtPrgsBar : ProgressBar
    lateinit var txtQuotedmsg : TextView
    lateinit var replyLayout : ConstraintLayout
    lateinit var rcvdBtn : Button
    lateinit var cancelBtn : ImageButton
    var quotePos : Int = 0


    val manager = LinearLayoutManager(this)
    var previousKey : String = ""
    var lastKey: String = ""
    var itempos = 0
    var key : String? = ""
    var newKey : String = ""
    var totalCount = 25
    var dateformat : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        recyclerViewBinding = RecyclerViewBinding.inflate(layoutInflater)
        setContentView(R.layout.adding_data)

        chatRecyclerView = findViewById(R.id.chats_recycler_view)

        Log.d("date&Time",dateformat)
      //  val d = SimpleDateFormat().parse(dateformat)
//        Log.d("date",d.toString())
//        val cal = Calendar.getInstance()
//        cal.time = d
//        val e = SimpleDateFormat("d,MMM").format(cal.time)
//Log.d("month",e)
         chatEdt = findViewById(R.id.ent_msg)
        chatBtn = findViewById(R.id.snd_btn)
         rcvdBtn = findViewById(R.id.rvd_btn)
         chtPrgsBar = findViewById(R.id.chat_prgs_bar)
         txtQuotedmsg = findViewById(R.id.txtQuotedMsg)
         replyLayout = findViewById(R.id.reply_layout)
        cancelBtn = findViewById(R.id.cancelButton)
        chatRecyclerView.layoutManager = manager
      //  manager.reverseLayout = true
supportActionBar!!.hide()



            val query = FirebaseDatabase.getInstance().getReference().child("Chats").limitToLast(totalCount).orderByKey()
          query.addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                    var model : ChatModel = snapshot.getValue(ChatModel::class.java)!!


    chatList.add(model)

adapter.notifyItemChanged(chatList.indexOf(model))
Log.d("modelCount",snapshot.childrenCount.toString())


Log.d("model",model.toString())




                    previousKey = snapshot.key!!
                    Log.d("dsKey", previousKey)
                    chatRecyclerView.scrollToPosition(chatList.size - 1)


                    for (ds in snapshot.children) {
                        itempos++
                        Log.d("itempos", itempos.toString())
                        Log.d("childCount", ds.children.count().toString())
                        if (first) {

                            key = snapshot.key!!

                            Log.d("Keyyy", snapshot.key!!)
                            first = false
                        }

                    }


//                lastKey = snapshot.key!!
//                Log.i("LAST",lastKey)

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    adapter.notifyDataSetChanged()

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    adapter.notifyDataSetChanged()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


val ref = FirebaseDatabase.getInstance().getReference().child("Chats").orderByKey()
ref.addListenerForSingleValueEvent(object : ValueEventListener{
    override fun onDataChange(snapshot: DataSnapshot) {
        var count  = 0
        for (ds in snapshot.children){
//            val model = ds.getValue(ChatModel::class.java)!!
//            chatList.add(model)
//            previousKey = ds.key!!
//            Log.d("dsKey",previousKey)
//            val previouspos = chatList.size - 1
//            adapter.notifyItemInserted(previouspos)
//            chatRecyclerView.scrollToPosition(previouspos)
        }
    }

    override fun onCancelled(error: DatabaseError) {
        TODO("Not yet implemented")
    }

})

        //  chatList.clear()
      val messageSwipeController = MessageSwipeController(this,object : SwipeControllerActions() {
          override fun showReplyUI(position: Int) {
              quotePos = position
    showMessgae(chatList.get(position))
          }

      })
        val itemTouchHelper = ItemTouchHelper(messageSwipeController)
        itemTouchHelper.attachToRecyclerView(chatRecyclerView)
cancelBtn.setOnClickListener(object : View.OnClickListener{
    override fun onClick(p0: View?) {
        replyLayout.visibility = GONE
    }

})
        adapter = ChatAdapter(chatList)
        chatRecyclerView.adapter = adapter
        Log.d("kk",chatList.size.toString())
        chatEdt.text.clear()


        chatBtn.setOnClickListener(object : View.OnClickListener {

            override fun onClick(p0: View?) {
                var s = System.currentTimeMillis()
                dateformat = SimpleDateFormat("d,MMM,y,hh:mm:ss a", Locale.ENGLISH).format(s)
                if (!chatEdt.text.isEmpty()){
                    if (replyLayout.visibility == VISIBLE){
                        replyLayout.visibility = GONE

                        val msg : String = chatEdt.text.toString()
                        val map = HashMap<String,Any>()
                        map.put("message",msg)
                        map.put("type","sender")
                        map.put("dateFormat",ServerValue.TIMESTAMP)
                        map.put("quotepos",quotePos)
                        map.put("quote",txtQuotedmsg.text.toString())


                        FirebaseDatabase.getInstance().getReference().child("Chats").push()
                            .updateChildren(map)
                    }else{
                        val msg: String = chatEdt.text.toString()
                        val map = HashMap<String, Any>()
                        map.put("message", msg)
                        map.put("type", "sender")
                        map.put("dateFormat",ServerValue.TIMESTAMP)
                        FirebaseDatabase.getInstance().getReference().child("Chats").push()
                            .updateChildren(map)
                    }


                }else{
                    Toast.makeText(this@MainActivity,"Please enter Message",Toast.LENGTH_SHORT).show()
                }
                chatEdt.text.clear()


            }

        })



        rcvdBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var s = System.currentTimeMillis()
                dateformat = SimpleDateFormat("d,MMM,y,hh:mm:ss a", Locale.ENGLISH).format(s)
                if (!chatEdt.text.isEmpty()){
                    if (replyLayout.visibility == VISIBLE){
                        replyLayout.visibility = GONE
                        val msg : String = chatEdt.text.toString()
                        val map = HashMap<String,Any>()
                        map.put("message",msg)
                        map.put("type","receiver")
                        map.put("dateFormat",ServerValue.TIMESTAMP)
                        map.put("quotepos",quotePos)
                        map.put("quote",txtQuotedmsg.text.toString())

                        FirebaseDatabase.getInstance().getReference().child("Chats").push()
                            .updateChildren(map)
                    }else{
                        val msg: String = chatEdt.text.toString()
                        val map = HashMap<String, Any>()
                        map.put("message", msg)
                        map.put("type", "receiver")
                        map.put("dateFormat",ServerValue.TIMESTAMP)
                       // FirebaseDatabase.getInstance().getReference().push().key!!
                        FirebaseDatabase.getInstance().getReference().child("Chats").push()
                            .updateChildren(map)
                    }


                }else{
                    Toast.makeText(this@MainActivity,"Please enter Message",Toast.LENGTH_SHORT).show()
                }
                chatEdt.text.clear()

            }
        })

chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            isScrolling = true
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        currentItem = manager.childCount
        scrolledItem = manager.findFirstVisibleItemPosition()
        totalItem = manager.itemCount
        currentPage++
        if (isScrolling && ( scrolledItem == 0) && !endResult ){

            isScrolling = false
            chtPrgsBar.visibility = VISIBLE
            Log.d("newkey",key.toString())


        val ref2 = FirebaseDatabase.getInstance().getReference().child("Chats").limitToLast(10).orderByKey().endBefore(key)

        ref2.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                var count = 0
                for (ds in snapshot.children) {
                    count++
                    if (snapshot.childrenCount < 10) {
                        endResult = true
                    } else if (count == 1) {
                        key = ds.key!!
                        Log.d("itemKey", ds.key!!)

                    }
                        Log.d("values", snapshot.childrenCount.toString())
                        var model = ds.getValue(ChatModel::class.java)!!
                        Log.d("model1", model.toString())


    tempChatList.add(model)


                    }
chatList.addAll(0,tempChatList)
                tempChatList.clear()





                adapter.notifyDataSetChanged()

                chtPrgsBar.visibility = GONE
                Log.d("count", count.toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })




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
    fun showMessgae(chatModel: ChatModel){
        chatEdt.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(chatEdt, InputMethodManager.SHOW_IMPLICIT)
        txtQuotedmsg.text = chatModel.message

            replyLayout.visibility = View.VISIBLE

    }

    override fun onQuoteClick(position: Int) {
        chatRecyclerView.smoothScrollToPosition(position - 1)

    }


}

