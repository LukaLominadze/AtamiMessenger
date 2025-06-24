package com.example.atamimessenger.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.atamimessenger.R
import com.example.atamimessenger.app.App
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FragmentRegister : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebase: FirebaseDatabase

    private lateinit var regUsernameTextInput: TextInputLayout
    private lateinit var regEmailTextInput: TextInputLayout
    private lateinit var regPassTextInput: TextInputLayout
    private lateinit var regRegisterButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        regUsernameTextInput = view.findViewById(R.id.regUsernameTextInput)
        regEmailTextInput = view.findViewById(R.id.regEmailTextInput)
        regPassTextInput = view.findViewById(R.id.regPassTextInput)
        regRegisterButton = view.findViewById(R.id.regRegisterButton)

        firebaseAuth = FirebaseAuth.getInstance()
        firebase = FirebaseDatabase
            .getInstance("https://atami-f90f1-default-rtdb.europe-west1.firebasedatabase.app")

        // if pressed back button, go back to authentication
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val action = FragmentRegisterDirections.actionFragmentRegisterToFragmentAuth()
                findNavController().navigate(action)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

        val dbRef = firebase.reference


        regRegisterButton.setOnClickListener {
            var usernameInp = regUsernameTextInput.editText?.text.toString()
            val emailInp = regEmailTextInput.editText?.text.toString()
            val passwordInp = regPassTextInput.editText?.text.toString()
            // check if user exists
            dbRef.child("usernames-public").child(usernameInp).get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        App.instance.showCustomToast(activity, "User exists", Toast.LENGTH_SHORT)
                        usernameInp = ""
                    }
                    else {
                        App.instance.showCustomToast(activity, "User available", Toast.LENGTH_SHORT)
                    }
                }
                .addOnFailureListener {
                    App.instance.showCustomToast(activity, "Something went wrong", Toast.LENGTH_SHORT)
                    usernameInp = ""
                }
            // if any field is empty, throw
            if (usernameInp.isEmpty()) {
                return@setOnClickListener
            }
            if (emailInp.isEmpty() || passwordInp.isEmpty()) {
                App.instance.showCustomToast(activity, "Email and password can't be empty!", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }
            else if (!emailInp.contains("@") || !emailInp.contains(".")) {
                App.instance.showCustomToast(activity, "Invalid email!", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }
            else {
                // attempt to create user
                firebaseAuth.createUserWithEmailAndPassword(emailInp, passwordInp).addOnCompleteListener {
                    if (it.isSuccessful) {
                        App.instance.showCustomToast(activity, "Added successfully", Toast.LENGTH_SHORT)
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        // register username in database
                        dbRef.child("usernames-public").child(usernameInp.lowercase()).setValue(usernameInp)
                            .addOnSuccessListener {
                                App.instance.showCustomToast(activity, "Added to public", Toast.LENGTH_SHORT)
                            }
                            .addOnFailureListener {
                                App.instance.showCustomToast(activity, "Failed to add to public", Toast.LENGTH_SHORT)
                            }
                        dbRef.child("usernames").child(userId.toString()).setValue(usernameInp)
                            .addOnSuccessListener {
                                App.instance.showCustomToast(activity, "Added to private", Toast.LENGTH_SHORT)
                                firebaseAuth.signOut()
                                val action = FragmentRegisterDirections.actionFragmentRegisterToFragmentAuth()
                                findNavController().navigate(action)
                            }
                            .addOnFailureListener {
                                App.instance.showCustomToast(activity, "Failed to add to private", Toast.LENGTH_SHORT)
                            }

                    }
                    else {
                        App.instance.showCustomToast(activity, "Failed to register", Toast.LENGTH_SHORT)
                    }
                }
            }
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}