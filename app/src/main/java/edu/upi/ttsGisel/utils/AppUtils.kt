package edu.upi.ttsGisel.utils

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import edu.upi.ttsGisel.R

class AppUtils {

    companion object {
        fun hasUserLoggedin(context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(Config.LOGGED_IN_SHARED_PREF, false)
        }
        fun getIdUser(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(Config.ID_USER_SHARED_PREF, "")
        }
        fun getName(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(Config.FULL_NAME_SHARED_PREF, "")
        }
        private lateinit var builder: AlertDialog.Builder
        private lateinit var dialog: AlertDialog
        fun progressDialog(context: Context, message: String){
            builder = AlertDialog.Builder(context)
            val dialogLayout = View.inflate(context, R.layout.dialog_layout, null)
            val textView = dialogLayout.findViewById<TextView>(R.id.textView)
            builder.setView(dialogLayout)
            builder.setCancelable(false)
            textView.text = message
            dialog = builder.create()
            dialog.show()
        }
        fun hideProgressDialog() {
            dialog.dismiss()
        }
        fun deletePref(context: Context){
            val preferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
            editor.commit()
        }
        fun getLesson(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(Config.LESSON_SHARED_PREF, "")
        }
        fun getStatusLesson(type: String, id: String, context: Context): String? {
            val sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(Config.LESSON_STATUS_SHARED_PREF+type+id, "")
        }
        fun getStatusLesson(id: String, context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(Config.LESSON_STATUS_SHARED_PREF+id, false)
        }
        fun isNetworkStatusAvialable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfos = connectivityManager.activeNetworkInfo
            if (netInfos != null)
                return netInfos.isConnected
            return false
        }
    }

}