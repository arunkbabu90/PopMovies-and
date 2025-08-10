package arunkbabu90.popmovies.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.IMG_SIZE_MID
import arunkbabu90.popmovies.data.model.Movie
import arunkbabu90.popmovies.data.repository.NetworkState
import arunkbabu90.popmovies.databinding.ItemMovieBinding
import arunkbabu90.popmovies.databinding.ItemNetworkStateBinding
import arunkbabu90.popmovies.getImageUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class MovieAdapter(private val itemClickListener: (Movie?, View?) -> Unit)
    : PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    val VIEW_TYPE_MOVIE = 1
    val VIEW_TYPE_NETWORK = 2

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_MOVIE) {
            val binding = ItemMovieBinding.inflate(inflater, parent, false)
            MovieViewHolder(binding)
        } else {
            val binding = ItemNetworkStateBinding.inflate(inflater, parent, false)
            NetworkStateViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_MOVIE) {
            val movie = getItem(position)
            (holder as MovieViewHolder).bind(movie, itemClickListener)
        } else {
            (holder as NetworkStateViewHolder).bind(networkState)
        }
    }

    override fun getItemCount(): Int = super.getItemCount() + if (hasExtraRow()) 1 else 0

    override fun getItemViewType(position: Int): Int
            = if (hasExtraRow() && position == itemCount - 1) VIEW_TYPE_NETWORK else VIEW_TYPE_MOVIE

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

    /**
     * ViewHolder for the movies
     */
    private inner class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie?, itemClickListener: (Movie?, View?) -> Unit) {
            val posterUrl = getImageUrl(movie?.posterPath ?: "", IMG_SIZE_MID)
            Glide.with(binding.root.context)
                .load(posterUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_img_err)
                .into(binding.ivMainPoster)

            binding.tvPosterTitle.text = movie?.title

            // Set transition name for shared element animation
            ViewCompat.setTransitionName(binding.ivMainPoster, "poster_${movie?.movieId}")

            binding.root.setOnClickListener { itemClickListener(movie, binding.ivMainPoster) }
        }
    }

    /**
     * ViewHolder for the Network State
     */
    private inner class NetworkStateViewHolder(private val binding: ItemNetworkStateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING) {
                binding.itemNetworkStateProgressBar.visibility = View.VISIBLE
            } else {
                binding.itemNetworkStateProgressBar.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                binding.itemNetworkStateErrTextView.visibility = View.VISIBLE
                binding.itemNetworkStateErrTextView.text = networkState.msg
            } else if (networkState != null && networkState == NetworkState.EOL) {
                binding.itemNetworkStateErrTextView.visibility = View.VISIBLE
                binding.itemNetworkStateErrTextView.text = networkState.msg
            } else {
                binding.itemNetworkStateErrTextView.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.CLEAR) {
                binding.itemNetworkStateErrTextView.visibility = View.VISIBLE
                binding.itemNetworkStateErrTextView.text = networkState.msg
            }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean
                = oldItem.movieId == newItem.movieId

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean
                = oldItem == newItem
    }
}