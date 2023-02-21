package fr.sercurio.soulseek

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import fr.sercurio.saoul_seek.slsk_android.R
import org.acra.ACRA
import org.acra.BuildConfig
import org.acra.config.mailSender
import org.acra.config.toast
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

@HiltAndroidApp
class SoulApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        initAcra {
            //core configuration:
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON

            mailSender {
                //required
                mailTo = "penalva.louis@gmail.com"
                //defaults to true
                reportAsFile = true
                //defaults to ACRA-report.stacktrace
                reportFileName = "Crash.txt"
                //defaults to "<applicationId> Crash Report"
                //subject = getString(R.string.mail_subject)
                //defaults to empty
                //body = getString(R.string.mail_body)
            }

            //each plugin you chose above can be configured in a block like this:
            toast {
                text = getString(R.string.acra_toast_text)
                //opening this block automatically enables the plugin.
            }
        }
    }
}