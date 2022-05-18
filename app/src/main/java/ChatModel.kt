import android.content.BroadcastReceiver

/**
 * Created by mohammad sajjad on 28-04-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

data class ChatModel(
    var dateFormat : Long? = 0,var mediaType : String? = "", var message : String? = "", var type : String? = "", var quotepos : Int? = -1,
    var quote : String? = "")
//{
//var quotepos : Int = -1
//    var quote : String = ""
//    constructor(message: String?,type: String?,quote : String,quotepos : Int) : this(message,type){
//        this.quotepos = quotepos
//        this.quote = quote
//    }
//}
