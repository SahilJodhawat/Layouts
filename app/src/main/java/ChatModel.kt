import android.content.BroadcastReceiver

/**
 * Created by mohammad sajjad on 28-04-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

data class ChatModel(val message : String? = "",val type : String? = "",val quotepos : Int? = -1,
                     val quote : String? = "")
//{
//var quotepos : Int = -1
//    var quote : String = ""
//    constructor(message: String?,type: String?,quote : String,quotepos : Int) : this(message,type){
//        this.quotepos = quotepos
//        this.quote = quote
//    }
//}
