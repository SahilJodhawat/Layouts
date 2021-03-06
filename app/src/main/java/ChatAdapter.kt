import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.provider.CalendarContract
import android.provider.MediaStore
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.text.format.DateFormat.format
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.chibde.visualizer.LineBarVisualizer
import com.example.layouts.AndroidUtills
import com.example.layouts.R
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.gms.common.util.IOUtils
import com.google.firebase.database.*
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import linc.com.amplituda.Amplituda
import linc.com.amplituda.AmplitudaResult
import okhttp3.internal.Util
import org.w3c.dom.Text
import rm.com.audiowave.AudioWaveView
import rm.com.audiowave.OnProgressListener
import rm.com.audiowave.OnSamplingListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import java.lang.String.format
import java.net.URL
import java.text.DateFormat
import java.util.concurrent.TimeUnit


/**
 * Created by mohammad sajjad on 28-04-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

open class ChatAdapter(val context: Context, chatlist: ArrayList<ChatModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var timer: CountDownTimer
    val MSG_RIGHT = 0
    val MSG_LEFT = 1
    val MSG_REPLY_RIGHT = 2
    val MSG_REPLY_LEFT = 3
    val IMG_MSG = 4
    val AUDIO_MSG = 5
    var mediaPlayer: MediaPlayer? = null
    var player: ExoPlayer? = null
    var bytes: ByteArray? = null
    var previous = 0
    var currentPlayingPosition = -1
    var playingHolder: SenderAudioViewHolder? = null

    //    lateinit var progressAnim : ObjectAnimator
    var duration: Long = 0
    var chatList: ArrayList<ChatModel> = chatlist

    interface QuoteClickListener {


        fun onQuoteClick(position: Int)
    }

    private var mQuoteClickListener: QuoteClickListener? = null

    fun setQuoteClickListener(listener: QuoteClickListener) {
        mQuoteClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MSG_RIGHT) {
            return SenderViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.sender_chat, parent, false)
            )
        } else if (viewType == MSG_REPLY_RIGHT) {
            return SenderReplyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.sender_reply, parent, false)
            )
        }
        if (viewType == IMG_MSG) {
            return SenderImgViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.send_image_layout, parent, false
                )
            )

        }
        if (viewType == MSG_LEFT) {
            return ReceiverViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.receiver_chat, parent, false)
            )
        }
        if (viewType == AUDIO_MSG) {
            return SenderAudioViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.sender_audio_message, parent, false)
            )
        }

        return ReceiverReplyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.receiver_reply, parent, false)
        )


    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (chatList.get(holder.bindingAdapterPosition).type.equals("sender")
            && chatList.get(holder.adapterPosition).quotepos == -1
        ) {
            var cal1 = Calendar.getInstance()
            var cal2 = Calendar.getInstance()
            val currentDate = Calendar.getInstance()
            val prevDate = Calendar.getInstance()
            prevDate.add(Calendar.DATE, -1)

            (holder as SenderViewHolder).senderTxt.text =
                chatList.get(holder.adapterPosition).message
            holder.senderMsgTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                Date(
                    chatList.get(holder.adapterPosition).dateFormat!!
                )
            )
            var previousMsg: Long = 0
            if (holder.adapterPosition > 1) {
                previousMsg = chatList.get(holder.adapterPosition - 1).dateFormat!!
            }
            if (previousMsg == 0L) {
                holder.date.visibility = View.VISIBLE
                val date = SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss a").format(
                    Date(chatList.get(holder.adapterPosition).dateFormat!!)
                )
                (holder as SenderViewHolder).date.text = date.substring(0, 6)
                Log.d("date", date)
            } else {

                cal1.timeInMillis = chatList.get(holder.adapterPosition).dateFormat!!
                cal2.timeInMillis = previousMsg


                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
                        Calendar.DAY_OF_YEAR
                    )
                ) {
                    (holder as SenderViewHolder).date.visibility = View.GONE
                    (holder as SenderViewHolder).date.text = ""
                    Log.d(
                        "cal1value",
                        cal1.get(Calendar.YEAR).toString() + " " + cal2.get(Calendar.YEAR)
                    )
                    Log.d(
                        "cal2value",
                        cal2.get(Calendar.MONTH).toString() + " " + cal1.get(Calendar.MONTH)
                    )

                } else {
                    (holder as SenderViewHolder).date.visibility = View.VISIBLE
                    if (cal1.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) && cal1.get(
                            Calendar.MONTH
                        )
                        == currentDate.get(Calendar.MONTH)
                        && cal1.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)
                    ) {
                        (holder as SenderViewHolder).date.text = "Today"

//                    Log.d(
//                        "prevDate", SimpleDateFormat("dd-MM-yyyy hh:mm:ss a")
//                            .format(Date(previousMsg)).toString()
                        //)
                    } else if (cal1.get(Calendar.YEAR) == prevDate.get(Calendar.YEAR) && cal1.get(
                            Calendar.MONTH
                        )
                        == prevDate.get(Calendar.MONTH) &&
                        cal1.get(Calendar.DAY_OF_YEAR) == prevDate.get(Calendar.DAY_OF_YEAR)
                    ) {
                        (holder as SenderViewHolder).date.text = "Yesterday"
                    } else {
                        (holder as SenderViewHolder).date.text =
                            SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss a", Locale.ENGLISH).format(
                                Date(chatList.get(holder.adapterPosition).dateFormat!!)
                            ).substring(0, 6)
                    }

                }
            }
//            Log.d("cal1value",cal1.get(Calendar.YEAR).toString() + " " +cal2.get(Calendar.YEAR))
//            Log.d("cal2value",cal2.get(Calendar.MONTH).toString() + " " +cal1.get(Calendar.MONTH))
            Log.d(
                "prevDate",
                SimpleDateFormat("dd:MM:yy hh:mm:ss a").format(Date(chatList.get(holder.adapterPosition).dateFormat!!))
            )



            (holder as SenderViewHolder).senderTxt.setOnLongClickListener(object :
                View.OnLongClickListener {
                override fun onLongClick(p0: View?): Boolean {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(p0!!.context)
                    builder.setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                var msg = chatList.get(holder.adapterPosition).message
                                Log.d("msg1", msg.toString())
//                                Log.d("pos",(holder as SenderViewHolder).pos.toString())
                                val ref =
                                    FirebaseDatabase.getInstance().getReference().child("Chats")
                                        .orderByChild("message").equalTo(msg)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("refKey", snapshot.value.toString())
                                        var count = 0
                                        for (ds in snapshot.children) {
                                            Log.d("refKey1", ds.value.toString())
                                            Log.d("key3", ds.key!!)
                                            val map = HashMap<String, Any>()
                                            map.put("message", "this is message is deleted")
                                            map.put("type", "sender")
                                            ds.ref.updateChildren(map)


                                        }
                                        chatList.remove(chatList.get(holder.adapterPosition))
                                        // notifyItemRemoved(holder.adapterPosition)

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                            }

                        })
                        .setNegativeButton("No", null)
                        .show()



                    return true
                }

            })


        } else if (chatList.get(holder.adapterPosition).type.equals("receiver") && chatList.get(
                holder.adapterPosition
            ).quotepos == -1
        ) {
            (holder as ReceiverViewHolder).receiverTxt.text =
                chatList.get(holder.adapterPosition).message
            holder.receiverMsgTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                Date(
                    chatList.get(holder.adapterPosition).dateFormat!!
                )
            )

            var previousMsg: Long? = 0
            if (holder.adapterPosition != 0) {
                previousMsg = chatList.get(holder.adapterPosition - 1).dateFormat!!
            }
            if (previousMsg == 0L) {
                (holder as ReceiverViewHolder).date1.visibility = View.VISIBLE
                val date = SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss a", Locale.ENGLISH).format(
                    Date(chatList.get(holder.adapterPosition).dateFormat!!)
                )
                (holder as ReceiverViewHolder).date1.text = date.substring(0, 6)
            } else {
                val cal1 = Calendar.getInstance()
                val cal2 = Calendar.getInstance()
                cal1.timeInMillis = chatList.get(holder.adapterPosition).dateFormat!!
                cal2.timeInMillis = previousMsg!!
                val currentDate = Calendar.getInstance()
                val prevDate = Calendar.getInstance()
                prevDate.add(Calendar.DATE, -1)

                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
                        Calendar.DAY_OF_YEAR
                    )
                ) {
                    (holder as ReceiverViewHolder).date1.visibility = View.GONE
                    (holder as ReceiverViewHolder).date1.text = ""

                } else {
                    (holder as ReceiverViewHolder).date1.visibility = View.VISIBLE
                    if (cal1.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) && cal1.get(
                            Calendar.MONTH
                        )
                        == currentDate.get(Calendar.MONTH)
                        && cal1.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)
                    ) {
                        (holder as ReceiverViewHolder).date1.text = "Today"

//                    Log.d(
//                        "prevDate", SimpleDateFormat("dd-MM-yyyy hh:mm:ss a")
//                            .format(Date(previousMsg)).toString()
                        //)
                    } else if (cal1.get(Calendar.YEAR) == prevDate.get(Calendar.YEAR) && cal1.get(
                            Calendar.MONTH
                        )
                        == prevDate.get(Calendar.MONTH) &&
                        cal1.get(Calendar.DAY_OF_YEAR) == prevDate.get(Calendar.DAY_OF_YEAR)
                    ) {
                        (holder as ReceiverViewHolder).date1.text = "Yesterday"
                    } else {
                        (holder as ReceiverViewHolder).date1.text =
                            SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss a", Locale.ENGLISH)
                                .format(Date(chatList.get(holder.adapterPosition).dateFormat!!))
                                .substring(0, 6)
                    }
                }


            }
            (holder as ReceiverViewHolder).receiverTxt.setOnLongClickListener(object :
                View.OnLongClickListener {
                override fun onLongClick(p0: View?): Boolean {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(p0!!.context)
                    builder.setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                var msg = chatList.get(holder.adapterPosition).message
                                Log.d("msg1", msg.toString())
//                                Log.d("pos",(holder as SenderViewHolder).pos.toString())
                                val ref =
                                    FirebaseDatabase.getInstance().getReference().child("Chats")
                                        .orderByChild("message").equalTo(msg)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("refKey", snapshot.value.toString())
                                        var count = 0
                                        for (ds in snapshot.children) {
                                            Log.d("refKey1", ds.value.toString())
                                            Log.d("key3", ds.key!!)
                                            val map = HashMap<String, Any>()
                                            map.put("message", "this is message is deleted")
                                            map.put("type", "receiver")
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
                        .setNegativeButton("No", null)
                        .show()



                    return true
                }

            })
        }
        if (chatList.get(holder.adapterPosition).quotepos != -1 && chatList.get(holder.adapterPosition).type.equals(
                "sender"
            )
        ) {


            if (chatList.get(holder.adapterPosition).mediaType.equals("image")) {
                Glide.with(context).load(Uri.parse(chatList.get(holder.adapterPosition).quote))
                    .override(400, 150)
                    .into((holder as SenderReplyViewHolder).quotedImgSndr)
                holder.sndReplyTxt.text = chatList.get(position).message
                holder.name.text = chatList.get(holder.adapterPosition).type
                holder.QuotedTxt.visibility = View.GONE
            } else {
                (holder as SenderReplyViewHolder).QuotedTxt.visibility = View.VISIBLE
                (holder as SenderReplyViewHolder).QuotedTxt.text = chatList.get(position).quote
                (holder as SenderReplyViewHolder).sndReplyTxt.text = chatList.get(position).message
                holder.name.text = chatList.get(holder.adapterPosition).type
                holder.quotedImgSndr.visibility = View.GONE
            }

            (holder as SenderReplyViewHolder).senderReplyMsgTime.text = SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
            ).format(Date(chatList.get(holder.adapterPosition).dateFormat!!))
//            (holder as SenderReplyViewHolder).reply.setOnClickListener(object :
//                View.OnClickListener {
//                override fun onClick(p0: View?) {
//                    mQuoteClickListener!!.onQuoteClick(chatList.get(holder.adapterPosition).quotepos!!)
//
//                }
//
//            })

            var previousMsg: Long? = 0
            if (holder.adapterPosition != 0) {
                previousMsg = chatList.get(holder.adapterPosition - 1).dateFormat!!
            }
            if (previousMsg == 0L) {
                (holder as SenderReplyViewHolder).date2.visibility = View.VISIBLE
                val date = SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss a", Locale.ENGLISH).format(
                    Date(chatList.get(holder.adapterPosition).dateFormat!!)
                )
                (holder as SenderReplyViewHolder).date2.text = date
            } else {
                val cal1 = Calendar.getInstance()
                val cal2 = Calendar.getInstance()
                cal1.timeInMillis = chatList.get(holder.adapterPosition).dateFormat!!
                cal2.timeInMillis = previousMsg!!
                val currentDate = Calendar.getInstance()
                val prevDate = Calendar.getInstance()
                prevDate.add(Calendar.DATE, -1)

                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
                        Calendar.DAY_OF_YEAR
                    )
                ) {
                    (holder as SenderReplyViewHolder).date2.visibility = View.GONE
                    (holder as SenderReplyViewHolder).date2.text = ""

                } else {
                    (holder as SenderReplyViewHolder).date2.visibility = View.VISIBLE
                    if (cal1.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) && cal1.get(
                            Calendar.MONTH
                        )
                        == currentDate.get(Calendar.MONTH)
                        && cal1.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)
                    ) {
                        (holder as SenderReplyViewHolder).date2.text = "Today"

//                    Log.d(
//                        "prevDate", SimpleDateFormat("dd-MM-yyyy hh:mm:ss a")
//                            .format(Date(previousMsg)).toString()
                        //)
                    } else if (cal1.get(Calendar.YEAR) == prevDate.get(Calendar.YEAR) && cal1.get(
                            Calendar.MONTH
                        )
                        == prevDate.get(Calendar.MONTH) &&
                        cal1.get(Calendar.DAY_OF_YEAR) == prevDate.get(Calendar.DAY_OF_YEAR)
                    ) {
                        (holder as SenderReplyViewHolder).date2.text = "Yesterday"
                    } else {
                        (holder as SenderReplyViewHolder).date2.text =
                            SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss a", Locale.ENGLISH)
                                .format(Date(chatList.get(holder.adapterPosition).dateFormat!!))
                                .substring(0, 6)
                    }
                }


            }
            (holder as SenderReplyViewHolder).reply.setOnLongClickListener(object :
                View.OnLongClickListener {
                override fun onLongClick(p0: View?): Boolean {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(p0!!.context)
                    builder.setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                var msg = chatList.get(holder.adapterPosition).message
                                Log.d("msg1", msg.toString())
//                                Log.d("pos",(holder as SenderViewHolder).pos.toString())
                                val ref =
                                    FirebaseDatabase.getInstance().getReference().child("Chats")
                                        .orderByChild("message").equalTo(msg)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("refKey", snapshot.value.toString())
                                        var count = 0
                                        for (ds in snapshot.children) {
                                            Log.d("refKey1", ds.value.toString())
                                            Log.d("key3", ds.key!!)
                                            val map = HashMap<String, Any>()
                                            map.put("message", "this is message is deleted")
                                            map.put("type", "sender")
                                            map.put("quotepos", -1)
                                            map.put("quote", "")
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
                        .setNegativeButton("No", null)
                        .show()

                    return true
                }

            })
        } else if (chatList.get(holder.adapterPosition).quotepos != -1 && chatList.get(holder.adapterPosition).type
                .equals("receiver")
        ) {
            var previousMsg: Long? = 0
            if (holder.adapterPosition != 0) {
                previousMsg = chatList.get(holder.adapterPosition - 1).dateFormat!!
            }
            if (previousMsg == 0L) {
                (holder as ReceiverReplyViewHolder).date3.visibility = View.VISIBLE
                val date = SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss a", Locale.ENGLISH).format(
                    Date(chatList.get(holder.adapterPosition).dateFormat!!)
                )
                (holder as ReceiverReplyViewHolder).date3.text = date
            } else {
                val cal1 = Calendar.getInstance()
                val cal2 = Calendar.getInstance()
                cal1.timeInMillis = chatList.get(holder.adapterPosition).dateFormat!!
                cal2.timeInMillis = previousMsg!!
                val currentDate = Calendar.getInstance()
                val prevDate = Calendar.getInstance()
                prevDate.add(Calendar.DATE, -1)

                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
                        Calendar.DAY_OF_YEAR
                    )
                ) {
                    (holder as ReceiverReplyViewHolder).date3.visibility = View.GONE
                    (holder as ReceiverReplyViewHolder).date3.text = ""

                } else {
                    (holder as ReceiverReplyViewHolder).date3.visibility = View.VISIBLE
                    if (cal1.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) && cal1.get(
                            Calendar.MONTH
                        )
                        == currentDate.get(Calendar.MONTH)
                        && cal1.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)
                    ) {
                        (holder as ReceiverReplyViewHolder).date3.text = "Today"

//                    Log.d(
//                        "prevDate", SimpleDateFormat("dd-MM-yyyy hh:mm:ss a")
//                            .format(Date(previousMsg)).toString()
                        //)
                    } else if (cal1.get(Calendar.YEAR) == prevDate.get(Calendar.YEAR) && cal1.get(
                            Calendar.MONTH
                        )
                        == prevDate.get(Calendar.MONTH) &&
                        cal1.get(Calendar.DAY_OF_YEAR) == prevDate.get(Calendar.DAY_OF_YEAR)
                    ) {
                        (holder as ReceiverReplyViewHolder).date3.text = "Yesterday"
                    } else {
                        (holder as ReceiverReplyViewHolder).date3.text =
                            SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss a", Locale.ENGLISH)
                                .format(Date(chatList.get(holder.adapterPosition).dateFormat!!))
                                .substring(0, 6)
                    }
                }


            }
            (holder as ReceiverReplyViewHolder).sndReplyTxt.text = chatList.get(position).message
            (holder as ReceiverReplyViewHolder).QuotedTxt.text = chatList.get(position).quote
            (holder as ReceiverReplyViewHolder).reply1.setOnClickListener(object :
                View.OnClickListener {
                override fun onClick(p0: View?) {
                    mQuoteClickListener!!.onQuoteClick(chatList.get(position).quotepos!!)
                }

            })
            (holder as ReceiverReplyViewHolder).reply1.setOnLongClickListener(object :
                View.OnLongClickListener {
                override fun onLongClick(p0: View?): Boolean {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(p0!!.context)
                    builder.setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                var msg = chatList.get(holder.adapterPosition).message
                                Log.d("msg1", msg.toString())
//                                Log.d("pos",(holder as SenderViewHolder).pos.toString())
                                val ref =
                                    FirebaseDatabase.getInstance().getReference().child("Chats")
                                        .orderByChild("message").equalTo(msg)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("refKey", snapshot.value.toString())
                                        var count = 0
                                        for (ds in snapshot.children) {
                                            val map = HashMap<String, Any>()
                                            map.put("message", "this is message is deleted")
                                            map.put("type", "receiver")
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
                        .setNegativeButton("No", null)
                        .show()

                    return true
                }

            })
        }
        if (chatList.get(holder.adapterPosition).mediaType.equals("image") &&
            chatList.get(holder.adapterPosition).type.equals("senderImage")
        ) {

            val options = RequestOptions().override(540, 540).transform(RoundedCorners(24))
            Glide.with(context).load(Uri.parse(chatList.get(holder.adapterPosition).message))
                .apply(options)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        //    (holder as SenderImgViewHolder).senderImg.visibility = View.INVISIBLE
                        //(holder as SenderImgViewHolder).deleteMsg.visibility =View.VISIBLE
                        Log.d("causedBy", e.toString())
//                            (holder as SenderImgViewHolder).deleteMsg.text =
//                                chatList.get(holder.adapterPosition).message

                        return false

                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                }).into((holder as SenderImgViewHolder).senderImg)

            var previousMsg: Long? = 0
            if (holder.adapterPosition != 0) {
                previousMsg = chatList.get(holder.adapterPosition - 1).dateFormat!!
            }
            if (previousMsg == 0L) {
                (holder as SenderImgViewHolder).sndImgDate.visibility = View.VISIBLE
                val date = SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss a", Locale.ENGLISH).format(
                    Date(chatList.get(holder.adapterPosition).dateFormat!!)
                )
                (holder as SenderImgViewHolder).sndImgDate.text = date
            } else {
                val cal1 = Calendar.getInstance()
                val cal2 = Calendar.getInstance()
                cal1.timeInMillis = chatList.get(holder.adapterPosition).dateFormat!!
                cal2.timeInMillis = previousMsg!!
                val currentDate = Calendar.getInstance()
                val prevDate = Calendar.getInstance()
                prevDate.add(Calendar.DATE, -1)

                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
                        Calendar.DAY_OF_YEAR
                    )
                ) {
                    (holder as SenderImgViewHolder).sndImgDate.visibility = View.GONE
                    (holder as SenderImgViewHolder).sndImgDate.text = ""

                } else {
                    (holder as SenderImgViewHolder).sndImgDate.visibility = View.VISIBLE
                    if (cal1.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) && cal1.get(
                            Calendar.MONTH
                        )
                        == currentDate.get(Calendar.MONTH)
                        && cal1.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)
                    ) {
                        (holder as SenderImgViewHolder).sndImgDate.text = "Today"

//                    Log.d(
//                        "prevDate", SimpleDateFormat("dd-MM-yyyy hh:mm:ss a")
//                            .format(Date(previousMsg)).toString()
                        //)
                    } else if (cal1.get(Calendar.YEAR) == prevDate.get(Calendar.YEAR) && cal1.get(
                            Calendar.MONTH
                        )
                        == prevDate.get(Calendar.MONTH) &&
                        cal1.get(Calendar.DAY_OF_YEAR) == prevDate.get(Calendar.DAY_OF_YEAR)
                    ) {
                        (holder as SenderImgViewHolder).sndImgDate.text = "Yesterday"
                    } else {
                        (holder as SenderImgViewHolder).sndImgDate.text =
                            SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss a", Locale.ENGLISH)
                                .format(Date(chatList.get(holder.adapterPosition).dateFormat!!))
                                .substring(0, 6)
                    }
                }


            }

            (holder as SenderImgViewHolder).senderImg.setOnLongClickListener(object :
                View.OnLongClickListener {
                override fun onLongClick(p0: View?): Boolean {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(p0!!.context)
                    builder.setMessage("Are you sure you want to delete this Image?")
                        .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                var msg = chatList.get(holder.adapterPosition).message
                                var timestamp = chatList.get(holder.adapterPosition).dateFormat
                                Log.d("timestamp", timestamp.toString())
                                Log.d("msg1", msg.toString())
//                                Log.d("pos",(holder as SenderViewHolder).pos.toString())
                                val ref =
                                    FirebaseDatabase.getInstance().getReference().child("Chats")
                                        .orderByChild("message").equalTo(msg)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("refKey", snapshot.value.toString())
                                        for (ds in snapshot.children) {
                                            val map = HashMap<String, Any>()
                                            map.put("message", "this is message is deleted")
                                            map.put("type", "sender")
                                            map.put("mediaType", "")
                                            ds.ref.updateChildren(map)
                                        }
                                        chatList.remove(chatList.get(holder.adapterPosition))


                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                            }

                        })
                        .setNegativeButton("No", null)
                        .show()

                    return true
                }

            })


        }
        if (chatList.get(holder.adapterPosition).type.equals("senderAudio")) {
            playingHolder = holder as SenderAudioViewHolder
            if (holder.absoluteAdapterPosition == currentPlayingPosition) {
                if (player!!.playWhenReady && player!!.playbackState == Player.STATE_READY) {
                    playingHolder!!.audioPlayBtn.setImageResource(R.drawable.pause_audio)
                } else {
                    playingHolder!!.audioPlayBtn.setImageResource(R.drawable.audio_play_btn)
                }
            } else {
                playingHolder!!.audioPlayBtn.setImageResource(R.drawable.audio_play_btn)
            }


        }


    }

    override fun getItemCount(): Int {
        return chatList.size
    }


    override fun getItemViewType(position: Int): Int {
        if (chatList.get(position).type.equals("sender") && chatList.get(position).quotepos == -1) {
            return MSG_RIGHT
        } else if (chatList.get(position).type.equals("sender") && chatList.get(position).quotepos != -1) {
            return MSG_REPLY_RIGHT
        }


        if (chatList.get(position).type.equals("receiver") && chatList.get(position).quotepos == -1) {
            return MSG_LEFT
        } else if (chatList.get(position).type.equals("receiver") && chatList.get(position).quotepos != -1) {
            return MSG_REPLY_LEFT
        }
        if (chatList.get(position).type.equals("senderAudio")) {
            return AUDIO_MSG
        } else {
            return IMG_MSG
        }

    }


    fun releasePlayer() {
        if (player != null) {
            player!!.release()
            player = null
            currentPlayingPosition = -1
        }
    }

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderTxt: TextView = itemView.findViewById(R.id.sender_txt)
        val date: TextView = itemView.findViewById(R.id.date)
        val senderMsgTime: TextView = itemView.findViewById(R.id.sender_msg_time)


        var pos = adapterPosition

    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiverTxt: TextView = itemView.findViewById(R.id.receiver_txt)
        val date1: TextView = itemView.findViewById(R.id.date1)
        val receiverMsgTime: TextView = itemView.findViewById(R.id.receiver_msg_time)
    }

    inner class SenderReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sndReplyTxt: TextView = itemView.findViewById(R.id.txtBody)
        val QuotedTxt: TextView = itemView.findViewById(R.id.textQuote)
        val reply: ConstraintLayout = itemView.findViewById(R.id.reply)
        val date2: TextView = itemView.findViewById(R.id.date2)
        val senderReplyMsgTime: TextView = itemView.findViewById(R.id.sender_reply_msg_time)
        val quotedImgSndr: ImageView = itemView.findViewById(R.id.quoted_img)
        val name: TextView = itemView.findViewById(R.id.name)
    }

    inner class ReceiverReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sndReplyTxt: TextView = itemView.findViewById(R.id.txtBody1)
        val QuotedTxt: TextView = itemView.findViewById(R.id.textQuote1)
        val reply1: ConstraintLayout = itemView.findViewById(R.id.reply1)
        val date3: TextView = itemView.findViewById(R.id.date3)
        val quotedImgRecvr: ImageView = itemView.findViewById(R.id.quoted_img_receiver)
    }

    inner class SenderImgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderImg: ImageView = itemView.findViewById(R.id.sender_img)
        val sndImgDate: TextView = itemView.findViewById(R.id.snd_img_date)
        //val deleteMsg : TextView = itemView.findViewById(R.id.delete_msg)
    }


    inner class SenderAudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val audioPlayBtn: ImageView = itemView.findViewById(R.id.play_audio_msg)
        val audioWaveBar: LineBarVisualizer = itemView.findViewById(R.id.audio_wave_bar)
        val audioTimeLine: TextView = itemView.findViewById(R.id.audio_timeline)

        init {

            audioPlayBtn.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            try {
                if (absoluteAdapterPosition == currentPlayingPosition) {

                    if (player!!.playWhenReady && player!!.playbackState == Player.STATE_READY) {
                        playingHolder!!.audioPlayBtn.setImageResource(R.drawable.audio_play_btn)
                        player!!.pause()
                        timer.cancel()
                        playingHolder!!.audioTimeLine.text = "00:00"
                        playingHolder!!.audioWaveBar.release()

                        Log.d("Duration4", player!!.duration.toString())


                    } else {
                        player = ExoPlayer.Builder(context).build()
                        val audioUrl = Uri.parse(chatList.get(currentPlayingPosition).message)
                        val mediaItem = MediaItem.fromUri(audioUrl)
                        player!!.setMediaItem(mediaItem)
                        player!!.prepare()
                        player!!.playWhenReady = true
                        playingHolder!!.audioPlayBtn.setImageResource(R.drawable.pause_audio)
                        Log.d("Duration3", player!!.currentPosition.toString())
                        player!!.addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                                    val duration = player!!.duration
                                    timer = object : CountDownTimer(duration, 1000) {
                                        override fun onTick(p0: Long) {
                                            val time = String.format(
                                                "%02d:%02d",
                                                p0 / 60000,
                                                p0 % 60000 / 1000
                                            )
                                            playingHolder!!.audioTimeLine.text = time
                                        }

                                        override fun onFinish() {

                                        }

                                    }.start()
                                }
                                if (playbackState == Player.STATE_ENDED) {
                                    if (playingHolder != null) {
                                        playingHolder!!.audioPlayBtn.setImageResource(R.drawable.audio_play_btn)
                                    }
                                    releasePlayer()
                                }
                            }

                        })

                        playingHolder!!.audioWaveBar.setDensity(90f)
                        playingHolder!!.audioWaveBar.setColor(Color.GRAY)
                        playingHolder!!.audioWaveBar.setPlayer(player!!.audioSessionId)


                    }
                } else {
                    currentPlayingPosition = absoluteAdapterPosition
                    if (player != null) {
                        if (playingHolder != null) {
                            playingHolder!!.audioPlayBtn.setImageResource(R.drawable.audio_play_btn)


                        }
                        timer.cancel()
                        playingHolder!!.audioTimeLine.text = "00:00"
                        playingHolder!!.audioWaveBar.release()
                        player!!.release()


                    }
                    playingHolder = this
                    player = ExoPlayer.Builder(context).build()
                    val audioUrl = Uri.parse(chatList.get(currentPlayingPosition).message)
                    val mediaItem = MediaItem.fromUri(audioUrl)
                    player!!.setMediaItem(mediaItem)
                    player!!.prepare()
                    player!!.playWhenReady = true
                    Log.d("Duration1", player!!.duration.toString())
                    if (player != null) {
                        playingHolder!!.audioPlayBtn.setImageResource(R.drawable.pause_audio)
                    }
                    player!!.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                                timer = object : CountDownTimer(player!!.duration, 1000) {
                                    override fun onTick(p0: Long) {
                                        val time = String.format(
                                            "%02d:%02d",
                                            p0 / 60000,
                                            p0 % 60000 / 1000
                                        )
                                        playingHolder!!.audioTimeLine.text = time
                                    }

                                    override fun onFinish() {

                                    }

                                }.start()
                            }
                            if (playbackState == Player.STATE_ENDED) {
                                if (playingHolder != null) {
                                    playingHolder!!.audioPlayBtn.setImageResource(R.drawable.audio_play_btn)
                                }
                                releasePlayer()
                            }
                        }

                    })

                    playingHolder!!.audioWaveBar.setDensity(50f)
                    playingHolder!!.audioWaveBar.setColor(Color.GRAY)
                    playingHolder!!.audioWaveBar.setPlayer(player!!.audioSessionId)
                }


//if (previous != current){
//    player!!.pause()
//}

            } catch (e: Exception) {
                Log.d("exoPlayerErr", e.toString())
            }
        }


    }
}
