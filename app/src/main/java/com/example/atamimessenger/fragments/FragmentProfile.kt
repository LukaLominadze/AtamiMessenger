package com.example.atamimessenger.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.atamimessenger.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.sign

class FragmentProfile : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: FirebaseDatabase
    private lateinit var signOutButton: Button
    private lateinit var profUsernameTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseDatabase
            .getInstance("https://atami-f90f1-default-rtdb.europe-west1.firebasedatabase.app")
        signOutButton = view.findViewById(R.id.signOutButton)
        profUsernameTextView = view.findViewById(R.id.profUsernameTextView)

        val dbRef = firebaseDb.reference
        dbRef.child("usernames").child(firebaseAuth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { snapshot ->
                profUsernameTextView.text = snapshot.value.toString()
            }

        signOutButton.setOnClickListener {
            firebaseAuth.signOut()
            val action = FragmentProfileDirections.actionFragmentProfileToFragmentAuth()

            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.visibility = View.GONE

            findNavController().navigate(action)
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}