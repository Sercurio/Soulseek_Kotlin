package fr.sercurio.saoul_seek.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.sercurio.saoul_seek.custom_view.recycler_view.RecyclerRoomMessageAdapter
import fr.sercurio.saoul_seek.models.Room
import fr.sercurio.saoul_seek.models.RoomMessage
import fr.sercurio.saoul_seek.slsk_android.R
import fr.sercurio.saoul_seek.utils.AndroidUiHelper
import kotlinx.android.synthetic.main.fragment_room.*


class RoomFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var roomMessageAdapter: RecyclerRoomMessageAdapter
    private var roomMessageList: ArrayList<RoomMessage> = ArrayList()

    private lateinit var simpleSpinnerAdapter: SpinnerAdapter
    private var roomNamesList: ArrayList<String> = ArrayList()

    private lateinit var roomFragmentInterface: RoomFragmentInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_room, container, false)
        /*
        rootView.setOnTouchListener { view, ev ->
            view.performClick()
            hideKeyboard(context as Activity?)
            false
        }
         fonctionne pas */

        /* RecyclerView */
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        linearLayoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager = linearLayoutManager
        roomMessageAdapter = RecyclerRoomMessageAdapter(roomMessageList)
        recyclerView.adapter = roomMessageAdapter

        /* Spinner */
        val roomSpinner = rootView.findViewById<Spinner>(R.id.roomSpinner)
        simpleSpinnerAdapter = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, roomNamesList).apply {
            setNotifyOnChange(true)
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        roomSpinner.adapter = simpleSpinnerAdapter
        roomSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val roomName = parent.getItemAtPosition(position).toString()
                if (roomName !== "")
                    roomFragmentInterface.onRoomSpinnerItemSelected(roomName)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val sendButton = rootView.findViewById<Button>(R.id.sendButton)
        sendButton.setOnClickListener {
            AndroidUiHelper.hideKeyboard(activity)
            roomFragmentInterface.onRoomMessageSend(RoomMessage(roomSpinner.selectedItem.toString(), "ME", messageEdit.text.toString()))
            messageEdit.setText("")
        }

        return rootView
    }

    fun setRoomList(roomList: ArrayList<Room>) {
        for (room in roomList) {
            roomNamesList.add(room.name)
        }
        roomSpinner.adapter = simpleSpinnerAdapter
    }

    fun addRoomMessage(roomMessage: RoomMessage) {
        roomMessageList.add(roomMessage)
        roomMessageAdapter.notifyItemChanged(roomMessageList.size)
        //recyclerView.smoothScrollToPosition(roomMessageList.size - 1)
    }

    fun setRoomFragmentInterface(callback: RoomFragmentInterface) {
        this.roomFragmentInterface = callback
    }

    interface RoomFragmentInterface {
        fun onRoomSpinnerItemSelected(roomName: String)
        fun onRoomMessageSend(roomMessage: RoomMessage)
    }
}