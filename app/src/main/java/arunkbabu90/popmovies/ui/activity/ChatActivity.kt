package arunkbabu90.popmovies.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import arunkbabu90.popmovies.Constants
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.model.Message
import arunkbabu90.popmovies.databinding.ActivityChatBinding
import arunkbabu90.popmovies.runPullDownAnimation
import arunkbabu90.popmovies.ui.adapter.ChatAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ChatActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityChatBinding

    private lateinit var roomRoot: DatabaseReference
    private lateinit var roomQuery: Query
    private var adapter: ChatAdapter? = null

    private val messages = ArrayList<Message>()
    private var senderName = ""
    private var senderId = ""
    private var movieId = ""

    private var isFirstLaunch = true

    companion object {
        const val MOVIE_ID_EXTRA_KEY = "key_chat_movie_id_extra"
        const val MOVIE_NAME_EXTRA_KEY = "key_movie_name_extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Status bar and Navigation bar colors
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlackBackground)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorBlackBackground)

        if (!Constants.isAccountActivated) {
            // Account NOT Activated; Disable the input fields and Visually make them disabled
            binding.fabSendMessage.isEnabled = false
            binding.etTypeMessage.isEnabled = false
            binding.etTypeMessage.setHintTextColor(ContextCompat.getColor(this, R.color.colorTextDisabled))
            binding.etTypeMessage.hint = getString(R.string.err_feature_disabled_short)
        }

        val movieTitle = intent.getStringExtra(MOVIE_NAME_EXTRA_KEY) ?: ""
        movieId = intent.getStringExtra(MOVIE_ID_EXTRA_KEY) ?: ""

        senderId = Constants.userId
        senderName = Constants.userFullName

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarName.text = movieTitle

        roomRoot = Firebase.database.reference
            .child(Constants.ROOT_MOVIE_ROOMS)
            .child(movieId)
        loadMessages()

        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        lm.stackFromEnd = true
        adapter = ChatAdapter(messages, userId = senderId)
        binding.rvMessages.layoutManager = lm
        binding.rvMessages.adapter = adapter

        binding.rvMessages.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, prevBottom ->
            // Scroll to the end of list when keyboard pop
            if (bottom < prevBottom)
                binding.rvMessages.smoothScrollToPosition(messages.size)
        }

        binding.toolbarBackBtn.setOnClickListener(this)
        binding.fabSendMessage.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.toolbarBackBtn.id -> {
                // Back
                finish()
            }
            binding.fabSendMessage.id -> {
                // Send
                val message: String = binding.etTypeMessage.text.toString()
                val newMsgRoot = roomRoot.push()

                if (message.isNotBlank()) {
                    val msgMap = hashMapOf(
                        Constants.FIELD_MESSAGE to message,
                        Constants.FIELD_SENDER_ID to senderId,
                        Constants.FIELD_SENDER_NAME to senderName,
                        Constants.FIELD_MSG_TIMESTAMP to ServerValue.TIMESTAMP
                    )
                    newMsgRoot.updateChildren(msgMap)

                    binding.etTypeMessage.setText("")
                }
            }
        }
    }

    /**
     * Loads the messages
     */
    private fun loadMessages() {
        roomQuery = roomRoot.orderByChild(Constants.FIELD_MSG_TIMESTAMP)
        // Child Event Listener
        roomQuery.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                updateDataItems(snapshot)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }
            override fun onChildRemoved(snapshot: DataSnapshot) {   }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {  }
            override fun onCancelled(error: DatabaseError) {  }
        })

        // Single Value Event Listener; Guaranteed to be called when all the data is loaded
        roomQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Show error message if the chat is empty
                if (messages.size <= 0) {
                    binding.chatErrLayout.visibility = View.VISIBLE
                    binding.tvChatErr.text = getString(R.string.err_no_messages)
                    binding.ivChatErr.setImageResource(R.drawable.ic_no_message)
                } else {
                    binding.chatErrLayout.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /**
     * Loads the message from database to recycler view
     */
    private fun updateDataItems(snapshot: DataSnapshot) {
        val message = snapshot.getValue(Message::class.java) ?: Message()
        message.key = snapshot.key ?: ""
        messages.add(message)

        if (isFirstLaunch) {
            runPullDownAnimation(this, binding.rvMessages)
            isFirstLaunch = false
        }

        binding.rvMessages.smoothScrollToPosition(messages.size)
        adapter?.notifyDataSetChanged()

        // Show error message if the chat is empty
        if (messages.size <= 0) {
            binding.chatErrLayout.visibility = View.VISIBLE
            binding.tvChatErr.text = getString(R.string.err_no_messages)
            binding.ivChatErr.setImageResource(R.drawable.ic_no_message)
        } else {
            binding.chatErrLayout.visibility = View.GONE
        }
    }
}