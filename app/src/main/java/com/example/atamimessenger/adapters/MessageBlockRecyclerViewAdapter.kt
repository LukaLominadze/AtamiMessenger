package com.example.atamimessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.atamimessenger.R
import com.example.atamimessenger.database.MessageCard

class MessageBlockRecyclerViewAdapter(
    private val messageList: List<MessageCard>,
    private val onItemClick: (MessageCard) -> Unit
) : RecyclerView.Adapter<MessageBlockRecyclerViewAdapter.MessageBlockViewHolder>() {


    class MessageBlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleTextView: TextView = itemView.findViewById(R.id.messageBlockUsername)
        val lastMessageTextView: TextView = itemView.findViewById(R.id.messageBlockLastMessage)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageBlockViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_message_block, parent, false)
        return MessageBlockViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MessageBlockViewHolder, position: Int) {
        val currentItem = messageList[position]

        holder.titleTextView.text = currentItem.username
        //holder.lastMessageTextView.text = currentItem.lastMessage
        //holder.publishYearTextView.text = currentItem.publishYear
        //holder.descriptionTextView.text = currentItem.description

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }


    override fun getItemCount() = messageList.size
}
