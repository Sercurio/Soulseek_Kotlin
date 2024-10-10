package fr.sercurio.soulseek

import android.os.Bundle
import android.os.Environment
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val api = SoulseekApi()
        api.setSaveDirectory(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC
            ).toString()
        )

        setContent {
            NavigationGraph(
                soulseekApi = api
            )
        }
    }
}