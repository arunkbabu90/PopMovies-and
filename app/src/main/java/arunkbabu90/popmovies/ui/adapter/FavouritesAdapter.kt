package arunkbabu90.popmovies.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.IMG_SIZE_MID
import arunkbabu90.popmovies.data.model.Favourite
import arunkbabu90.popmovies.databinding.ItemFavouritesBinding
import arunkbabu90.popmovies.getImageUrl
import com.bumptech.glide.Glide

class FavouritesAdapter(private val favouriteMovies: List<Favourite>,
                        private val itemClickListener: (Favourite?) -> Unit) :
    RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val binding = ItemFavouritesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        val movie = favouriteMovies[position]
        holder.bind(movie, itemClickListener)
    }

    override fun getItemCount(): Int = favouriteMovies.size

    inner class FavouritesViewHolder(private val binding: ItemFavouritesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favourite: Favourite?, itemClickListener: (Favourite?) -> Unit) {
            val posterUrl = getImageUrl(favourite?.posterPath ?: "", IMG_SIZE_MID)
            Glide.with(binding.root.context).load(posterUrl).into(binding.ivFavPoster)

            binding.tvFavTitle.text = favourite?.title
            binding.tvFavYear.text = binding.root.context.getString(R.string.released, favourite?.releaseDate)
            binding.tvFavRating.text = binding.root.context.getString(R.string.rating, favourite?.rating)

            binding.root.setOnClickListener{ itemClickListener(favourite) }
        }
    }
}
