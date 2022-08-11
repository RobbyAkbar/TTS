package edu.upi.ttsGisel

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import edu.upi.ttsGisel.helpers.InputValidation
import edu.upi.ttsGisel.utils.AppUtils.Companion.hideProgressDialog
import edu.upi.ttsGisel.utils.AppUtils.Companion.progressDialog
import edu.upi.ttsGisel.utils.Config
import java.util.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val activity = this@LoginActivity

    private lateinit var giselIcon: ImageView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var rootView: RelativeLayout
    private lateinit var afterAnimationView: RelativeLayout

    private lateinit var textInputLayoutEmail: TextInputLayout
    private lateinit var textInputLayoutPassword: TextInputLayout

    private lateinit var textInputEditTextEmail: TextInputEditText
    private lateinit var textInputEditTextPassword: TextInputEditText

    private lateinit var appCompatButtonLogin: AppCompatButton
    private lateinit var textViewLinkRegister: AppCompatTextView

    private lateinit var inputValidation: InputValidation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)

        initViews()

        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                loadingProgressBar.visibility = View.GONE
                rootView.setBackgroundColor(ContextCompat.getColor(this@LoginActivity, R.color.colorSplashText))

                val layoutParams: ViewGroup.LayoutParams = giselIcon.layoutParams
                layoutParams.width = resources.getDimension(R.dimen.imageview_size).toInt()
                layoutParams.height = resources.getDimension(R.dimen.imageview_size).toInt()
                giselIcon.layoutParams = layoutParams

                giselIcon.setImageResource(R.mipmap.ic_launcher)
                startAnimation()
            }
        }.start()

        initObjects()
        initListeners()
    }

    private fun initViews() {
        giselIcon = findViewById(R.id.imageView)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        rootView = findViewById(R.id.rootView)
        afterAnimationView = findViewById(R.id.afterAnimationView)

        textInputLayoutEmail = findViewById<View>(R.id.textInputLayoutEmail) as TextInputLayout
        textInputLayoutPassword = findViewById<View>(R.id.textInputLayoutPassword) as TextInputLayout

        textInputEditTextEmail = findViewById<View>(R.id.emailEditText) as TextInputEditText
        textInputEditTextPassword = findViewById<View>(R.id.passwordEditText) as TextInputEditText

        appCompatButtonLogin = findViewById(R.id.loginButton)
        textViewLinkRegister = findViewById(R.id.txtSignUp)
    }

    /**
     * This method is to initialize objects to be used
     */
    private fun initObjects() {
        inputValidation = InputValidation(activity)
    }

    private fun initListeners() {
        appCompatButtonLogin.setOnClickListener(this)
        textViewLinkRegister.setOnClickListener(this)
    }

    private fun startAnimation() {
        val viewPropertyAnimator = giselIcon.animate()
        viewPropertyAnimator.x(0f)
        viewPropertyAnimator.y(0f)
        viewPropertyAnimator.duration = 1000
        viewPropertyAnimator.setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                afterAnimationView.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    override fun onClick(v: View) {
        val intent: Intent
        when (v.id) {
            R.id.loginButton -> verifyFromLogin()
            R.id.txtSignUp -> {
                intent = Intent(this, SignupActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    /**
     * This method is to validate the input text fields and verify login credentials
     */
    private fun verifyFromLogin() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_username))) {
            return
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return
        }

        progressDialog(activity, activity.getString(R.string.auth))
        val username = textInputEditTextEmail.text.toString().trim()
        val password = textInputEditTextPassword.text.toString().trim()

        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, Config.LOGIN_URL,
                Response.Listener { response ->
                    when {
                        response.contains(Config.SUCCESS) -> {
                            val sharedPreferences =
                                    activity.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            val data = response.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            editor.putBoolean(Config.LOGGED_IN_SHARED_PREF, true)
                            editor.putString(Config.ID_USER_SHARED_PREF, data[1])
                            editor.putString(Config.FULL_NAME_SHARED_PREF, data[2])
                            editor.putString(Config.USERNAME_SHARED_PREF, username)
                            editor.putString(Config.IMG_PHOTO_SHARED_PREF, data[3])
                            editor.apply()
                            editor.commit()
                            emptyInputEditText()
                            hideProgressDialog()
                            val intent = Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        response.contains(Config.WRONG_USER_NAME) -> {
                            hideProgressDialog()
                            Snackbar.make(rootView, getString(R.string.error_valid_username), Snackbar.LENGTH_LONG).show()
                        }
                        response.contains(Config.WRONG_PASSWORD) -> {
                            hideProgressDialog()
                            Snackbar.make(rootView, getString(R.string.error_valid_password), Snackbar.LENGTH_LONG).show()
                        }
                    }
                },
                Response.ErrorListener {
                    hideProgressDialog()
                    Snackbar.make(rootView, getString(R.string.connection_failed), Snackbar.LENGTH_LONG).show()
                }){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params[Config.USERNAME_SHARED_PREF] = username
                params[Config.PASSWORD_PARAM] = password
                return params
            }
        }
        queue.add(stringRequest)
    }

    /**
     * This method is to empty all input edit text
     */
    private fun emptyInputEditText() {
        textInputEditTextEmail.text = null
        textInputEditTextPassword.text = null
    }
}