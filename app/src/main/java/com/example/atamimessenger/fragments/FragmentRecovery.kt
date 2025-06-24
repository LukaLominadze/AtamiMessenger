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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class FragmentRecovery : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var recEmailTextInput: TextInputLayout
    private lateinit var recResetButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recovery, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        recEmailTextInput = view.findViewById(R.id.recEmailTextInput)
        recResetButton = view.findViewById(R.id.recResetButton)


        // when pressing the back button, go back to authentication
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val action = FragmentRecoveryDirections.actionFragmentRecoveryToFragmentAuth()
                findNavController().navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        // send request for resetting account
        recResetButton.setOnClickListener {
            val email = recEmailTextInput.editText?.text.toString()
            if (email.isEmpty()) {
                App.instance.showCustomToast(activity, "Email can't be empty", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    App.instance.showCustomToast(activity, "Sent recovery link to email", Toast.LENGTH_SHORT)
                    val action = FragmentRecoveryDirections.actionFragmentRecoveryToFragmentAuth()
                    findNavController().navigate(action)
                }
                .addOnFailureListener {
                    App.instance.showCustomToast(activity, "Email is invalid", Toast.LENGTH_SHORT)
                }
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}