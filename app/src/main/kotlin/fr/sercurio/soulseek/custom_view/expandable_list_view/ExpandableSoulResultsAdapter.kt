package fr.sercurio.soulseek.custom_view.expandable_list_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.TextView
import fr.sercurio.saoul_seek.slsk_android.R
import fr.sercurio.soulseek.entities.PeerApiModel
import fr.sercurio.soulseek.entities.SoulFile


class ExpandableSoulResultsAdapter(
    private val context: Context, private val listTitles: MutableList<PeerApiModel>,
    private val listItems: MutableMap<PeerApiModel, List<SoulFile>>
) : BaseExpandableListAdapter() {
    private val tag = ExpandableSoulResultsAdapter::class.java.toString()
    lateinit var onClickDlButtonListener: ((PeerApiModel, SoulFile) -> Unit)

    override fun getGroup(listPosition: Int): Any {
        return listTitles[listPosition]
    }

    override fun getGroupCount(): Int {
        return listTitles.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup
    ): View {
        var view = convertView
        val peerTitle = getGroup(listPosition) as PeerApiModel
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.expandable_list_group, null)
        }
        val peerNameView = view!!.findViewById<TextView>(R.id.username)
        val folderPathView = view.findViewById<TextView>(R.id.folderPath)
        peerNameView.text = peerTitle.username
        folderPathView.text = peerTitle.soulFiles[0]?.folder ?: "unknown"
        return view
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): SoulFile {
        return listItems[listTitles[listPosition]]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        listPosition: Int, expandedListPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        var view = convertView
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.expandable_list_item, null)
        }

        val soulFile = getChild(listPosition, expandedListPosition)

        val fileNameView = view?.findViewById<TextView>(R.id.filename)
        fileNameView?.isSelected //TODO don't works

        val sizeView = view?.findViewById<TextView>(R.id.size)
        val bitrateView = view?.findViewById<TextView>(R.id.bitrate)
        val durationView = view?.findViewById<TextView>(R.id.duration)
        val dlButton = view?.findViewById<Button>(R.id.dlButton)

        fileNameView!!.text = soulFile.filename
        sizeView!!.text = toKiloBytes(soulFile.size)
        bitrateView!!.text = context.getString(R.string.bitrate_format, soulFile.bitrate, "kbps")
        durationView!!.text = toMinutes(soulFile.duration)

        /*
        client opens a "P" type socket to the peer. This need not be a NEW socket. If a "P" type connection to the peer has already been established (for instance, because the peer has sent you a search result), it can be reused.
        client sends PeerInit "P" message immediately followed by TransferRequest message
        peer optionally sends back 12 bytes (the 5th is zero or one)
        (Note from Brian: I am a little bit worried about the random 12 bytes that sometimes get sent before a TransferResponse message. The first four bytes are obviously not a message length. Like I said in the comments, the Python code (slskproto.py:SlskProtoThread..process_peer_input()) has some wacky if-elseif-else logic in there that gets called only if it is a non "F" peer connection and it is the first message received.)
        peer sends back TransferResponse message
        client opens second socket to peer
        client sends PeerInit "F" message to peer
        peer sends file
        client closes second socket to peer

         */
        dlButton!!.setOnClickListener {
            onClickDlButtonListener.invoke(getGroup(listPosition) as PeerApiModel, soulFile)
        }

        return view!!
    }

    private fun toKiloBytes(size: Long): CharSequence? {
        val formatTemplate = "%d Ko"
        return formatTemplate.format(size / (1024))
    }

    private fun toMinutes(duration: Int): CharSequence? {
        val formatTemplate = "%.0fm%.0f"
        val minutes = duration.toFloat() / 60f
        val seconds = (minutes % 1) * 60

        return formatTemplate.format(minutes, seconds)
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return listItems[listTitles[listPosition]]!!.size
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

    fun addHeaderAndItems(peer: PeerApiModel) {
        listTitles.add(peer)
        listItems[peer] = peer.soulFiles
        this.notifyDataSetChanged()
    }

    fun clearHeaderAndItems() {
        listTitles.clear()
        listItems.clear()
        this.notifyDataSetChanged()
    }
}



