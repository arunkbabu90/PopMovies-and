package arunkbabu90.popmovies.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.IMG_SIZE_MID
import arunkbabu90.popmovies.data.model.Favourite
import arunkbabu90.popmovies.getImageUrl
import arunkbabu90.popmovies.inflate
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_favourites.view.*

class FavouritesAdapter(private val favouriteMovies: List<Favourite>,
                        private val itemClickListener: (Favourite?) -> Unit) :
    RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>() {

    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val v = parent.inflate(R.layout.item_favourites)
        context = parent.context
        return FavouritesViewHolder(v, parent.context)
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        val movie = favouriteMovies[position]
        holder.bind(movie, itemClickListener)
    }

    override fun getItemCount(): Int = favouriteMovies.size

    inner class FavouritesViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        var movie: Favourite? = null

        fun bind(favourite: Favourite?, itemClickListener: (Favourite?) -> Unit) {
            movie = favourite

            val posterUrl = getImageUrl(favourite?.posterPath ?: "", IMG_SIZE_MID)
            Glide.with(context).load(posterUrl).into(itemView.iv_fav_poster)

            itemView.tv_fav_title.text = favourite?.title
            itemView.tv_fav_year.text = context.getString(R.string.released, favourite?.releaseDate)
            itemView.tv_fav_rating.text = context.getString(R.string.rating, favourite?.rating)

            itemView.setOnClickListener{ itemClickListener(favourite) }
        }
    }
}