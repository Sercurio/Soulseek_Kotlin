package fr.sercurio.saoul_seek.socket

import android.util.Log
import fr.sercurio.saoul_seek.models.Peer
import fr.sercurio.saoul_seek.utils.SoulStack
import java.net.SocketException
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import kotlin.random.Random

class PeerManager : Runnable {
    private val tag = this.javaClass.name
    private val nbThreads = 20
    private val executorService = Executors.newFixedThreadPool(nbThreads)
    private var peersWaitingToConnect: BlockingQueue<Peer> = LinkedBlockingQueue()
    private var peers = ArrayList<Peer>()
    private lateinit var peerSocketManagerInterface: PeerSocketManagerInterface

    override fun run() {
        while (true) {
            val iterator = peersWaitingToConnect.iterator()
            while (iterator.hasNext()) {
                connectToPeer(peersWaitingToConnect.take())
            }
            Thread.sleep(500)
            checkPeersWithoutResults()
        }
    }

    private fun checkPeersWithoutResults() {
        val iterator = peers.iterator()
        while (iterator.hasNext()) {
            val peer = iterator.next()
            if (!peer.searchResults) {
                println("close socket of $peer")
                peer.socketPeer!!.stop()
                iterator.remove()
            }
        }
    }

    fun addWaitingPeer(peer: Peer) {
        peersWaitingToConnect.put(peer)
    }


    private fun connectToPeer(peer: Peer) {
        val peerSocket = object : PeerSocket(peer) {
            override fun onSocketConnected(soulSocket: SoulSocket) {
                soulSocket as PeerSocket
                val token = Random.nextLong()
                pierceFirewall(peer.token)
                peerInit(peer.username, "P", token)
                //getShareFileList()
                //userInfoRequest()
                peer.socketPeer = this
                peers.add(peer)
                //Log.d(tag, "${token}, ${soulStack.searches[soulStack.actualSearchToken]}")
                val actualSearch = SoulStack.searches[SoulStack.actualSearchToken] ?: ""
                fileSearchRequest(token, actualSearch)
            }

            override fun onSocketDisconnected(exception: Exception) {
                if (exception is SocketException) {
                    peers.remove(peer)
                }
                Log.e(tag, exception.toString())
            }

            override fun onAbstractGetSharedList() {
                peerSocketManagerInterface.onGetSharedList()
            }

            override fun onAbstractFileSearchResult(peer: Peer) {
                peer.searchResults = true
                println("actual token: ${SoulStack.actualSearchToken} peer token : ${peer.token}")
                //if (SoulStack.actualSearchToken == peer.token)
                peerSocketManagerInterface.onFileSearchResult(peer)
            }

            override fun onAbstractFolderContentsRequest(numberOfFiles: Int) {
                peerSocketManagerInterface.onFolderContentsRequest(numberOfFiles)
            }

            override fun onAbstractTransferDownloadRequest(token: Long, allowed: Int, reason: String?) {
                peerSocketManagerInterface.onTransferDownloadRequest(token, allowed, reason)
            }

            override fun onAbstractUploadFailed(filename: String) {
                peerSocketManagerInterface.onUploadFailed(filename)
            }

            override fun onAbstractQueueFailed(filename: String?, reason: String) {
                peerSocketManagerInterface.onQueueFailed(filename, reason)
            }
        }.also { executorService.submit(it) }
    }


    fun setPeerSocketManagerInterface(callback: PeerSocketManagerInterface) {
        this.peerSocketManagerInterface = callback
    }

    interface PeerSocketManagerInterface {
        fun onGetSharedList()
        fun onFileSearchResult(peer: Peer)
        fun onFolderContentsRequest(numberOfFiles: Int)
        fun onTransferDownloadRequest(token: Long, allowed: Int, reason: String?)
        fun onUploadFailed(filename: String)
        fun onQueueFailed(filename: String?, reason: String)
    }

    private fun closePeerSocket(peer: Peer) {
        peer.socketPeer?.let {
            if (it.connected) {
                Log.d(tag, "close socket of $peer")
                peer.socketPeer!!.stop()
                peers.remove(peer)
            }
        }
    }
}