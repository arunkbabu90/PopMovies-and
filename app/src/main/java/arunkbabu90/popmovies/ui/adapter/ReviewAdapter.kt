package arunkbabu90.popmovies.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.model.Review
import arunkbabu90.popmovies.data.repository.NetworkState
import arunkbabu90.popmovies.databinding.ItemNetworkStateBinding
import arunkbabu90.popmovies.databinding.ItemReviewBinding
import arunkbabu90.popmovies.inflate

class ReviewAdapter : PagedListAdapter<Review, RecyclerView.ViewHolder>(ReviewDiffCallback()) {
    private lateinit var binding: ItemReviewBinding
    private lateinit var networkBinding: ItemNetworkStateBinding

    val VIEW_TYPE_REVIEW = 1
    val VIEW_TYPE_NETWORK = 2

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemReviewBinding.inflate(inflater, parent, false)
        networkBinding = ItemNetworkStateBinding.bind(parent.inflate(R.layout.item_network_state))

        return if (viewType == VIEW_TYPE_REVIEW)
            ReviewViewHolder(binding.root)
        else
            NetworkStateViewHolder(networkBinding.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_REVIEW)
            (holder as ReviewViewHolder).bind(getItem(position))
        else
            (holder as NetworkStateViewHolder).bind(networkState)
    }

    override fun getItemViewType(position: Int)
        = if (hasExtraRow() && position == itemCount - 1) VIEW_TYPE_NETWORK else VIEW_TYPE_REVIEW

    override fun getItemCount() = super.getItemCount() + if (hasExtraRow()) 1 else 0

    private fun hasExtraRow(): Boolean = networkState != null && networkState != NetworkState.LOADED

    fun setNetworkState(networkState: NetworkState) {
        val prevState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = networkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow)
                notifyItemRemoved(super.getItemCount())
            else
                notifyItemInserted(super.getItemCount())
        } else if (hasExtraRow && prevState != networkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    private inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(review: Review?) {
            binding.tvAuthor.text = review?.author
            binding.tvContent.text = review?.content
        }
    }

    private inner class NetworkStateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING) {
                networkBinding.itemNetworkStateProgressBar.visibility = View.VISIBLE
            } else {
                networkBinding.itemNetworkStateProgressBar.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                networkBinding.itemNetworkStateErrTextView.visibility = View.VISIBLE
                networkBinding.itemNetworkStateErrTextView.text = networkState.msg
            } else if (networkState != null && networkState == NetworkState.EOL) {
                networkBinding.itemNetworkStateErrTextView.visibility = View.VISIBLE
                networkBinding.itemNetworkStateErrTextView.text = networkState.msg
            } else {
                networkBinding.itemNetworkStateErrTextView.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.CLEAR) {
                networkBinding.itemNetworkStateErrTextView.visibility = View.VISIBLE
                networkBinding.itemNetworkStateErrTextView.text = networkState.msg
            }
        }
    }

    class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }
    }
}