package fr.sercurio.soulseek.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import fr.sercurio.soulseek.SoulActivity
//import fr.sercurio.soulseek.BuildConfig
import fr.sercurio.soulseek.databinding.ActivitySettingsBinding
import fr.sercurio.soulseek.utils.AndroidUiHelper
import java.io.File

/**
 * Activité qui récupère les préférences de l'utilisateur
 * Skip si les préférences sont déja remplies
 */
class SettingsActivity : AppCompatActivity() {
    private val tag = SettingsActivity::class.java.toString()
    private lateinit var binding: ActivitySettingsBinding


    /* Constants */
    private val loginKey = "key_login"
    private val passwordKey = "key_password"
    private val hostKey = "key_host"
    private val portKey = "key_port"
    private val downloadDirectory = "key_download_directory"

    private val downloadDefaultDirName = "soulfiles"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPreference.edit()

//        checkFirstRun(this, sharedPreference)

        val appSpecificExternalDir = File(getExternalFilesDir(null), downloadDefaultDirName)
        appSpecificExternalDir.mkdir()
        editor.putString(downloadDirectory, downloadDefaultDirName)

        binding.goButton.setOnClickListener {
            if (checkIfSettingsAreEmpty() == 1) return@setOnClickListener

            editor.putString(loginKey, binding.loginText.text.toString())
            editor.putString(passwordKey, binding.passwordText.text.toString())
            editor.putString(hostKey, binding.hostText.text.toString())
            editor.putString(portKey, binding.portText.text.toString())
            editor.apply()

            val soulActivity = Intent(this@SettingsActivity, SoulActivity::class.java)
            AndroidUiHelper.hideKeyboard(this@SettingsActivity)
            startActivity(soulActivity)
            finish()
        }
    }

    fun browseSharesDirectory(view: View?) {
        // not implemented
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {/*
            intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, 0)
            */
        } else {

        }
    }

    /**
     * Mécanisme qui active/désactive les paramètres avancés
     *
     */
    fun toggleAdvanced() {
        if (binding.advancedCheck.isChecked) {
            binding.hostText.isEnabled = true
            binding.portText.isEnabled = true
        } else {
            binding.hostText.isEnabled = false
            binding.portText.isEnabled = false
        }
    }

//    private fun checkFirstRun(settingsActivity: SettingsActivity, sharedPreference: SharedPreferences) {
//        val startSoul = Intent(this, SoulActivity::class.java)
//        val prefVersionCodeKey = "0.1"
//        val doestExist = -1
//
//        // Get current version code
//        val currentVersionCode = BuildConfig.VERSION_CODE
//
//        // Get saved version code
//        val savedVersionCode = sharedPreference.getInt(prefVersionCodeKey, -1)
//        Log.i("Version saved & current", "$currentVersionCode $savedVersionCode")
//
//        // Check for first run or upgrade
//        when {
//            (currentVersionCode == savedVersionCode || currentVersionCode > savedVersionCode) && preferencesAreSaved(
//                sharedPreference
//            ) -> {
//                startActivity(startSoul)
//                settingsActivity.finish()
//            }
//
//            savedVersionCode == doestExist -> {
//            }/*currentVersionCode > savedVersionCode -> {
//            }
//            */// TODO This is an upgrade
//
//            // Update the shared preferences with the current version code
//        }
//        // TODO This is an upgrade
//
//        // Update the shared preferences with the current version code
//        sharedPreference.edit().putInt(prefVersionCodeKey, currentVersionCode).apply()
//    }

    private fun checkIfSettingsAreEmpty(): Int {
        var error = 0
        if (TextUtils.isEmpty(binding.loginText.text.toString())) {
            binding.loginText.error = "Login cannot be empty."
            error = 1
        }
        if (TextUtils.isEmpty(binding.passwordText.text.toString())) {
            binding.passwordText.error = "Password cannot be empty."
            error = 1
        }
        if (TextUtils.isEmpty(binding.hostText.text.toString())) {
            binding.hostText.error = "Host cannot be empty."
            error = 1
        }
        if (TextUtils.isEmpty(binding.portText.text.toString())) {
            binding.portText.error = "Port cannot be empty."
            error = 1
        }
        return error
    }

    private fun preferencesAreSaved(sharedPreference: SharedPreferences): Boolean {
        Log.d(
            tag, "login : ${sharedPreference.getString(loginKey, "")}\n" + "pwd: ${
                sharedPreference.getString(
                    passwordKey, ""
                )
            }\n" + "host : ${sharedPreference.getString(hostKey, "")}\n" + "port : ${
                sharedPreference.getString(
                    portKey, ""
                )
            })"
        )


        return !sharedPreference.getString(loginKey, "").equals("") && !sharedPreference.getString(passwordKey, "")
            .equals("") && !sharedPreference.getString(hostKey, "").equals("") && !sharedPreference.getString(
            portKey, "0"
        ).equals("0")
    }
}