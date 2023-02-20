package fr.sercurio.saoul_seek.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import fr.sercurio.saoul_seek.slsk_android.R
import fr.sercurio.saoul_seek.slsk_android.R.layout.activity_splash
import fr.sercurio.saoul_seek.slsk_android.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.webView.loadUrl("file:///android_res/drawable/soulseek.gif")

        Handler().postDelayed({
            val mainIntent = Intent(this@SplashActivity, SettingsActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, 1000)


    }
}