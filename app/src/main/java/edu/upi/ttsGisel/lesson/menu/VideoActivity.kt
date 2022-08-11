package edu.upi.ttsGisel.lesson.menu

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.URLUtil
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import edu.upi.ttsGisel.R
import edu.upi.ttsGisel.ui.home.MateriModel
import edu.upi.ttsGisel.utils.AppUtils
import edu.upi.ttsGisel.utils.Config

class VideoActivity : AppCompatActivity() {

    private lateinit var videoName: String
    private lateinit var videoView: VideoView
    private lateinit var loading: TextView
    private lateinit var completed: TextView

    private lateinit var materi: MateriModel

    private val playBackTime = "play_time"
    private var currentPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        materi = intent.getParcelableExtra(Config.EXTRA_LESSON)!!
        videoName = materi.urlVideo

        videoView = findViewById(R.id.videoview)
        loading = findViewById(R.id.loading)
        completed = findViewById(R.id.completed)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState != null) {
            currentPos = savedInstanceState.getInt(playBackTime)
        }

        val controller = MediaController(this)
        controller.setMediaPlayer(videoView)

        videoView.setMediaController(controller)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(playBackTime, videoView.currentPosition)
    }

    private fun getURI(videoName:String): Uri {
        return if (URLUtil.isValidUrl(videoName)) {
            //  an external URL
            Uri.parse(videoName)
        } else { //  a raw resource
            Uri.parse("android.resource://" + packageName +
                    "/raw/" + videoName)
        }
    }

    private fun initPlayer() {
        loading.visibility = VideoView.VISIBLE
        completed.visibility = VideoView.INVISIBLE
        val videoUri:Uri = getURI(videoName)
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener{
            loading.visibility = VideoView.INVISIBLE
            if (currentPos > 0) {
                videoView.seekTo(currentPos)
            } else {
                videoView.seekTo(1)
            }
            videoView.start()
        }
        videoView.setOnCompletionListener {
            completed.visibility = VideoView.VISIBLE
            videoView.seekTo(1)
        }
    }

    private fun releasePlayer(){
        videoView.stopPlayback()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onStart() {
        super.onStart()
        initPlayer()
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            videoView.pause()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        if (AppUtils.getStatusLesson(Config.VIDEO, materi.idMateri, applicationContext)!=Config.COMPLETED) inflater.inflate(R.menu.menu_finish, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.finish -> {
                val sharedPreferences = application.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(Config.LESSON_STATUS_SHARED_PREF+ Config.VIDEO+materi.idMateri, Config.COMPLETED)
                editor.putString(Config.LESSON_STATUS_SHARED_PREF+ Config.THEORY+materi.idMateri, Config.ACTIVE)
                editor.apply()
                editor.commit()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
