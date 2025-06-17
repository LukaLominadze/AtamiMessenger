package com.example.atamimessenger.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.atamimessenger.R
import com.google.firebase.auth.FirebaseAuth

class FragmentAuth : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Email and password can't be empty!", Toast.LENGTH_SHORT).show()
//        }
//        else if (!email.contains("@") || !email.contains(".")) {
//            Toast.makeText(this, "Invalid email!", Toast.LENGTH_SHORT).show()
//        }
//        else {
//            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
//                if (it.isSuccessful) {
//                    Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show()
//                }
//                else {
//                    Toast.makeText(this, "Failed to register", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}