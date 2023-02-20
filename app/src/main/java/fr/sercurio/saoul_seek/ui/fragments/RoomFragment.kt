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
import fr.sercurio.saoul_seek.slsk_android.databinding.FragmentRoomBinding
import fr.sercurio.saoul_seek.utils.AndroidUiHelper


class RoomFragment : Fragment() {
    private var _binding: FragmentRoomBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var roomMessageAdapter: RecyclerRoomMessageAdapter
    private var roomMessageList: ArrayList<RoomMessage> = ArrayList()

    private lateinit var simpleSpinnerAdapter: SpinnerAdapter
    private var roomNamesList: ArrayList<String> = ArrayList()

    private lateinit var roomFragmentInterface: RoomFragmentInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoomBinding.inflate(inflater, container, false)
        val view = binding.root

        /* RecyclerView */
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        linearLayoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager = linearLayoutManager
        roomMessageAdapter = RecyclerRoomMessageAdapter(roomMessageList)
        recyclerView.adapter = roomMessageAdapter

        /* Spinner */
        val roomSpinner = view.findViewById<Spinner>(R.id.roomSpinner)
        simpleSpinnerAdapter =
            ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, roomNamesList).apply {
                setNotifyOnChange(true)
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        roomSpinner.adapter = simpleSpinnerAdapter
        roomSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val roomName = parent.getItemAtPosition(position).toString()
                if (roomName !== "") roomFragmentInterface.onRoomSpinnerItemSelected(roomName)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val sendButton = view.findViewById<Button>(R.id.sendButton)
        sendButton.setOnClickListener {
            AndroidUiHelper.hideKeyboard(activity)
            roomFragmentInterface.onRoomMessageSend(
                RoomMessage(
                    roomSpinner.selectedItem.toString(), "ME", binding.messageEdit.text.toString()
                )
            )
            binding.messageEdit.setText("")
        }

        return view
    }

    fun setRoomList(roomList: ArrayList<Room>) {
        for (room in roomList) {
            roomNamesList.add(room.name)
        }
        binding.roomSpinner.adapter = simpleSpinnerAdapter
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