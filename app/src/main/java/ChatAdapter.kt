import android.app.AlertDialog
import android.content.DialogInterface
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
import org.w3c.dom.Text

/**
 * Created by mohammad sajjad on 28-04-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

open class ChatAdapter( chatlist : ArrayList<ChatModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            (holder as SenderViewHolder).senderTxt.text =
                chatList.get(holder.adapterPosition).message
            (holder as SenderViewHolder).senderTxt.setOnLongClickListener(object : View.OnLongClickListener{
                override fun onLongClick(p0: View?): Boolean {
                    val builder : AlertDialog.Builder = AlertDialog.Builder(p0!!.context)
                    builder.setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes",object : DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                var msg = chatList.get(position).message
                                Log.d("msg",msg.toString())
                                Log.d("pos",(holder as SenderViewHolder).pos.toString())
                                val ref = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("message").equalTo(msg)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("refKey",snapshot.value.toString())
                                        var count = 0
                                        for (ds in snapshot.children){
                                            Log.d("refKey1",ds.value.toString())
                                            Log.d("key3",ds.key!!)
//                                    val map = HashMap<String,Any>()
//                                            map.put("message","this is message is deleted")
//                                            map.put("type","sender")
//                                            ds.ref.updateChildren(map)

ds.ref.removeValue()


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

            (holder as ReceiverViewHolder).receiverTxt.setOnLongClickListener(object : View.OnLongClickListener{
                override fun onLongClick(p0: View?): Boolean {
                    val builder : AlertDialog.Builder = AlertDialog.Builder(p0!!.context)
                    builder.setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes",object : DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                var msg = chatList.get(holder.adapterPosition).message
                                Log.d("msg",msg.toString())
//                                Log.d("pos",(holder as SenderViewHolder).pos.toString())
                                val ref = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("message").equalTo(msg)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("refKey",snapshot.value.toString())
                                        var count = 0
                                        for (ds in snapshot.children){
                                            Log.d("refKey1",ds.value.toString())
                                            Log.d("key3",ds.key!!)
                                            ds.ref.removeValue()



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
            (holder as SenderReplyViewHolder).reply.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    mQuoteClickListener!!.onQuoteClick(chatList.get(holder.adapterPosition).quotepos!!)

                }

            })
            (holder as SenderReplyViewHolder).reply.setOnLongClickListener(object : View.OnLongClickListener{
                override fun onLongClick(p0: View?): Boolean {
                    val builder : AlertDialog.Builder = AlertDialog.Builder(p0!!.context)
                    builder.setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes",object : DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                var msg = chatList.get(holder.adapterPosition).message
                                Log.d("msg",msg.toString())
//                                Log.d("pos",(holder as SenderViewHolder).pos.toString())
                                val ref = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("message").equalTo(msg)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("refKey",snapshot.value.toString())
                                        var count = 0
                                        for (ds in snapshot.children){
                                            Log.d("refKey1",ds.value.toString())
                                            Log.d("key3",ds.key!!)
                                            ds.ref.removeValue()



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
                                Log.d("msg",msg.toString())
//                                Log.d("pos",(holder as SenderViewHolder).pos.toString())
                                val ref = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("message").equalTo(msg)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("refKey",snapshot.value.toString())
                                        var count = 0
                                        for (ds in snapshot.children){
                                            Log.d("refKey1",ds.value.toString())
                                            Log.d("key3",ds.key!!)
                                            ds.ref.removeValue()



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
          }

else

return MSG_REPLY_LEFT

    }

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val senderTxt : TextView = itemView.findViewById(R.id.sender_txt)

var pos = adapterPosition

    }
    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiverTxt : TextView = itemView.findViewById(R.id.receiver_txt)
    }

    inner class SenderReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
val sndReplyTxt : TextView = itemView.findViewById(R.id.txtBody)
        val QuotedTxt : TextView = itemView.findViewById(R.id.textQuote)
        val reply : ConstraintLayout = itemView.findViewById(R.id.reply)
    }

    inner class ReceiverReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sndReplyTxt : TextView = itemView.findViewById(R.id.txtBody1)
        val QuotedTxt : TextView = itemView.findViewById(R.id.textQuote1)
        val reply1 : ConstraintLayout = itemView.findViewById(R.id.reply1)
    }

    fun addMoreItems(newItems : ArrayList<ChatModel>){
        val previousItems = chatList.size
          chatList.addAll(newItems)

        notifyItemRangeInserted(previousItems,newItems.size)
    }

}