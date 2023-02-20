package fr.sercurio.saoul_seek.ui.fragments.child

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
import fr.sercurio.saoul_seek.custom_view.expandable_list_view.ExpandableSoulResultsAdapter
import fr.sercurio.saoul_seek.models.Peer
import fr.sercurio.saoul_seek.models.SoulFile
import fr.sercurio.saoul_seek.slsk_android.R
import fr.sercurio.saoul_seek.utils.AndroidUiHelper
import fr.sercurio.saoul_seek.utils.Bytes


class SearchChildFragment : Fragment() {
    private var searchResultList: MutableMap<Peer, List<SoulFile>> = mutableMapOf()
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
                AndroidUiHelper.hideKeyboard(parentFragment?.activity)
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
        expandableSoulResultsAdapter = ExpandableSoulResultsAdapter(this.requireContext(), searchResultList.keys.toMutableList(), searchResultList)
        expandableView.setAdapter(expandableSoulResultsAdapter)
        expandableSoulResultsAdapter.onClickDlButtonListener = { peer: Peer, soulFile: SoulFile ->
            //TODO
            Toast.makeText(context, "Not working yet", Toast.LENGTH_LONG).show()
            Log.d(tag, "\n$peer,\n$soulFile")
            searchChildInterface.onSoulfileDownloadQuery(peer, soulFile)
            //peer.socketPeer?.peerInit(peer.username, "P", Bytes.randomBytes(4))
            peer.socketPeer?.transferRequest(0, peer.token, soulFile.filename, null)
        }

        return rootView
    }


    fun addSearchResults(peer: Peer) {
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
        fun onSoulfileDownloadQuery(peer: Peer, soulFile: SoulFile)
    }
}