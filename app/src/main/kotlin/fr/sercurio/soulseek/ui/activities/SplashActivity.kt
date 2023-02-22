package soulseek.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import fr.sercurio.soulseek.databinding.ActivitySplashBinding

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