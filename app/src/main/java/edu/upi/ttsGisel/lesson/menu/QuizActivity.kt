package edu.upi.ttsGisel.lesson.menu

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import edu.upi.ttsGisel.R
import edu.upi.ttsGisel.helpers.InputValidation
import edu.upi.ttsGisel.ui.home.MateriModel
import edu.upi.ttsGisel.utils.AppUtils
import edu.upi.ttsGisel.utils.Config
import org.json.JSONArray
import org.json.JSONException


class QuizActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var materi: MateriModel
    private lateinit var progressBar: ProgressBar
    private lateinit var inputValidation: InputValidation

    private lateinit var soalPG: TextView
    private lateinit var soalSQL: TextView
    private lateinit var btnPG: Button
    private lateinit var btnSQL: Button

    private lateinit var tvXP: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var layoutCekSQL: TextInputLayout
    private lateinit var cekSQL: TextInputEditText

    private lateinit var rbSatu: RadioButton
    private lateinit var rbDua: RadioButton
    private lateinit var rbTiga: RadioButton
    private lateinit var rbEmpat: RadioButton

    private lateinit var idMateri: String
    private lateinit var idQuiz: String
    private lateinit var idGuess: String
    private lateinit var idUser: String

    private lateinit var jawaban: String
    private var kunciPG: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        materi = intent.getParcelableExtra(Config.EXTRA_LESSON)!!
        idMateri = materi.idMateri
        idUser = AppUtils.getIdUser(this)!!

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        getSoal()
        initObjects()
        initListeners()
    }

    private fun initViews(){
        progressBar = findViewById(R.id.progressBar)

        soalPG = findViewById(R.id.soalPG)
        soalSQL = findViewById(R.id.soalSQL)
        btnPG = findViewById(R.id.btn_choose)
        btnSQL = findViewById(R.id.btn_query)

        tvXP = findViewById(R.id.tvXP)
        radioGroup = findViewById(R.id.rg_options)
        layoutCekSQL = findViewById(R.id.textInputLayoutSQL)
        cekSQL = findViewById(R.id.textInputEditTextSQL)

        rbSatu = findViewById(R.id.rb_1)
        rbDua = findViewById(R.id.rb_2)
        rbTiga = findViewById(R.id.rb_3)
        rbEmpat = findViewById(R.id.rb_4)
    }

    private fun getSoal(){
        progressBar.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, Config.GET_SOAL_URL,
                Response.Listener { response ->
                    setSoal(response)
                    val sharedPreferences = applicationContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString(Config.SOAL_SHARED_PREF+idMateri, response)
                    editor.apply()
                    editor.commit()
                    progressBar.visibility = View.INVISIBLE
                },
                Response.ErrorListener {
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
                }){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params[Config.ID_USER_SHARED_PREF] = idUser
                params[Config.ID_MATERI_PARAM] = idMateri
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun setSoal(soal: String){
        lateinit var soalArray: JSONArray
        try {
            soalArray = JSONArray(soal)
            var score = ""
            for (i in 0 until soalArray.length()) {
                val `object` = soalArray.getJSONObject(i)
                soalPG.text = `object`.getString("soal_pg")
                rbSatu.text = `object`.getString("answer1")
                rbDua.text = `object`.getString("answer2")
                rbTiga.text = `object`.getString("answer3")
                rbEmpat.text = `object`.getString("answer4")
                soalSQL.text = `object`.getString("soal_sql")
                score = getString(R.string.score) + `object`.getString("score_weight")
                idQuiz = `object`.getString("id_quiz")
                idGuess = `object`.getString("id_guess")
                kunciPG = Integer.parseInt(`object`.getString("answer_key"))
                jawaban = `object`.getString("jawaban")
            }
            tvXP.text = score
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (jawaban!="null"){
            setJawaban()
        }
    }

    private fun initObjects() {
        inputValidation = InputValidation(this)
    }

    private fun initListeners(){
        btnPG.setOnClickListener(this)
        btnSQL.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        if (AppUtils.getStatusLesson(Config.QUIZ, materi.idMateri, applicationContext)!= Config.COMPLETED) inflater.inflate(R.menu.menu_finish, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.finish -> {
                val sharedPreferences = application.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(Config.LESSON_STATUS_SHARED_PREF+ Config.QUIZ+materi.idMateri, Config.COMPLETED)
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

    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_choose -> {
                val radioButtonID: Int = radioGroup.checkedRadioButtonId
                if (radioButtonID != -1) {
                    val radioButton: View = radioGroup.findViewById(radioButtonID)
                    val idx: Int = radioGroup.indexOfChild(radioButton)
                    val status: String
                    if (idx==kunciPG-1){
                        status = "true"
                        Toast.makeText(this, getString(R.string.benar), Toast.LENGTH_SHORT).show()
                    } else {
                        status = "false"
                        Toast.makeText(this, getString(R.string.salah), Toast.LENGTH_SHORT).show()
                    }
                    postJawabPG((idx+1).toString(), status)
                } else Toast.makeText(this, getString(R.string.no_jawaban), Toast.LENGTH_SHORT).show()
            }
            R.id.btn_query -> {
                postJawabSQL()
            }
        }
    }

    private fun setJawaban(){
        radioGroup.check(radioGroup.getChildAt(Integer.parseInt(jawaban)-1).id)
        btnPG.isEnabled = false
        for (i in 0 until radioGroup.childCount) {
            radioGroup.getChildAt(i).isEnabled = false
        }
        radioGroup.getChildAt(kunciPG-1).setBackgroundColor(resources.getColor(R.color.colorSecondary))
    }

    private fun postJawabPG(answer: String, status: String){
        progressBar.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, Config.INPUT_JAWABAN_URL,
                Response.Listener {response ->
                    if (response.contains(Config.SUCCESS)){
                        btnPG.isEnabled = false
                        for (i in 0 until radioGroup.childCount) {
                            radioGroup.getChildAt(i).isEnabled = false
                        }
                        radioGroup.getChildAt(kunciPG-1).setBackgroundColor(resources.getColor(R.color.colorSecondary))
                    }
                    progressBar.visibility = View.INVISIBLE
                },
                Response.ErrorListener {
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
                }){
            override fun getParams(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                params[Config.ID_USER_SHARED_PREF] = idUser
                params[Config.ID_QUIZ_PARAM] = idQuiz
                params[Config.ANSWER_PARAM] = answer
                params[Config.TYPE_PARAM] = status
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun postJawabSQL(){
        if (!inputValidation.isInputEditTextFilled(cekSQL, layoutCekSQL, getString(R.string.error_message_sql))) {
            return
        }
        progressBar.visibility = View.VISIBLE
        val answer = cekSQL.text.toString().trim()
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, Config.INPUT_SQL_URL,
                Response.Listener {response ->
                    progressBar.visibility = View.INVISIBLE
                    val builder = AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                    val dialogLayout = View.inflate(this, R.layout.result_layout, null)
                    val myWebView = dialogLayout.findViewById<WebView>(R.id.activity_main_webview)
                    val pb = dialogLayout.findViewById<ProgressBar>(R.id.progressBar)
                    pb.visibility = View.VISIBLE
                    myWebView.webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            pb.progress = newProgress
                            if (newProgress == 100) {
                                pb.visibility = View.INVISIBLE
                            }
                        }
                    }
                    myWebView.loadDataWithBaseURL(null, response, "text/html", "UTF-8", null)

                    builder.setCancelable(true)
                            .setView(dialogLayout)
                            .setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
                                dialogInterface.cancel()
                            }
                            .show()
                },
                Response.ErrorListener {
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
                }){
            override fun getParams(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                params[Config.ID_USER_SHARED_PREF] = idUser
                params[Config.ID_GUESS_PARAM] = idGuess
                params[Config.ANSWER_PARAM] = answer
                return params
            }
        }
        queue.add(stringRequest)
    }
}
