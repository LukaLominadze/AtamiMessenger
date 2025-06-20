package com.example.atamimessenger.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.atamimessenger.R
import com.example.atamimessenger.database.Message

class MessageRecyclerViewAdapter(
    private val messageList: MutableList<Message>,
    private val onItemClick: (Message) -> Unit
) : RecyclerView.Adapter<MessageRecyclerViewAdapter.MessageViewHolder>() {


    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val messageTextView: TextView = itemView.findViewById(R.id.messageMessage)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_message, parent, false)
        return MessageViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentItem = messageList[position]

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    public fun add(message: Message) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    public fun addFirst(message: Message) {
        messageList.addFirst(message)
        notifyItemInserted(0)
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    public fun removeLast() {
        messageList.removeLast()
        notifyItemRemoved(messageList.size)
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    public fun removeFirst() {
        messageList.removeFirst()
        notifyItemRemoved(0)
    }

    override fun getItemCount() = messageList.size
}
