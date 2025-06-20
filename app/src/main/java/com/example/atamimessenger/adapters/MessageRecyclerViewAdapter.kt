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

        val constraintLayout = holder.itemView.findViewById<ConstraintLayout>(R.id.messageConstraint)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        val cardViewId = R.id.messageCardView

        constraintSet.clear(cardViewId)

        constraintSet.connect(
            cardViewId, ConstraintSet.TOP,
            ConstraintSet.PARENT_ID, ConstraintSet.TOP,
            0
        )

        constraintSet.connect(
            cardViewId, ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
            0
        )

        var constraintDir = ConstraintSet.END
        if (currentItem.user != username) {
            constraintDir = ConstraintSet.START
        }

        constraintSet.connect(
            cardViewId, constraintDir,
            ConstraintSet.PARENT_ID, constraintDir,
            8
        )

        constraintSet.constrainWidth(cardViewId, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(cardViewId, ConstraintSet.WRAP_CONTENT)

        constraintSet.applyTo(constraintLayout)
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

    public fun addAll(msgs: List<Message>) {
        messageList.addAll(msgs)
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
