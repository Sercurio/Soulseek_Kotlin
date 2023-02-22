package fr.sercurio.soulseek

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import fr.sercurio.soulseek.databinding.ActivitySoulBinding
import fr.sercurio.soulseek.entities.PeerApiModel
import fr.sercurio.soulseek.entities.RoomApiModel
import fr.sercurio.soulseek.entities.RoomMessageApiModel
import fr.sercurio.soulseek.entities.SoulFile
import fr.sercurio.soulseek.ui.fragments.PreferencesFragment
import fr.sercurio.soulseek.ui.fragments.RoomFragment
import fr.sercurio.soulseek.ui.fragments.SearchFragment
import fr.sercurio.soulseek.utils.Bytes
import fr.sercurio.soulseek.viewmodel.LoginViewModel
import kotlinx.coroutines.*
import soulseek.ui.fragments.child.SearchChildFragment
import soulseek.ui.fragments.child.SearchChildFragment.SearchChildInterface
import soulseek.utils.SoulStack


/**
 * Créé et codé par Louis Penalva tout droits réservés.
 */
@AndroidEntryPoint
class SoulActivity : AppCompatActivity(), CoroutineScope by MainScope(),
    SearchChildInterface,
    RoomFragment.RoomFragmentInterface {

    private lateinit var binding: ActivitySoulBinding

    /* Fragments */
    private val roomFragment = RoomFragment()
    private val searchFragment = SearchFragment()
    private val preferencesFragment = PreferencesFragment()

    /* Logger */
    private val tag = SoulActivity::class.java.simpleName

    /* Managers */
    private lateinit var soulSeekApi: SoulSeekApi

    /* BottomNavigationListener */
    private val onNavigationItemSelectedListener = NavigationBarView.OnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.destination_search -> setCurrentFragment(searchFragment)
            R.id.destination_room -> setCurrentFragment(roomFragment)
            R.id.destination_preferences -> setCurrentFragment(preferencesFragment)
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySoulBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setCurrentFragment(roomFragment)

        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)

        binding.bottomNavigationView.setOnItemSelectedListener(onNavigationItemSelectedListener)

        val viewModel: LoginViewModel by viewModels()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    println(it.connected)
                }
            }
        }

        soulSeekApi = SoulSeekApi(
            "DebugApp",
            "159753",
        )

        /*SoulSeekApi(
        sharedPreference.getString("key_login", "")!!,
        sharedPreference.getString("key_password", "")!!,
        5001,
        sharedPreference.getString("key_host", "")!!,
        sharedPreference.getString("key_port", "0")!!.toInt()
    ) */
    }


    override fun onAttachFragment(fragment: Fragment) {
        when (fragment) {
            is RoomFragment -> {
                fragment.setRoomFragmentInterface(this)
            }

            is SearchChildFragment -> {
                fragment.setSearchChildInterface(this)
            }

            is PreferencesFragment -> {
                //
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soulSeekApi.clientSoul.close()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    /************************/
    /* SearchChildInterface */
    /************************/
    override suspend fun onQueryChangeListener(query: String?) {
        val token = Bytes.tokenInt()
        if (query != null) {
            SoulStack.searches[token] = query
            SoulStack.actualSearchToken = token
            Log.d(
                tag,
                "search: ${SoulStack.searches[SoulStack.actualSearchToken]}, token: ${SoulStack.actualSearchToken}"
            )
            soulSeekApi.clientSoul.fileSearch(query)
        }
    }

    override fun onSoulfileDownloadQuery(peer: PeerApiModel, soulFile: SoulFile) {
        //TODO send to a peer a transfer request
    }

    /****************************/
    /* FRAGMENT MANAGER METHODS */
    /****************************/
    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
                .commit()
        }
    }
    /**************/
    /* UI METHODS */
    /**************/
    override suspend fun onRoomSpinnerItemSelected(roomName: String) {
        soulSeekApi.clientSoul.joinRoom(roomName)
    }

    override suspend fun onRoomMessageSend(roomMessage: RoomMessageApiModel) {
        soulSeekApi.clientSoul.sendRoomMessage(roomMessage)
    }

    /*************************/
    /* SERVER SOCKET METHODS */
    /*************************/
    fun onLogin(connected: Int, greeting: String, ipAddress: String) {
        runOnUiThread {
            if (connected == 1) Toast.makeText(this@SoulActivity, "Connected", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this@SoulActivity, "Not Connected", Toast.LENGTH_SHORT).show()
        }
    }

    fun onRoomMessage(roomName: String, username: String, message: String) {
        runOnUiThread {
            roomFragment.addRoomMessage(RoomMessageApiModel(roomName, username, message))
        }
    }

    fun onUserJoinRoom(
        roomName: String,
        username: String,
        status: Int,
        averageSpeed: Int,
        downloadNum: Int,
        nbFiles: Int,
        nbDirectories: Int,
        slotsFree: Int,
        countryCode: String
    ) {
        runOnUiThread {
            roomFragment.addRoomMessage(RoomMessageApiModel(roomName, username, "a rejoint la room"))
        }
    }

    fun onUserLeftRoom(roomName: String, username: String) {
        runOnUiThread {
            roomFragment.addRoomMessage(RoomMessageApiModel(roomName, username, "a quitté la room"))
        }
    }

    fun onRoomList(rooms: ArrayList<RoomApiModel>) {
        runOnUiThread {
            roomFragment.setRoomList(rooms)
        }
    }

    /******************************/
    /* PeerSocketManagerInterface */
    /******************************/
    fun onGetSharedList() {
        TODO("Not yet implemented")
    }

    fun onFileSearchResult(peer: PeerApiModel) {
        runOnUiThread {
            searchFragment.addSoulFiles(peer)
        }
    }

    fun onFolderContentsRequest(numberOfFiles: Int) {
        TODO("Not yet implemented")
    }

    fun onTransferDownloadRequest(token: Long, allowed: Int, reason: String?) {
        TODO("Not yet implemented")
    }

    fun onUploadFailed(filename: String) {
        TODO("Not yet implemented")
    }

    fun onQueueFailed(filename: String?, reason: String) {
        TODO("Not yet implemented")
    }
}