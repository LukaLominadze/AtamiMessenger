package com.example.atamimessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.atamimessenger.R
import com.example.atamimessenger.database.SearchCard

class SearchRecyclerViewAdapter(
    private val messageList: MutableList<SearchCard>,
    private val onItemClick: (SearchCard) -> Unit
) : RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchViewHolder>() {


    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.searchUsernameTextView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_search_user, parent, false)
        return SearchViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val currentItem = messageList[position]

        holder.titleTextView.text = currentItem.username

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    public fun add(card: SearchCard) {
        messageList.add(card)
        notifyItemInserted(messageList.size - 1)
    }

    public fun updateItems(newItems: List<SearchCard>) {
        messageList.clear()
        messageList.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemCount() = messageList.size
}
