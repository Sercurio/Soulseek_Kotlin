package fr.sercurio.saoul_seek

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import fr.sercurio.saoul_seek.slsk_android.R
import org.acra.ACRA
import org.acra.ReportingInteractionMode
import org.acra.annotation.ReportsCrashes

@HiltAndroidApp
@ReportsCrashes(
        mailTo = "penalva.louis@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
class SoulApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        ACRA.init(this)
    }
}