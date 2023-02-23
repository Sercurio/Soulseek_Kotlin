package fr.sercurio.soulseek.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.sercurio.soulseek.custom_view.recycler_view.RecyclerRoomMessageAdapter
import fr.sercurio.soulseek.R
import fr.sercurio.soulseek.databinding.FragmentRoomBinding
import fr.sercurio.soulseek.entities.RoomApiModel
import fr.sercurio.soulseek.entities.RoomMessageApiModel
import fr.sercurio.soulseek.utils.AndroidUiHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RoomFragment : Fragment() {
    private var _binding: FragmentRoomBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var roomMessageAdapter: RecyclerRoomMessageAdapter
    private var roomMessageList: ArrayList<RoomMessageApiModel> = ArrayList()

    private lateinit var simpleSpinnerAdapter: SpinnerAdapter
    private var roomNamesList: ArrayList<String> = ArrayList()

    private lateinit var roomFragmentInterface: RoomFragmentInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomBinding.inflate(inflater, container, false)
        val view = binding.root

        /* RecyclerView */
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        linearLayoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager = linearLayoutManager
        roomMessageAdapter = RecyclerRoomMessageAdapter(roomMessageList)
        recyclerView.adapter = roomMessageAdapter

        viewLifecycleOwner.lifecycleScope.launch {

        }

        /* Spinner */
        val roomSpinner = view.findViewById<Spinner>(R.id.roomSpinner)
        simpleSpinnerAdapter =
            ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, roomNamesList).apply {
                setNotifyOnChange(true)
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        roomSpinner.adapter = simpleSpinnerAdapter

        roomSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                CoroutineScope(Dispatchers.IO).launch {
                    val roomName = parent?.getItemAtPosition(position).toString()
                    if (roomName !== "") roomFragmentInterface.onRoomSpinnerItemSelected(roomName)
                }
            }
        }

        val sendButton = view.findViewById<Button>(R.id.sendButton)
        sendButton.setOnClickListener {
            AndroidUiHelper.hideKeyboard(activity as Activity)
            CoroutineScope(Dispatchers.IO).launch {
                roomFragmentInterface.onRoomMessageSend(
                    RoomMessageApiModel(
                        roomSpinner.selectedItem.toString(), "ME", binding.messageEdit.text.toString()
                    )
                )
            }
            binding.messageEdit.setText("")
        }

        return view
    }

    fun setRoomList(roomList: ArrayList<RoomApiModel>) {
        for (room in roomList) {
            roomNamesList.add(room.name)
        }
        binding.roomSpinner.adapter = simpleSpinnerAdapter
    }

    fun addRoomMessage(roomMessage: RoomMessageApiModel) {
        roomMessageList.add(roomMessage)
        roomMessageAdapter.notifyItemChanged(roomMessageList.size)
        //recyclerView.smoothScrollToPosition(roomMessageList.size - 1)
    }

    fun setRoomFragmentInterface(callback: RoomFragmentInterface) {
        this.roomFragmentInterface = callback
    }

    interface RoomFragmentInterface {
        suspend fun onRoomSpinnerItemSelected(roomName: String)
        suspend fun onRoomMessageSend(roomMessage: RoomMessageApiModel)
    }
}