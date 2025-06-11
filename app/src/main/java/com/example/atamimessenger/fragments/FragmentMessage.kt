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
import com.example.atamimessenger.fragments.FragmentMessageDirections.Companion.actionFragmentMessageToFragmentHome
import com.google.android.material.bottomnavigation.BottomNavigationView

class FragmentMessage : Fragment() {

    private var messageText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            messageText = FragmentMessageArgs.fromBundle(it).username
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)

        view.findViewById<TextView>(R.id.messagePageUsername).text = messageText

        view.findViewById<Button>(R.id.messageBackButton).setOnClickListener {
            val action = FragmentMessageDirections.actionFragmentMessageToFragmentHome()

            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.visibility = View.VISIBLE

            findNavController().navigate(action)
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}