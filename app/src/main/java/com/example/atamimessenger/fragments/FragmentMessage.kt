package com.example.atamimessenger.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atamimessenger.R
import com.example.atamimessenger.adapters.MessageBlockRecyclerViewAdapter
import com.example.atamimessenger.adapters.MessageRecyclerViewAdapter
import com.example.atamimessenger.app.App
import com.example.atamimessenger.database.FirebaseMessage
import com.example.atamimessenger.database.Message
import com.example.atamimessenger.database.MessageCard
import com.example.atamimessenger.database.MessageRoom
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.android.play.integrity.internal.ac
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FragmentMessage : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: FirebaseDatabase
    private lateinit var messageTextInput: TextInputLayout
    private lateinit var messageSendButton: Button
    private lateinit var users: MutableList<String>
    private var otherUser: String? = null
    private var canSendMsg = false
    private var loadMsgs = true
    private var username = ""
    private var dateOffset = 1
    private lateinit var dates: List<String>

    private lateinit var messages: MutableList<Message>
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageRecyclerViewAdapter

    private lateinit var messageListener: ChildEventListener;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            otherUser = FragmentMessageArgs.fromBundle(it).username
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)

        view.findViewById<TextView>(R.id.messagePageUsername).text = otherUser

        view.findViewById<Button>(R.id.messageBackButton).setOnClickListener {
            val action = FragmentMessageDirections.actionFragmentMessageToFragmentHome()

            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.visibility = View.VISIBLE

            findNavController().navigate(action)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val action = FragmentMessageDirections.actionFragmentMessageToFragmentHome()

                val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
                bottomNav.visibility = View.VISIBLE

                findNavController().navigate(action)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseDatabase
            .getInstance("https://atami-f90f1-default-rtdb.europe-west1.firebasedatabase.app")

        messageListener = object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(FirebaseMessage::class.java)
                val currentdate = snapshot.ref.parent?.key.toString()
                val time = snapshot.key.toString().split("-")[0]
                val msg = Message(currentdate, time, message?.user.toString(), message?.message.toString())
                messageAdapter.add(msg)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val lastItemPosition = messageAdapter.itemCount - 1

                lifecycleScope.launch {
                    val other = if (users[0] == username) users[1] else users[0]
                    val exists = App.instance.db.getMessageDao()
                        .messageExists(currentdate, time, username, otherUser.toString(), msg.message)

                    if (exists == 0) {
                        App.instance.db.getMessageDao().insertMessage(
                            MessageRoom(0, currentdate, time, username, otherUser.toString(), msg.message)
                        )
                    }
                }

                if (lastVisiblePosition == lastItemPosition - 1 ||
                    msg.user == username) {
                    // If last visible item was previously last message,
                    // now new message added, scroll down to new last message
                    recyclerView.scrollToPosition(lastItemPosition)
                }
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        val dbRef = firebaseDb.reference
        dbRef.child("usernames").child(firebaseAuth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { snapshot ->
                username = snapshot.getValue(String::class.java).toString()
                users = mutableListOf<String>()
                users.add(username)
                users.add(otherUser.toString())
                users.sort()

                val mySlot = if (username == users[0]) "0" else "1"

                val chatRef = dbRef
                    .child("chats")
                    .child("${users.get(0)}-${users.get(1)}")
                    .child("trusted-users")
                    .child(mySlot)
                chatRef.get()
                    .addOnSuccessListener { snapshot ->
                        if (!snapshot.exists()) {
                            chatRef.setValue(firebaseAuth.currentUser?.uid.toString())
                        }
                        canSendMsg = true
                        setupAdapterView(view)
                        setDates()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(activity, "Error on trusted users ${e.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Couldn't find username ${e.message.toString()}", Toast.LENGTH_SHORT).show()
            }

        messageTextInput = view.findViewById(R.id.messageTextInput)
        messageSendButton = view.findViewById(R.id.messageSendButton)

        messageSendButton.setOnClickListener {
            var msg = messageTextInput.editText?.text.toString()
            if (msg.isEmpty()) {
                return@setOnClickListener
            }
            if (!canSendMsg) {
                return@setOnClickListener
            }

            messageTextInput.editText?.text?.clear()

            val date = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val time = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("HHmmss"))

            val messageData = mapOf(
                "user" to username,
                "message" to msg
            )

            val dbRef = firebaseDb.reference
                .child("chats")
                .child("${users[0]}-${users[1]}")
                .child("messages")
                .child(date)
                .child("${time}-${username}")

            dbRef.updateChildren(messageData)
                .addOnSuccessListener {

                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "Something went wrong ${e.message.toString()}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupAdapterView(view: View) {
        recyclerView = view.findViewById(R.id.messageRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        messages = mutableListOf()
        messageAdapter = MessageRecyclerViewAdapter(username, messages)

        recyclerView.adapter = messageAdapter

        firebaseDb.reference
            .child("chats")
            .child("${users.get(0)}-${users.get(1)}")
            .child("messages")
            .get()
            .addOnSuccessListener { snapshot ->
                val dates = snapshot.children.mapNotNull { it.key }
                lifecycleScope.launch {
                    for (date in dates.asReversed()) {
                        val currentdate = date
                        val msgs = App.instance.db.getMessageDao().getMessagesOfChatWithDate(
                            currentdate, username, otherUser.toString()
                        )
                        if (msgs.isEmpty()) {
                            break
                        }
                        if (messageAdapter.itemCount > 20) {
                            break
                        }
                        for (msg in msgs) {
                            val conv = Message(msg.date, msg.time, msg.sender, msg.message)
                            messageAdapter.add(conv)
                        }
                        val lastItemPosition = messageAdapter.itemCount - 1
                        if (lastItemPosition >= 0) {
                            recyclerView.scrollToPosition(lastItemPosition)
                        }
                    }
                }
            }

        val date = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        val chatRef = firebaseDb.reference
            .child("chats")
            .child("${users[0]}-${users[1]}")
            .child("messages")
        chatRef
            .child(date)
            .addChildEventListener(messageListener)

        recyclerView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (!loadMsgs) {
                        return@setOnTouchListener false
                    }

                    val isAtTop = !recyclerView.canScrollVertically(-1)

                    if (isAtTop && loadMsgs) {

                        loadMsgs = false
                        dateOffset += 1
                        if (dateOffset > dates.size) {
                            return@setOnTouchListener false
                        }
                        val date = dates[dates.size - dateOffset]
                        var isCached = false
                        lifecycleScope.launch {
                            val msgs = App.instance.db.getMessageDao().getMessagesOfChatWithDate(
                                date, username, otherUser.toString()
                            )
                            if (msgs.isEmpty()) {
                                return@launch
                            }
                            for (msg in msgs) {
                                messageAdapter.add(Message(
                                    date, msg.time, msg.sender, msg.message
                                ))
                            }
                            isCached = true
                        }
                        if (isCached) {
                            return@setOnTouchListener false
                        }
                        chatRef
                            .child(date)
                            .get()
                            .addOnSuccessListener { snapshot ->
                                if (!snapshot.exists()) {
                                    loadMsgs = true
                                    return@addOnSuccessListener
                                }
                                val msgs: List<Message> = snapshot.children.mapNotNull { childSnapshot ->
                                    val timestamp = childSnapshot.key ?: return@mapNotNull null
                                    val fbMsg = childSnapshot.getValue(FirebaseMessage::class.java) ?: return@mapNotNull null
                                    Message(date, timestamp, fbMsg.user ?: "", fbMsg.message ?: "")
                                }
                                for (msg: Message in msgs.asReversed()) {
                                    messageAdapter.addFirst(msg)
                                }
                                lifecycleScope.launch {
                                    for (msg: Message in msgs.asReversed()) {
                                        val exists = App.instance.db.getMessageDao()
                                            val other = if (msg.user == username) otherUser.toString() else username
                                            if (App.instance.db.getMessageDao()
                                                .messageExists(date, msg.time, msg.user, other, msg.message) == 0) {
                                                App.instance.db.getMessageDao().insertMessage(
                                                    MessageRoom(0, date, msg.time, msg.user, other, msg.message)
                                                )
                                            }
                                    }
                                }
                                loadMsgs = true
                            }
                    }
                }
            }
            false
        }

        // When a new day starts, reconfigure events

        val nowUtc = ZonedDateTime.now(ZoneOffset.UTC)
        val nextUtcMidnight = nowUtc.toLocalDate().plusDays(1).atStartOfDay(ZoneOffset.UTC)
        val millisUntilMidnight = Duration.between(nowUtc, nextUtcMidnight).toMillis()

        Handler(Looper.getMainLooper()).postDelayed({
            onNewDayStarted()
        }, millisUntilMidnight)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDates() {
        val chatRef = firebaseDb.reference
            .child("chats")
            .child("${users.get(0)}-${users.get(1)}")
            .child("messages")
        chatRef
            .get()
            .addOnSuccessListener { snapshot ->
                val keys = snapshot.children.mapNotNull { it.key }
                val currentDate = ZonedDateTime
                    .now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                if (keys.isNotEmpty() && keys.last() != currentDate) {
                    chatRef
                        .child(keys.last())
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val msgs = snapshot.children.mapNotNull { childSnapshot ->
                                val timestamp = childSnapshot.key ?: return@mapNotNull null
                                val fbMsg = childSnapshot.getValue(FirebaseMessage::class.java) ?: return@mapNotNull null
                                Message(keys.last(), timestamp, fbMsg.user ?: "", fbMsg.message ?: "")}
                            for (msg: Message in msgs.asReversed()) {
                                messageAdapter.addFirst(msg)
                            }
                            val lastItemPosition = messageAdapter.itemCount - 1
                            recyclerView.scrollToPosition(lastItemPosition)
                        }
                }
                dates = keys
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onNewDayStarted() {
        val date = ZonedDateTime.now(ZoneOffset.UTC)
            .minusDays(1)
            .format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        val chatRef = firebaseDb.reference
            .child("chats")
            .child("${users[0]}-${users[1]}")
            .child("messages")
        chatRef
            .child(date)
            .removeEventListener(messageListener)

        val currentdate = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        chatRef
            .child(currentdate)
            .addChildEventListener(messageListener)

        val nowUtc = ZonedDateTime.now(ZoneOffset.UTC)
        val nextUtcMidnight = nowUtc.toLocalDate().plusDays(1).atStartOfDay(ZoneOffset.UTC)
        val millisUntilMidnight = Duration.between(nowUtc, nextUtcMidnight).toMillis()

        Handler(Looper.getMainLooper()).postDelayed({
            onNewDayStarted()
        }, millisUntilMidnight)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}