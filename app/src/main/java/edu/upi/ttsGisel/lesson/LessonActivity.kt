package edu.upi.ttsGisel.lesson

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import edu.upi.ttsGisel.R
import edu.upi.ttsGisel.ui.home.MateriModel
import edu.upi.ttsGisel.utils.AppUtils
import edu.upi.ttsGisel.utils.Config

class LessonActivity : AppCompatActivity() {

    private lateinit var mAdapter: LessonTimeLineAdapter
    private val mDataList = ArrayList<LessonTimeLineModel>()
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var progressBar: ProgressBar
    private lateinit var materi: MateriModel

    private lateinit var idUser: String
    private lateinit var idMateri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)

        materi = intent.getParcelableExtra(Config.EXTRA_LESSON)!!
        idUser = AppUtils.getIdUser(this)!!

        progressBar = findViewById(R.id.progressBar)

        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.title = materi.judul
        }

        idMateri = materi.idMateri
        val sharedPreferences = application.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if (materi.dtFinish != "null"){
            editor.putBoolean(Config.LESSON_STATUS_SHARED_PREF+idMateri, true)
            editor.putString(Config.LESSON_STATUS_SHARED_PREF+Config.VIDEO+idMateri, Config.COMPLETED)
            editor.putString(Config.LESSON_STATUS_SHARED_PREF+Config.THEORY+idMateri, Config.COMPLETED)
            editor.putString(Config.LESSON_STATUS_SHARED_PREF+Config.QUIZ+idMateri, Config.COMPLETED)
            editor.apply()
            editor.commit()
        }

        if (!AppUtils.getStatusLesson(idMateri, applicationContext)){
            editor.putBoolean(Config.LESSON_STATUS_SHARED_PREF+idMateri, true)
            editor.putString(Config.LESSON_STATUS_SHARED_PREF+Config.VIDEO+idMateri, Config.ACTIVE)
            editor.putString(Config.LESSON_STATUS_SHARED_PREF+Config.THEORY+idMateri, Config.INACTIVE)
            editor.putString(Config.LESSON_STATUS_SHARED_PREF+Config.QUIZ+idMateri, Config.INACTIVE)
            editor.apply()
            editor.commit()
        }

        setDataListItems()
        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        if (AppUtils.getStatusLesson(Config.VIDEO, materi.idMateri+1, applicationContext) != Config.ACTIVE && materi.dtFinish == "null") inflater.inflate(R.menu.menu_finish, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.finish -> {
                if (AppUtils.getStatusLesson(Config.QUIZ, materi.idMateri, applicationContext) != Config.COMPLETED){
                    messageUnFinish()
                } else {
                    updateLesson()
                    val sharedPreferences = application.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString(Config.LESSON_STATUS_SHARED_PREF+ Config.VIDEO+materi.idMateri+1, Config.ACTIVE)
                    editor.apply()
                    editor.commit()
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun messageUnFinish(){
        val builder = AlertDialog.Builder(this, R.style.Theme_MaterialComponents_DayNight_Dialog_Alert)
        builder.setMessage(applicationContext.getString(R.string.message_un_finish_lesson))
                .setCancelable(false)
                .setPositiveButton(applicationContext.getString(R.string.ok)) { dialogInterface, _ ->
                    dialogInterface.cancel()
                }.show()
    }

    private fun updateLesson(){
        progressBar.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, Config.UPDATE_LESSON_URL,
                Response.Listener { response ->
                    if (response.contains(Config.SUCCESS)){
                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(this, getString(R.string.lesson_finish), Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener {
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
                }){
            override fun getParams(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                params[Config.ID_USER_SHARED_PREF] = idUser
                params[Config.ID_MATERI_PARAM] = idMateri
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun setDataListItems() {
        mDataList.add(LessonTimeLineModel(getString(R.string.msg_video), "Video", AppUtils.getStatusLesson(Config.VIDEO, materi.idMateri, applicationContext)))
        mDataList.add(LessonTimeLineModel(getString(R.string.msg_theory), "Theory", AppUtils.getStatusLesson(Config.THEORY, materi.idMateri, applicationContext)))
        mDataList.add(LessonTimeLineModel(getString(R.string.msg_quiz), "Quiz", AppUtils.getStatusLesson(Config.QUIZ, materi.idMateri, applicationContext)))
    }

    private fun initRecyclerView() {
        mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val recyclerView: RecyclerView = findViewById(R.id.rv_tlLesson)
        recyclerView.layoutManager = mLayoutManager
        mAdapter = LessonTimeLineAdapter(materi, mDataList, this)
        recyclerView.adapter = mAdapter
    }

    override fun onResume() {
        mDataList.clear()
        setDataListItems()
        mAdapter.updateWith(mDataList)
        super.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
