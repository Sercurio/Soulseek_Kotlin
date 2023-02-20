package fr.sercurio.saoul_seek.custom_view.recycler_view

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.sercurio.saoul_seek.inflate
import fr.sercurio.saoul_seek.models.RoomMessage
import fr.sercurio.saoul_seek.slsk_android.R

class RecyclerRoomMessageAdapter(private val roomMessages: ArrayList<RoomMessage>) :
    RecyclerView.Adapter<RecyclerRoomMessageAdapter.RoomMessageHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomMessageHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_room_message, false)
        return RoomMessageHolder(inflatedView)
    }

    override fun getItemCount() = roomMessages.size

    override fun onBindViewHolder(holder: RoomMessageHolder, position: Int) {
        val roomMessage = roomMessages[position]
        holder.bindRoomMessage(roomMessage)
    }

    class RoomMessageHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
        }

        companion object {
            private val ROOMESSAGE_KEY = "ROOMMESSAGE"
        }

        fun bindRoomMessage(roomMessage: RoomMessage) {
            view.findViewById<TextView>(R.id.usernameView).text = roomMessage.username
            view.findViewById<TextView>(R.id.messageView).text = roomMessage.message
        }
    }
}