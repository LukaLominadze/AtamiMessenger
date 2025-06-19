package com.example.atamimessenger.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.atamimessenger.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class FragmentAuth : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var authEmailTextInput: TextInputLayout
    private lateinit var authPassTextInput: TextInputLayout
    private lateinit var authLoginButton: Button
    private lateinit var authRegisterButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        authEmailTextInput = view.findViewById(R.id.authEmailTextInput)
        authPassTextInput = view.findViewById(R.id.authPassTextInput)
        authLoginButton = view.findViewById(R.id.authLoginButton)
        authRegisterButton = view.findViewById(R.id.authRegisterButton)

        authRegisterButton.setOnClickListener {
            val action = FragmentAuthDirections.actionFragmentAuthToFragmentRegister()
            findNavController().navigate(action)
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}