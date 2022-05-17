import android.app.AlertDialog
import android.content.DialogInterface
import android.provider.CalendarContract
import android.text.TextUtils
import android.text.format.DateFormat.format
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.layouts.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.internal.Util
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import java.lang.String.format
import java.text.DateFormat


/**
 * Created by mohammad sajjad on 28-04-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

open class ChatAdapter( chatlist : ArrayList<ChatModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val DATE_MSG: Int = 4
    val MSG_RIGHT = 0
    val MSG_LEFT = 1
    val MSG_REPLY_RIGHT = 2
    val MSG_REPLY_LEFT = 3
    var key : String = ""

    var chatList : ArrayList<ChatModel> = chatlist
    interface QuoteClickListener {


        fun onQuoteClick(position: Int)
    }
    private var mQuoteClickListener: QuoteClickListener? = null

    fun setQuoteClickListener(listener: QuoteClickListener) {
        mQuoteClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
   if (viewType == MSG_RIGHT){
       return SenderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sender_chat,parent,false))
   }else if (viewType == MSG_REPLY_RIGHT){
       return SenderReplyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sender_reply,parent,false))
   }
        if (viewType == MSG_LEFT){
            return ReceiverViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.receiver_chat,parent,false))
        }
        return ReceiverReplyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.receiver_reply,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        if (chatList.get(holder.adapterPosition).type.equals("sender") && chatList.get(holder.adapterPosition).quotepos == -1) {
            var cal1 = Calendar.getInstance()
            var cal2 = Calendar.getInstance()
            (holder as SenderViewHolder).senderTxt.text =
                chatList.get(holder.adapterPosition).message
            holder.senderMsgTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(
                chatList.get(holder.adapterPosition).dateFormat!!
            ))
var previousMsg : Long = 0
            if (holder.adapterPosition > 1){
                previousMsg = chatList.get(holder.adapterPosition - 1).dateFormat!!
            }
            if (previousMsg == 0L){
                holder.date.visibility = View.VISIBLE
                val date = SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss a").format(
                    Date(chatList.get(holder.adapterPosition).dateFormat!!)
                )
                (holder as SenderViewHolder).date.text = date.substring(0,6)
                Log.d("date",date)
            }

            else {

                cal1.timeInMillis = chatList.get(holder.adapterPosition).dateFormat!!
                cal2.timeInMillis = previousMsg

                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
                        Calendar.DAY_OF_YEAR
                    )
                ) {
                    (holder as SenderViewHolder).date.visibility = View.GONE
                    (holder as SenderViewHolder).date.text = ""
                    Log.d("cal1value",cal1.get(Calendar.YEAR).toString() + " " +cal2.get(Calendar.YEAR))
                    Log.d("cal2value",cal2.get(Calendar.MONTH).toString() + " " +cal1.get(Calendar.MONTH))

                } else {
                    (holder as SenderViewHolder).date.visibility = View.VISIBLE
                    (holder as SenderViewHolder).date.text =
                        SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss a")
                            .format(Date(chatList.get(holder.adapterPosition).dateFormat!!)).substring(0,6)
//                    Log.d(
//                        "prevDate", SimpleDateFormat("dd-MM-yyyy hh:mm:ss a")
//                            .format(Date(previousMsg)).toString()
                    //)

                }
            }
//            Log.d("cal1value",cal1.get(Calendar.YEAR).toString() + " " +cal2.get(Calendar.YEAR))
//            Log.d("cal2value",cal2.get(Calendar.MONTH).toString() + " " +cal1.get(Calendar.MONTH))
            Log.d(
                "prevDate", SimpleDateFormat("dd:MM:yy hh:mm:ss a").format(Date(chatList.get(holder.adapterPosition).dateFormat!!))
                    )



            (holder as SenderViewHolder).senderTxt.setOnLongClickListener(object : View.OnLongClickListener{
                override fun onLongClick(p0: View?): Boolean {
                    val builder : AlertDialog.Builder = AlertDialog.Builder(p0!!.context)
                    builder.setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes",object : DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                var msg = chatList.get(holder.adapterPosition).message
                                Log.d("msg1",msg.toString())
//                                Log.d("pos",(holder as SenderViewHolder).pos.toString())
                                val ref = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("message").equalTo(msg)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("refKey",snapshot.value.toString())
                                        var count = 0
                                        for (ds in snapshot.children){
                                            Log.d("refKey1",ds.value.toString())
                                            Log.d("key3",ds.key!!)
                                            val map = HashMap<String,Any>()
                                            map.put("message","this is message is deleted")
                                            map.put("type","sender")
                                            ds.ref.updateChildren(map)




                                        }
                                        chatList.remove(chatList.get(holder.adapterPosition))
                                        notifyItemRemoved(holder.adapterPosition)

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                            }

                        })
                        .setNegativeButton("No",null)
                        .show()



                    return true
                }

            })



        }else if(chatList.get(holder.adapterPosition).type.equals("receiver") && chatList.get(holder.adapterPosition).quotepos == -1){
            (holder as ReceiverViewHolder).receiverTxt.text =
                chatList.get(holder.adapterPosition).message
            holder.receiverMsgTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(
                chatList.get(holder.adapterPosition).dateFormat!!
            ))

            var previousMsg : Long? = 0
            if (holder.adapterPosition != 0){
                previousMsg = chatList.get(holder.adapterPosition - 1).dateFormat!!
            }
            if (previousMsg == 0L){
                (holder as ReceiverViewHolder).date1.visibility = View.VISIBLE
                val date = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH).format(
                    Date(chatList.get(holder.adapterPosition).dateFormat!!)
                )
                (holder as ReceiverViewHolder).date1.text = date
            }else{
                val cal1 = Calendar.getInstance()
                val cal2 = Calendar.getInstance()
                cal1.timeInMillis = chatList.get(holder.adapterPosition).dateFormat!!
                cal2.timeInMillis = previousMsg!!

                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)){
                    (holder as ReceiverViewHolder).date1.visibility = View.GONE
                    (holder as ReceiverViewHolder).date1.text = ""

                }else{
                    (holder as ReceiverViewHolder).date1.visibility = View.VISIBLE
                    (holder as ReceiverViewHolder).date1.text = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH)
                        .format(Date(chatList.get(holder.adapterPosition).dateFormat!!)).substring(0,10)
                }


            }
            (holder as ReceiverViewHolder).receiverTxt.setOnLongClickListener(object : View.OnLongClickListener{
                override fun onLongClick(p0: View?): Boolean {
                    val builder : AlertDialog.Builder = AlertDialog.Builder(p0!!.context)
                    builder.setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes",object : DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                var msg = chatList.get(holder.adapterPosition).message
                                Log.d("msg1",msg.toString())
//                                Log.d("pos",(holder as SenderViewHolder).pos.toString())
                                val ref = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("message").equalTo(msg)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("refKey",snapshot.value.toString())
                                        var count = 0
                                        for (ds in snapshot.children){
                                            Log.d("refKey1",ds.value.toString())
                                            Log.d("key3",ds.key!!)
                                            val map = HashMap<String,Any>()
                                            map.put("message","this is message is deleted")
                                            map.put("type","receiver")
                                            ds.ref.updateChildren(map)




                                        }
                                        chatList.remove(chatList.get(holder.adapterPosition))
                                        notifyItemRemoved(holder.adapterPosition)

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                            }

                        })
                        .setNegativeButton("No",null)
                        .show()



                    return true
                }

            })
        }
        if (chatList.get(holder.adapterPosition).quotepos != -1 && chatList.get(holder.adapterPosition).type.equals("sender")){
            (holder as SenderReplyViewHolder).QuotedTxt.text = chatList.get(position).quote
            (holder as SenderReplyViewHolder).sndReplyTxt.text = chatList.get(position).message
            (holder as SenderReplyViewHolder).senderReplyMsgTime.text = SimpleDateFormat("hh:mm a",
                Locale.getDefault()).format(Date(chatList.get(holder.adapterPosition).dateFormat!!))
            (holder as SenderReplyViewHolder).reply.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    mQuoteClickListener!!.onQuoteClick(chatList.get(holder.adapterPosition).quotepos!!)

                }

            })

            var previousMsg : Long? = 0
            if (previousMsg == 0L){
                previousMsg = chatList.get(holder.adapterPosition - 1).dateFormat!!
            }
            if (holder.adapterPosition == 0){
                (holder as SenderReplyViewHolder).date2.visibility = View.VISIBLE
                val date = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH).format(
                    Date(chatList.get(holder.adapterPosition).dateFormat!!)
                )
                (holder as SenderReplyViewHolder).date2.text = date
            }else{
                val cal1 = Calendar.getInstance()
                val cal2 = Calendar.getInstance()
                cal1.timeInMillis = chatList.get(holder.adapterPosition).dateFormat!!
                cal2.timeInMillis = previousMsg!!

                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)){
                    (holder as SenderReplyViewHolder).date2.visibility = View.GONE
                    (holder as SenderReplyViewHolder).date2.text = ""

                }else{
                    (holder as SenderReplyViewHolder).date2.visibility = View.VISIBLE
                    (holder as SenderReplyViewHolder).date2.text = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH)
                        .format(Date(chatList.get(holder.adapterPosition).dateFormat!!))
                }


            }
            (holder as SenderReplyViewHolder).reply.setOnLongClickListener(object : View.OnLongClickListener{
                override fun onLongClick(p0: View?): Boolean {
                    val builder : AlertDialog.Builder = AlertDialog.Builder(p0!!.context)
                    builder.setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes",object : DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                var msg = chatList.get(holder.adapterPosition).message
                                Log.d("msg1",msg.toString())
//                                Log.d("pos",(holder as SenderViewHolder).pos.toString())
                                val ref = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("message").equalTo(msg)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("refKey",snapshot.value.toString())
                                        var count = 0
                                        for (ds in snapshot.children){
                                            Log.d("refKey1",ds.value.toString())
                                            Log.d("key3",ds.key!!)
                                            val map = HashMap<String,Any>()
                                            map.put("message","this is message is deleted")
                                            map.put("type","sender")
                                            map.put("quotepos",holder.adapterPosition)
                                            map.put("quote","")
                                            ds.ref.updateChildren(map)
                                            notifyItemChanged(holder.adapterPosition)



                                        }
                                        chatList.remove(chatList.get(holder.adapterPosition))
                                        notifyItemRemoved(holder.adapterPosition)


                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                            }

                        })
                        .setNegativeButton("No",null)
                        .show()

                    return true
                }

            })
        }else if(chatList.get(holder.adapterPosition).quotepos != -1 && chatList.get(holder.adapterPosition).type
                .equals("receiver")){
            var previousMsg : Long? = 0
            if (holder.adapterPosition != 0){
                previousMsg = chatList.get(holder.adapterPosition - 1).dateFormat!!
            }
            if (previousMsg == 0L){
                (holder as ReceiverReplyViewHolder).date3.visibility = View.VISIBLE
                val date = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH).format(
                    Date(chatList.get(holder.adapterPosition).dateFormat!!)
                )
                (holder as ReceiverReplyViewHolder).date3.text = date
            }else{
                val cal1 = Calendar.getInstance()
                val cal2 = Calendar.getInstance()
                cal1.timeInMillis = chatList.get(holder.adapterPosition).dateFormat!!
                cal2.timeInMillis = previousMsg!!

                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)){
                    (holder as ReceiverReplyViewHolder).date3.visibility = View.GONE
                    (holder as ReceiverReplyViewHolder).date3.text = ""

                }else{
                    (holder as ReceiverReplyViewHolder).date3.visibility = View.VISIBLE
                    (holder as ReceiverReplyViewHolder).date3.text = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH)
                        .format(chatList.get(holder.adapterPosition).dateFormat!!)
                }


            }
            (holder as ReceiverReplyViewHolder).sndReplyTxt.text = chatList.get(position).message
            (holder as ReceiverReplyViewHolder).QuotedTxt.text = chatList.get(position).quote
            (holder as ReceiverReplyViewHolder).reply1.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    mQuoteClickListener!!.onQuoteClick(chatList.get(position).quotepos!!)
                }

            })
            (holder as ReceiverReplyViewHolder).reply1.setOnLongClickListener(object : View.OnLongClickListener{
                override fun onLongClick(p0: View?): Boolean {
                    val builder : AlertDialog.Builder = AlertDialog.Builder(p0!!.context)
                    builder.setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes",object : DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                var msg = chatList.get(holder.adapterPosition).message
                                Log.d("msg1",msg.toString())
//                                Log.d("pos",(holder as SenderViewHolder).pos.toString())
                                val ref = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("message").equalTo(msg)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("refKey",snapshot.value.toString())
                                        var count = 0
                                        for (ds in snapshot.children){
                                            val map = HashMap<String,Any>()
                                            map.put("message","this is message is deleted")
                                            map.put("type","receiver")
                                            map.put("quotepos",holder.adapterPosition)
                                            map.put("quote","")
                                            ds.ref.updateChildren(map)
                                            notifyItemChanged(holder.adapterPosition)



                                        }
                                        chatList.remove(chatList.get(holder.adapterPosition))
                                        notifyItemRemoved(holder.adapterPosition)


                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                            }

                        })
                        .setNegativeButton("No",null)
                        .show()

                    return true
                }

            })
        }
    }

    override fun getItemCount(): Int {
       return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (chatList.get(position).type.equals("sender") && chatList.get(position).quotepos == -1) {
            return MSG_RIGHT
        }else if (chatList.get(position).type.equals("sender") && chatList.get(position).quotepos != -1){
            return MSG_REPLY_RIGHT
        }

          if (chatList.get(position).type.equals("receiver") && chatList.get(position).quotepos == -1){
              return MSG_LEFT
          }else {
              return MSG_REPLY_LEFT
          }



    }

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val senderTxt : TextView = itemView.findViewById(R.id.sender_txt)
        val date : TextView = itemView.findViewById(R.id.date)
        val senderMsgTime : TextView = itemView.findViewById(R.id.sender_msg_time)


var pos = adapterPosition

    }
    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiverTxt : TextView = itemView.findViewById(R.id.receiver_txt)
        val date1 : TextView = itemView.findViewById(R.id.date1)
        val receiverMsgTime : TextView = itemView.findViewById(R.id.receiver_msg_time)
    }

    inner class SenderReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
val sndReplyTxt : TextView = itemView.findViewById(R.id.txtBody)
        val QuotedTxt : TextView = itemView.findViewById(R.id.textQuote)
        val reply : ConstraintLayout = itemView.findViewById(R.id.reply)
        val date2 : TextView = itemView.findViewById(R.id.date2)
        val senderReplyMsgTime : TextView = itemView.findViewById(R.id.sender_reply_msg_time)
    }

    inner class ReceiverReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sndReplyTxt : TextView = itemView.findViewById(R.id.txtBody1)
        val QuotedTxt : TextView = itemView.findViewById(R.id.textQuote1)
        val reply1 : ConstraintLayout = itemView.findViewById(R.id.reply1)
        val date3 : TextView = itemView.findViewById(R.id.date3)
    }


    fun addMoreItems(newItems : ArrayList<ChatModel>){
        val previousItems = chatList.size
          chatList.addAll(newItems)

        notifyItemRangeInserted(previousItems,newItems.size)
    }

}



