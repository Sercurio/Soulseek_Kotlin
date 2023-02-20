package fr.sercurio.saoul_seek.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import fr.sercurio.saoul_seek.slsk_android.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        webView.loadUrl("file:///android_res/drawable/soulseek.gif")

        Handler().postDelayed({
            val mainIntent = Intent(this@SplashActivity, SettingsActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, 1000)


    }
}