package com.example.atamimessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.atamimessenger.R
import com.example.atamimessenger.app.App
import com.example.atamimessenger.database.MessageCard

class MessageBlockRecyclerViewAdapter(
    private val messageList: MutableList<MessageCard>,
    private val onItemClick: (MessageCard) -> Unit
) : RecyclerView.Adapter<MessageBlockRecyclerViewAdapter.MessageBlockViewHolder>() {


    class MessageBlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleTextView: TextView = itemView.findViewById(R.id.messageBlockUsername)
        val lastMessageTextView: TextView = itemView.findViewById(R.id.messageBlockLastMessage)
        val homeUserIcon: ImageView = itemView.findViewById(R.id.homeUserIcon)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageBlockViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_message_block, parent, false)
        return MessageBlockViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MessageBlockViewHolder, position: Int) {
        val currentItem = messageList[position]

        holder.titleTextView.text = currentItem.username
        holder.homeUserIcon.setBackgroundColor(App.instance.colorFromUsername(currentItem.username))
        holder.lastMessageTextView.text = currentItem.lastMessage

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    public fun add(message: MessageCard) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }

    public fun addAll(messages: List<MessageCard>) {
        messageList.addAll(messages)
        notifyDataSetChanged()
    }

    override fun getItemCount() = messageList.size
}
