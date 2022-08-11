package edu.upi.ttsGisel.lesson.menu

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import edu.upi.ttsGisel.R
import edu.upi.ttsGisel.ui.home.MateriModel
import edu.upi.ttsGisel.utils.AppUtils
import edu.upi.ttsGisel.utils.Config

class TheoryActivity : AppCompatActivity() {

    private lateinit var materi: MateriModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theory)

        materi = intent.getParcelableExtra(Config.EXTRA_LESSON)!!

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val myWebView: WebView = findViewById(R.id.webview)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        myWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.visibility = View.INVISIBLE
                }
            }
        }
        myWebView.loadUrl(materi.content)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        if (AppUtils.getStatusLesson(Config.THEORY, materi.idMateri, applicationContext)!= Config.COMPLETED) inflater.inflate(R.menu.menu_finish, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.finish -> {
                val sharedPreferences = application.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(Config.LESSON_STATUS_SHARED_PREF+ Config.THEORY+materi.idMateri, Config.COMPLETED)
                editor.putString(Config.LESSON_STATUS_SHARED_PREF+ Config.QUIZ+materi.idMateri, Config.ACTIVE)
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
