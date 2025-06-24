package com.example.atamimessenger.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atamimessenger.R
import com.example.atamimessenger.adapters.SearchRecyclerViewAdapter
import com.example.atamimessenger.app.App
import com.example.atamimessenger.database.SearchCard
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase

class FragmentAdd : Fragment() {

    private lateinit var searchCards: MutableList<SearchCard>
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: SearchRecyclerViewAdapter
    private lateinit var firebaseDb: FirebaseDatabase
    private lateinit var searchTextInput: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        recyclerView = view.findViewById(R.id.searchRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        searchCards = mutableListOf()
        searchAdapter = SearchRecyclerViewAdapter(searchCards) { user ->
            val action = FragmentAddDirections.actionFragmentAddToFragmentMessage(user.username)

            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.visibility = View.GONE

            // clear bottom navigation cache
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.fragmentAdd, true)
                .build()

            findNavController().navigate(action, navOptions)
        }
        recyclerView.adapter = searchAdapter

        firebaseDb = FirebaseDatabase
            .getInstance("https://atami-f90f1-default-rtdb.europe-west1.firebasedatabase.app")

        searchTextInput = view.findViewById(R.id.searchTextInput)
        searchTextInput.editText?.doOnTextChanged { text, v0, v1, v2 ->
            if (text.toString().isEmpty()) {
                searchAdapter.updateItems(mutableListOf())
            }
            else {
                val dbRef = firebaseDb.reference
                // search for users in the database
                dbRef.child("usernames-public").get()
                    .addOnSuccessListener { snapshot ->
                        val users = mutableListOf<SearchCard>()
                        for (userSnapshot in snapshot.children) {
                            val username = userSnapshot.getValue(String::class.java).toString()
                            if (username.lowercase().contains(text.toString().lowercase())) {
                                users.add(SearchCard(username))
                            }
                        }
                        searchAdapter.updateItems(users)
                    }
                    .addOnFailureListener { e ->
                        App.instance.showCustomToast(activity, "Couldn't get values ${e.message}", Toast.LENGTH_LONG)
                    }
            }
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}