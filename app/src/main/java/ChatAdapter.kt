import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.layouts.R

/**
 * Created by mohammad sajjad on 28-04-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

class ChatAdapter( chatlist : ArrayList<ChatModel>,) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val MSG_RIGHT = 0
    val MSG_LEFT = 1

    var chatList : ArrayList<ChatModel> = chatlist

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MSG_RIGHT){
            return SenderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sender_chat,parent,false))
        }
            return ReceiverViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.receiver_chat,parent,false))


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (chatList.get(holder.adapterPosition).type.equals("sender")) {
            (holder as SenderViewHolder).senderTxt.text =
                chatList.get(holder.adapterPosition).message
        }else {
            (holder as ReceiverViewHolder).receiverTxt.text =
                chatList.get(holder.adapterPosition).message
        }
    }

    override fun getItemCount(): Int {
       return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (chatList.get(position).type.equals("sender")){
            return MSG_RIGHT
        }else{
            return MSG_LEFT
        }

    }

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val senderTxt : TextView = itemView.findViewById(R.id.sender_txt)

    }
    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiverTxt : TextView = itemView.findViewById(R.id.receiver_txt)
    }

    fun addMoreItems(newItems : ArrayList<ChatModel>){
        val previousItems = chatList.size
          chatList.addAll(newItems)

        notifyItemRangeInserted(previousItems,newItems.size)
    }

}