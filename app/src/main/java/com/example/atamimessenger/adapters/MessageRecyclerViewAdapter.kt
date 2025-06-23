package com.example.atamimessenger.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.example.atamimessenger.R
import com.example.atamimessenger.database.Message

class MessageRecyclerViewAdapter(
    private val username: String,
    private val messageList: MutableList<Message>,
) : RecyclerView.Adapter<MessageRecyclerViewAdapter.MessageViewHolder>() {


    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val messageCardView: CardView = itemView.findViewById(R.id.messageCardView)
        val messageTextView: TextView = itemView.findViewById(R.id.messageMessage)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_message, parent, false)
        return MessageViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentItem = messageList[position]

        holder.messageTextView.text = currentItem.message

        val cardView = holder.messageCardView
        val layoutParams = cardView.layoutParams as ConstraintLayout.LayoutParams

        if (currentItem.user == username) {
            layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        } else {
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        }

        cardView.layoutParams = layoutParams
    }

    public fun add(message: Message) {
        if (messageList.contains(message)) {
            return
        }
        messageList.add(message)
        messageList.sortBy { msg -> msg.date + msg.time }
        notifyDataSetChanged()
    }

    public fun addFirst(message: Message) {
        if (messageList.contains(message)) {
            return
        }
        messageList.add(0, message)
        notifyItemInserted(0)
    }

    public fun addAll(msgs: List<Message>) {
        messageList.addAll(msgs)
        messageList.sortBy { msg -> msg.date + msg.time }
        notifyDataSetChanged()
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
