package fr.sercurio.saoul_seek

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import fr.sercurio.saoul_seek.models.Peer
import fr.sercurio.saoul_seek.models.Room
import fr.sercurio.saoul_seek.models.RoomMessage
import fr.sercurio.saoul_seek.models.SoulFile
import fr.sercurio.saoul_seek.slsk_android.R
import fr.sercurio.saoul_seek.slsk_android.databinding.ActivitySoulBinding
import fr.sercurio.saoul_seek.socket.PeerManager
import fr.sercurio.saoul_seek.socket.ServerClient
import fr.sercurio.saoul_seek.ui.fragments.PreferencesFragment
import fr.sercurio.saoul_seek.ui.fragments.RoomFragment
import fr.sercurio.saoul_seek.ui.fragments.SearchFragment
import fr.sercurio.saoul_seek.ui.fragments.child.SearchChildFragment
import fr.sercurio.saoul_seek.ui.fragments.child.SearchChildFragment.SearchChildInterface
import fr.sercurio.saoul_seek.utils.Bytes
import fr.sercurio.saoul_seek.utils.SoulStack


/**
 * Créé et codé par Louis Penalva tout droits réservés.
 */
@AndroidEntryPoint
class SoulActivity : AppCompatActivity(), ServerClient.ServerSocketInterface, PeerManager.PeerSocketManagerInterface, SearchChildInterface, RoomFragment.RoomFragmentInterface {
    private lateinit var binding: ActivitySoulBinding


    /* Fragments */
    private val roomFragment = RoomFragment()
    private val searchFragment = SearchFragment()
    private val preferencesFragment = PreferencesFragment()

    /* Logger */
    private val tag = SoulActivity::class.java.simpleName

    /* Managers */
    private lateinit var serverClient: ServerClient
    private lateinit var peerManager: PeerManager

    /* BottomNavigationListener */
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
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

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        /*CoroutineScope(IO).launch {
            Log.d("IPLocal", Utils.getIPAddress(true))
            Log.d("IPInternet", Utils.getPublicIp())
        }*/
        /*CoroutineScope(IO).launch {
            val peerServer = PeerServer(2234)
        }*/

        peerManager = PeerManager()
        val peerManagerThread = Thread(peerManager)
        peerManagerThread.start()
        peerManager.setPeerSocketManagerInterface(this@SoulActivity)


        serverClient = ServerClient.getInstance(
                sharedPreference.getString("key_login", "")!!,
                sharedPreference.getString("key_password", "")!!,
                5001,
                sharedPreference.getString("key_host", "")!!,
                sharedPreference.getString("key_port", "0")!!.toInt())
        val soulServerThread = Thread(serverClient)
        soulServerThread.start()
        serverClient.setServerSocketInterface(this@SoulActivity)
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
        serverClient.stop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    /************************/
    /* SearchChildInterface */
    /************************/
    override fun onQueryChangeListener(query: String?) {
        val token = Bytes.tokenInt()
        if (query != null) {
            SoulStack.searches[token] = query
            SoulStack.actualSearchToken = token
            Log.d(tag, "search: ${SoulStack.searches[SoulStack.actualSearchToken]}, token: ${SoulStack.actualSearchToken}")
            serverClient.fileSearch(query, token)
        }
    }

    override fun onSoulfileDownloadQuery(peer: Peer, soulFile: SoulFile) {
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
    override fun onRoomSpinnerItemSelected(roomName: String) {
        serverClient.joinRoom(roomName)
    }

    override fun onRoomMessageSend(roomMessage: RoomMessage) {
        serverClient.sendRoomMessage(roomMessage)
    }

    /*************************/
    /* SERVER SOCKET METHODS */
    /*************************/
    override fun onLogin(connected: Int, greeting: String, ipAddress: String) {
        runOnUiThread {
            if (connected == 1) Toast.makeText(this@SoulActivity, "Connected", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this@SoulActivity, "Not Connected", Toast.LENGTH_SHORT).show()
        }
    }
    /***********************/
    /* SoulSocketInterface */
    /***********************/
    override fun onConnectToPeer(peer: Peer) {
        peerManager.addWaitingPeer(peer)
    }

    override fun onRoomMessage(roomName: String, username: String, message: String) {
        runOnUiThread {
            roomFragment.addRoomMessage(RoomMessage(roomName, username, message))
        }
    }

    override fun onUserJoinRoom(roomName: String, username: String, status: Int, averageSpeed: Int, downloadNum: Int, nbFiles: Int, nbDirectories: Int, slotsFree: Int, countryCode: String) {
        runOnUiThread {
            roomFragment.addRoomMessage(RoomMessage(roomName, username, "a rejoint la room"))
        }
    }

    override fun onUserLeftRoom(roomName: String, username: String) {
        runOnUiThread {
            roomFragment.addRoomMessage(RoomMessage(roomName, username, "a quitté la room"))
        }
    }

    override fun onRoomList(rooms: ArrayList<Room>) {
        runOnUiThread {
            roomFragment.setRoomList(rooms)
        }
    }

    /******************************/
    /* PeerSocketManagerInterface */
    /******************************/
    override fun onGetSharedList() {
        TODO("Not yet implemented")
    }

    override fun onFileSearchResult(peer: Peer) {
        runOnUiThread {
            searchFragment.addSoulFiles(peer)
        }
    }

    override fun onFolderContentsRequest(numberOfFiles: Int) {
        TODO("Not yet implemented")
    }

    override fun onTransferDownloadRequest(token: Long, allowed: Int, reason: String?) {
        TODO("Not yet implemented")
    }

    override fun onUploadFailed(filename: String) {
        TODO("Not yet implemented")
    }

    override fun onQueueFailed(filename: String?, reason: String) {
        TODO("Not yet implemented")
    }
}