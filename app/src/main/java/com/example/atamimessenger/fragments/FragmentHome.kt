package com.example.atamimessenger.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atamimessenger.R
import com.example.atamimessenger.adapters.MessageRecyclerViewAdapter
import com.example.atamimessenger.database.MessageCard
import com.google.android.material.bottomnavigation.BottomNavigationView

class FragmentHome : Fragment() {

    private lateinit var messageBlobs: MutableList<MessageCard>
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        messageBlobs = mutableListOf()
        messageBlobs.add(MessageCard("Me"))
        messageAdapter = MessageRecyclerViewAdapter(messageBlobs) { message ->
            val action = FragmentHomeDirections.actionFragmentHomeToFragmentMessage(message.username)

            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.visibility = View.GONE

            findNavController().navigate(action)
        }

        recyclerView.adapter = messageAdapter

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}