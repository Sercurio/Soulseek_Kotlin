package soulseek.ui.fragments.child

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import fr.sercurio.soulseek.custom_view.expandable_list_view.ExpandableSoulResultsAdapter
import fr.sercurio.soulseek.R
import fr.sercurio.soulseek.utils.AndroidUiHelper
import fr.sercurio.soulseek.entities.PeerApiModel
import fr.sercurio.soulseek.entities.SoulFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class SearchChildFragment : Fragment() {
    private var searchResultList: MutableMap<PeerApiModel, List<SoulFile>> = mutableMapOf()
    private lateinit var expandableSoulResultsAdapter: ExpandableSoulResultsAdapter

    private lateinit var searchChildInterface: SearchChildInterface

    private lateinit var progressBar: ProgressBar

    private var lastSearch: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SearchChildInterface) {
            searchChildInterface = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_childsearch, container, false)

        /* SearchView */
        val searchView = rootView.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (lastSearch != query)
                    searchResultList.clear()
                clearSearchResults()

                lastSearch = query

                searchChildInterface.onQueryChangeListener(query)

                AndroidUiHelper.hideKeyboard(parentFragment?.activity as Activity)
                progressBar.visibility = View.VISIBLE
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })

        /* ProgressBar */
        progressBar = rootView.findViewById(R.id.progressBar)

        /* ExpandableListView */
        val expandableView = rootView.findViewById<ExpandableListView>(R.id.expandableListView)
        expandableSoulResultsAdapter =
            ExpandableSoulResultsAdapter(this.requireContext(), searchResultList.keys.toMutableList(), searchResultList)
        expandableView.setAdapter(expandableSoulResultsAdapter)
        expandableSoulResultsAdapter.onClickDlButtonListener = { peer: PeerApiModel, soulFile: SoulFile ->
            //TODO
            Toast.makeText(context, "Not working yet", Toast.LENGTH_LONG).show()
            Log.d(tag, "\n$peer,\n$soulFile")
            searchChildInterface.onSoulfileDownloadQuery(peer, soulFile)
            //peer.socketPeer?.peerInit(peer.username, "P", Bytes.randomBytes(4))
            CoroutineScope(Dispatchers.IO).launch {
                peer.clientPeer?.transferRequest(0, peer.token, soulFile, null)
            }
        }

        return rootView
    }


    fun addSearchResults(peer: PeerApiModel) {
        expandableSoulResultsAdapter.addHeaderAndItems(peer)
    }

    fun clearSearchResults() {
        expandableSoulResultsAdapter.clearHeaderAndItems()
    }

    fun setSearchChildInterface(callback: SearchChildInterface) {
        this.searchChildInterface = callback
    }


    companion object {
        private const val MY_BOOLEAN = "0"
        private const val MY_INT = "my_int"

        fun newInstance(aBoolean: Boolean, anInt: Int) = SearchChildFragment().apply {
            arguments = Bundle(2).apply {
                putBoolean(MY_BOOLEAN, aBoolean)
                putInt(MY_INT, anInt)
            }
        }
    }

    interface SearchChildInterface {
        fun onQueryChangeListener(query: String?)
        fun onSoulfileDownloadQuery(peer: PeerApiModel, soulFile: SoulFile)
    }
}