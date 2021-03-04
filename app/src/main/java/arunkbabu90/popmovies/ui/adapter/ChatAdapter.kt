package arunkbabu90.popmovies.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.data.model.Message
import arunkbabu90.popmovies.databinding.ItemMessageLtBinding
import arunkbabu90.popmovies.databinding.ItemMessageRtBinding
import arunkbabu90.popmovies.getLogicalDateString
import com.google.android.material.textview.MaterialTextView
import java.util.*

class ChatAdapter(private val messages: ArrayList<Message>,
                  private val userId: String,)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val msgRtBinding = ItemMessageRtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val msgLtBinding = ItemMessageLtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            Message.TYPE_YOU -> MessageViewHolderRt(parent.context, msgRtBinding)
            else -> MessageViewHolderLt(parent.context, msgLtBinding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg: Message = messages[position]
        var futureTimestamp: Long = -1
        if (position != 0) {
            // Look only up until 0th position; otherwise position - 1 will throw an OutOfBounds Exception
            futureTimestamp = messages[position - 1].msgTimestamp
        }

        if (msg.senderId == userId) {
            (holder as MessageViewHolderRt).bind(msg, futureTimestamp)
        } else {
            (holder as MessageViewHolderLt).bind(msg, futureTimestamp)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val uid: String = messages[position].senderId
        if (uid == userId)
            return Message.TYPE_YOU

        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int = messages.size

    /**
     * Visually Groups the messages by each Day in the chat
     * @param msgTs The current message Timestamp
     * @param futureTs The next message Timestamp
     * @param dtv The text view in which the date should be shown
     * @param dl The included view group item which is holding the #dtv
     * @param sysTs The current timestamp of the system or OS
     */
    private fun groupMsgByDate(msgTs: Long, futureTs: Long,
                               dtv: MaterialTextView, dl: View) {
        if (futureTs == 0L) {
            dl.visibility = View.VISIBLE
            dtv.text = getLogicalDateString(msgTs)
        } else {
            val c1: Calendar = Calendar.getInstance(TimeZone.getDefault())
            val c2: Calendar = Calendar.getInstance(TimeZone.getDefault())
            c1.timeInMillis = msgTs
            c2.timeInMillis = futureTs

            val isSameDay = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                    && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                    && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)

            if (isSameDay) {
                dl.visibility = View.GONE
                dtv.text = ""
            } else {
                dl.visibility = View.VISIBLE
                dtv.text = getLogicalDateString(msgTs)
            }
        }
    }

    inner class MessageViewHolderRt(private val context: Context,
                                    private val binding: ItemMessageRtBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message, futureTimestamp: Long) {
            binding.itemMsgRtText.text = message.msg

            groupMsgByDate(message.msgTimestamp, futureTimestamp,
                binding.itemMsgRtDateLayout.tvItemMsgDate, binding.itemMsgRtDateLayout.root)
        }
    }

    inner class MessageViewHolderLt(private val context: Context,
                                    private val binding: ItemMessageLtBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message, futureTimestamp: Long) {
            binding.itemMsgLtText.text = message.msg
            binding.itemMsgLtName.text = message.senderName

            groupMsgByDate(message.msgTimestamp, futureTimestamp,
                binding.itemMsgLtDateLayout.tvItemMsgDate, binding.itemMsgLtDateLayout.root)
        }
    }
}