package com.example.atamimessenger.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atamimessenger.R
import com.example.atamimessenger.adapters.MessageBlockRecyclerViewAdapter
import com.example.atamimessenger.app.App
import com.example.atamimessenger.database.MessageCard
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class FragmentHome : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: FirebaseDatabase
    private lateinit var messageBlobs: MutableList<MessageCard>
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageBlockRecyclerViewAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        messageBlobs = mutableListOf()
        messageAdapter = MessageBlockRecyclerViewAdapter(messageBlobs) { message ->
            val action = FragmentHomeDirections.actionFragmentHomeToFragmentMessage(message.username)

            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.visibility = View.GONE

            findNavController().navigate(action)
        }

        recyclerView.adapter = messageAdapter

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseDatabase.
            getInstance("https://atami-f90f1-default-rtdb.europe-west1.firebasedatabase.app")

        var username = ""
        firebaseDb.reference
            .child("usernames")
            .child(firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { snapshot ->
                username = snapshot.getValue(String::class.java).toString()
                firebaseDb.reference
                    .child("chats")
                    .addChildEventListener(object: ChildEventListener {
                        override fun onChildAdded(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            val key = snapshot.key.toString()
                            if (key.contains(username)) {
                                val filtered = {
                                    when {
                                        key.startsWith("$username-") -> MessageCard(key.replaceFirst("$username-", ""))
                                        key.endsWith("-$username") -> MessageCard(key.replaceFirst("-$username", ""))
                                        else -> MessageCard(key)
                                    }
                                }()
                                messageAdapter.add(filtered)
                            }
                        }
                        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                        override fun onChildRemoved(snapshot: DataSnapshot) {}
                        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}