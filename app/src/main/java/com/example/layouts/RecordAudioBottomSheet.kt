package com.example.layouts

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.audiofx.Visualizer
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.EnvironmentCompat
import com.chibde.visualizer.LineBarVisualizer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.storage.FirebaseStorage
import com.visualizer.amplitude.AudioRecordView
import java.io.File
import java.io.IOException
import java.util.*


/**
 * Created by mohammad sajjad on 25-05-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

class RecordAudioBottomSheet : BottomSheetDialogFragment() {
    lateinit var recordMic: ImageView
    lateinit var recordTimer: Chronometer
    lateinit var recordInfo: TextView
    var mMediaRecorder: MediaRecorder? = null
    lateinit var mMediaPlayer: MediaPlayer
    lateinit var recordVisualizer : AudioRecordView
    var filename: String = ""
    var timer : Timer? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.record_audio_sheet, container, false)
    }


    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recordMic = view.findViewById(R.id.record_audio_mic)
        recordTimer = view.findViewById(R.id.record_audio_count)
        recordInfo = view.findViewById(R.id.record_info)
        recordVisualizer = view.findViewById(R.id.record_visualizer)
        mMediaPlayer = MediaPlayer()
        filename = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RECORDINGS)
                .toString()
        ).toString()


        filename = filename + "/audioRecording.mp3"
        Log.d("filename", filename)
        recordInfo.text = "Hold to record audio"
        recordMic.setOnTouchListener(
            object : View.OnTouchListener {
                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                    if (p1!!.action == MotionEvent.ACTION_UP) {
                        recordTimer.stop()
                        recordTimer.base = SystemClock.elapsedRealtime()
                        recordMic.background = ColorDrawable(Color.TRANSPARENT)
                        recordInfo.visibility = View.VISIBLE
                        recordInfo.text = "Hold to record audio"
                        stopRecording()



                    } else if (p1.action == MotionEvent.ACTION_DOWN) {
                        recordTimer.start()
                        recordTimer.base = SystemClock.elapsedRealtime()
                        recordMic.setBackgroundResource(R.drawable.mic_background)
                        recordInfo.text = "Recording audio"
                        startRecording()


                    }
                    return true
                }

            })

    }


    override fun onStart() {
        super.onStart()
        val behaviour = BottomSheetBehavior.from(requireView().parent as View)
        behaviour.peekHeight = 950
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun startRecording() {
        mMediaRecorder = MediaRecorder()
        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
        mMediaRecorder?.setOutputFile(filename)
        mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mMediaRecorder?.setAudioSamplingRate(8000)
        mMediaRecorder?.setAudioEncodingBitRate(8000)
        try {
            mMediaRecorder!!.prepare()
            mMediaRecorder!!.start()
        } catch (e: IOException) {
            Log.d("prepareErr", e.toString())
        }

  timer = Timer()
        timer?.schedule(object : TimerTask(){
            override fun run() {

                try{
                    val currentMaxAmplitude = mMediaRecorder?.maxAmplitude
                    recordVisualizer.update(currentMaxAmplitude ?: 0)
                }catch (e : Exception)
                {
                    Log.d("amplitude",e.toString())
                }


            }

        },0,100)



    }

    fun stopRecording() {
        try {
            mMediaRecorder?.stop()
            mMediaRecorder?.reset()
            mMediaRecorder?.release()
        }catch (e : Exception){
            Log.d("recStoperr",e.toString())
        }

        mMediaRecorder = null
        timer?.cancel()
        recordVisualizer.recreate()





    }
}