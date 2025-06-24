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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class FragmentAuth : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var authEmailTextInput: TextInputLayout
    private lateinit var authPassTextInput: TextInputLayout
    private lateinit var authLoginButton: Button
    private lateinit var authRegisterButton: Button
    private lateinit var authResetButton: Button

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
        authResetButton = view.findViewById(R.id.authResetButton)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        authRegisterButton.setOnClickListener {
            val action = FragmentAuthDirections.actionFragmentAuthToFragmentRegister()
            findNavController().navigate(action)
        }

        authResetButton.setOnClickListener {
            val action = FragmentAuthDirections.actionFragmentAuthToFragmentRecovery()
            findNavController().navigate(action)
        }

        // if already logged in, go to home screen
        val user = firebaseAuth.currentUser
        if (user != null) {
            val action = FragmentAuthDirections.actionFragmentAuthToFragmentHome()
            findNavController().navigate(action)
        }

        authLoginButton.setOnClickListener {
            val email = authEmailTextInput.editText?.text.toString()
            val password = authPassTextInput.editText?.text.toString()
            // email and password validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(activity, "Email and password can't be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (!email.contains("@") || !email.contains(".")) {
                Toast.makeText(activity, "Invalid email!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // if logged in, go to home screen
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val action = FragmentAuthDirections.actionFragmentAuthToFragmentHome()

                    val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
                    bottomNav.visibility = View.VISIBLE

                    findNavController().navigate(action)
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}