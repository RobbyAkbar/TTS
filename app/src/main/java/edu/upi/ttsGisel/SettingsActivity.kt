package edu.upi.ttsGisel

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import edu.upi.ttsGisel.utils.AppUtils
import edu.upi.ttsGisel.utils.Config


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            when (preference.key) {
                getString(R.string.account) -> logout()
                getString(R.string.help) -> help()
                getString(R.string.about) -> about()
                getString(R.string.profile) -> profile()
                getString(R.string.feedback) -> feedback()
            }
            return super.onPreferenceTreeClick(preference)
        }
        private fun logout() {
            val builder = AlertDialog.Builder(requireContext(), R.style.AppCompatAlertDialogStyle)
            builder.setMessage(getString(R.string.message_logout))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        AppUtils.deletePref(requireContext())
                        requireActivity().finish()
                    }
                    .setNegativeButton(
                            getString(R.string.no)
                    ) { dialogInterface, _ -> dialogInterface.cancel() }
                    .show()
        }
        private fun help(){
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=bIz0Lck2orc"))
            try {
                requireActivity().startActivity(webIntent)
            } catch (ex: ActivityNotFoundException) { }
        }
        private fun about(){
            val builder = AlertDialog.Builder(requireContext(), R.style.AppCompatAlertDialogStyle)
            val dialogLayout = View.inflate(activity, R.layout.about_layout, null)
            builder.setCancelable(true)
                    .setView(dialogLayout)
                    .setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }
                    .show()
        }
        private fun profile(){

        }
        private fun feedback(){
            val contact = "+6289666549850" // use country code with your phone number
            val message = getString(R.string.message_feedback)
            val url = "https://wa.me/$contact/?text=$message"
            try {
                requireActivity().packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(activity, getString(R.string.no_whatsapp), Toast.LENGTH_SHORT)
                        .show()
                e.printStackTrace()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}