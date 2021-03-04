package arunkbabu90.popmovies.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.IMG_SIZE_MID
import arunkbabu90.popmovies.data.model.Movie
import arunkbabu90.popmovies.data.repository.NetworkState
import arunkbabu90.popmovies.getImageUrl
import arunkbabu90.popmovies.inflate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.item_movie.view.*
import kotlinx.android.synthetic.main.item_network_state.view.*

class MovieAdapter(private val itemClickListener: (Movie?) -> Unit)
    : PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    val VIEW_TYPE_MOVIE = 1
    val VIEW_TYPE_NETWORK = 2

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MOVIE)
            MovieViewHolder(parent.inflate(R.layout.item_movie))
        else
            NetworkStateViewHolder(parent.inflate(R.layout.item_network_state))
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
    private inner class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(movie: Movie?, itemClickListener: (Movie?) -> Unit) {

            val posterUrl = getImageUrl(movie?.posterPath ?: "", IMG_SIZE_MID)
            Glide.with(itemView.context)
                .load(posterUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_img_err)
                .into(itemView.iv_main_poster)

            itemView.tv_poster_title.text = movie?.title

            itemView.setOnClickListener { itemClickListener(movie) }
        }
    }

    /**
     * ViewHolder for the Network State
     */
    private inner class NetworkStateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING) {
                itemView.item_network_state_progress_bar.visibility = View.VISIBLE
            } else {
                itemView.item_network_state_progress_bar.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                itemView.item_network_state_err_text_view.visibility = View.VISIBLE
                itemView.item_network_state_err_text_view.text = networkState.msg
            } else if (networkState != null && networkState == NetworkState.EOL) {
                itemView.item_network_state_err_text_view.visibility = View.VISIBLE
                itemView.item_network_state_err_text_view.text = networkState.msg
            } else {
                itemView.item_network_state_err_text_view.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.CLEAR) {
                itemView.item_network_state_err_text_view.visibility = View.VISIBLE
                itemView.item_network_state_err_text_view.text = networkState.msg
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