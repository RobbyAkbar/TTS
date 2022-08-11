package edu.upi.ttsGisel

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import de.hdodenhof.circleimageview.CircleImageView
import edu.upi.ttsGisel.helpers.InputValidation
import edu.upi.ttsGisel.helpers.MultiPartRequester
import edu.upi.ttsGisel.listeners.AsyncTaskCompleteListener
import edu.upi.ttsGisel.utils.AppUtils.Companion.hideProgressDialog
import edu.upi.ttsGisel.utils.AppUtils.Companion.progressDialog
import edu.upi.ttsGisel.utils.Config
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class SignupActivity : AppCompatActivity(), View.OnClickListener, AsyncTaskCompleteListener {

    private val activity = this@SignupActivity
    private val gallery = 1

    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var textInputLayoutName: TextInputLayout
    private lateinit var textInputLayoutEmail: TextInputLayout
    private lateinit var textInputLayoutPassword: TextInputLayout
    private lateinit var textInputLayoutConfirmPassword: TextInputLayout
    private lateinit var textInputLayoutAge: TextInputLayout

    private lateinit var textInputEditTextName: TextInputEditText
    private lateinit var textInputEditTextEmail: TextInputEditText
    private lateinit var textInputEditTextPassword: TextInputEditText
    private lateinit var textInputEditTextConfirmPassword: TextInputEditText
    private lateinit var textInputEditTextAge: TextInputEditText

    private lateinit var spGender: AppCompatSpinner
    private lateinit var appCompatButtonRegister: AppCompatButton
    private lateinit var imgProfile: CircleImageView
    private lateinit var fabAddPhoto: FloatingActionButton

    private lateinit var inputValidation: InputValidation
    private var arraylist: ArrayList<HashMap<String, String>>? = null
    private var path: String? = null
    private var urlImg: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        requestMultiplePermissions()

        // initializing the views
        initViews()

        // initializing the listeners
        initListeners()

        // initializing the objects
        initObjects()

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    /**
     * This method is to initialize views
     */
    private fun initViews() {
        constraintLayout = findViewById<View>(R.id.constraintLayout) as ConstraintLayout

        textInputLayoutName = findViewById<View>(R.id.textInputLayoutName) as TextInputLayout
        textInputLayoutEmail = findViewById<View>(R.id.textInputLayoutEmail) as TextInputLayout
        textInputLayoutPassword = findViewById<View>(R.id.textInputLayoutPassword) as TextInputLayout
        textInputLayoutConfirmPassword = findViewById<View>(R.id.textInputLayoutConfirmPassword) as TextInputLayout
        textInputLayoutAge = findViewById<View>(R.id.textInputLayoutAge) as TextInputLayout

        textInputEditTextName = findViewById<View>(R.id.textInputEditTextName) as TextInputEditText
        textInputEditTextEmail = findViewById<View>(R.id.textInputEditTextEmail) as TextInputEditText
        textInputEditTextPassword = findViewById<View>(R.id.textInputEditTextPassword) as TextInputEditText
        textInputEditTextConfirmPassword = findViewById<View>(R.id.textInputEditTextConfirmPassword) as TextInputEditText
        textInputEditTextAge = findViewById<View>(R.id.textInputEditTextAge) as TextInputEditText

        spGender = findViewById<View>(R.id.spGender) as AppCompatSpinner
        appCompatButtonRegister = findViewById<View>(R.id.appCompatButtonRegister) as AppCompatButton
        imgProfile = findViewById<View>(R.id.imgProfilePicture) as CircleImageView
        fabAddPhoto = findViewById<View>(R.id.fab) as FloatingActionButton
    }

    /**
     * This method is to initialize listeners
     */
    private fun initListeners() {
        appCompatButtonRegister.setOnClickListener(this)
        fabAddPhoto.setOnClickListener(this)
    }

    /**
     * This method is to initialize objects to be used
     */
    private fun initObjects() {
        inputValidation = InputValidation(activity)
        val adapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGender.adapter = adapter
    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.appCompatButtonRegister -> checkData()
            R.id.fab -> choosePhotoFromGallery()
        }
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, gallery)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == gallery) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    path = saveImage(bitmap)
                    Toast.makeText(activity, "Image Saved!", Toast.LENGTH_SHORT).show()
                    imgProfile.setImageBitmap(bitmap)
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImage(path: String) {
        val map = HashMap<String, String>()
        map["url"] = Config.UPLOAD_IMG_URL
        map["filename"] = path
        MultiPartRequester(this, map, gallery, this)
    }

    override fun onTaskCompleted(response: String, serviceCode: Int) {
        Log.d("respon", response)
        when (serviceCode) {
            gallery -> if (isSuccess(response)) {
                urlImg = getURL(response)
                postData()
            }
        }
    }

    private fun isSuccess(response: String): Boolean {
        try {
            val jsonObject = JSONObject(response)
            return jsonObject.optString("status") == "true"

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return false
    }


    private fun getURL(response:String):String {
        var url = ""
        try {
            val jsonObject = JSONObject(response)
            jsonObject.toString().replace("\\\\", "")
            if (jsonObject.getString("status") == "true") {
                arraylist = ArrayList()
                val dataArray = jsonObject.getJSONArray("data")
                for (i in 0 until dataArray.length())
                {
                    val dataobj = dataArray.getJSONObject(i)
                    url = dataobj.optString("pathToFile")
                }
            }
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }
        return url
    }

    companion object {
        private const val IMAGE_DIRECTORY = "/img_upload"
    }

    private fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                getExternalFilesDir(IMAGE_DIRECTORY).toString())
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                    arrayOf(f.path),
                    arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)
            return f.absolutePath
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }

    private fun checkData(){
        if (!inputValidation.isInputEditTextFilled(textInputEditTextName, textInputLayoutName, getString(R.string.error_message_name))) {
            return
        }
        if (!inputValidation.isInputTextMinimum(textInputEditTextName, textInputLayoutName, 3)){
            return
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_username))) {
            return
        }
        if (!inputValidation.isInputTextMinimum(textInputEditTextEmail, textInputLayoutEmail, 3)){
            return
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return
        }
        if (!inputValidation.isInputTextMinimum(textInputEditTextPassword, textInputLayoutPassword, 6)){
            return
        }
        if (!inputValidation.isInputEditTextMatches(textInputEditTextPassword, textInputEditTextConfirmPassword, textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
            return
        }
        if(path==null) {
            Snackbar.make(constraintLayout, getString(R.string.error_img), Snackbar.LENGTH_SHORT).show()
            return
        } else uploadImage(path!!)
    }

    /**
     * This method is to validate the input text fields and post data
     */
    private fun postData() {
        progressDialog(activity, activity.getString(R.string.creating_account))
        val fullName = textInputEditTextName.text.toString().trim()
        val username = textInputEditTextEmail.text.toString().trim()
        val password = textInputEditTextPassword.text.toString().trim()
        val age = textInputEditTextAge.text.toString().trim()
        val gender = spGender.selectedItemPosition
        var genderText = ""

        if (gender == 0) genderText = "Male"
        else if (gender == 1) genderText = "Female"

        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, Config.SIGN_UP_URL,
                Response.Listener {response ->
                    when {
                        response.contains(Config.SUCCESS) -> {
                            emptyInputEditText()
                            hideProgressDialog()
                            Snackbar.make(constraintLayout, getString(R.string.success_message), Snackbar.LENGTH_LONG).show()
                            val sDL = 1000
                            Handler().postDelayed({
                                finish()
                            }, sDL.toLong())
                        }
                        response.contains(Config.DUPLICATE) -> {
                            hideProgressDialog()
                            Snackbar.make(constraintLayout, getString(R.string.success_message), Snackbar.LENGTH_LONG).show()
                        }
                        else -> {
                            hideProgressDialog()
                            Log.i("info", response.toString())
                            Snackbar.make(constraintLayout, getString(R.string.failed_account), Snackbar.LENGTH_LONG).show()
                        }
                    }
                },
                Response.ErrorListener { error ->
                    hideProgressDialog()
                    Log.e("error", error.message.toString())
                    Snackbar.make(constraintLayout, getString(R.string.connection_failed), Snackbar.LENGTH_LONG).show()
                }){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params[Config.USERNAME_SHARED_PREF] = username
                params[Config.FULL_NAME_SHARED_PREF] = fullName
                params[Config.PASSWORD_PARAM] = password
                params[Config.AGE_PARAM] = age
                params[Config.GENDER_PARAM] = genderText
                params[Config.URL_IMG_PARAM] = urlImg!!
                return params
            }
        }
        queue.add(stringRequest)
    }

    /**
     * This method is to empty all input edit text
     */
    private fun emptyInputEditText() {
        textInputEditTextName.text = null
        textInputEditTextEmail.text = null
        textInputEditTextPassword.text = null
        textInputEditTextConfirmPassword.text = null
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(applicationContext, "All permissions are granted by user!", Toast.LENGTH_SHORT)
                                    .show()
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { Toast.makeText(applicationContext, "Some Error! ", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }
}