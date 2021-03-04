package arunkbabu90.popmovies.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.YT_IMG_SIZE_ORIGINAL
import arunkbabu90.popmovies.data.model.Video
import arunkbabu90.popmovies.getYouTubeThumbUrl
import arunkbabu90.popmovies.getYouTubeVideoUrl
import arunkbabu90.popmovies.inflate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.item_video.view.*

class VideoAdapter(private val videoList: List<Video>,
                   private val itemClickListener: (String) -> Unit,
                   private val itemLongClickListener: (String) -> Unit)
    : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = parent.inflate(R.layout.item_video)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount() = videoList.size

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(video: Video?) {
            val thumbUrl = getYouTubeThumbUrl(video?.videoId ?: "", YT_IMG_SIZE_ORIGINAL)
            var videoUrl = ""

            Glide.with(itemView.context)
                .load(thumbUrl)
                .placeholder(R.drawable.ic_film)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.itemVideo_thumbnail)

            itemView.itemVideo_title.text = video?.title

            if (video?.site == "YouTube") {
                itemView.itemVideo_siteIcon.setImageResource(R.drawable.ic_youtube)
                videoUrl = getYouTubeVideoUrl(video.videoId)
            } else {
                itemView.itemVideo_siteIcon.setImageResource(R.drawable.ic_play)
            }

            itemView.setOnClickListener { itemClickListener(videoUrl) }
            itemView.setOnLongClickListener {
                itemLongClickListener(videoUrl)
                return@setOnLongClickListener true
            }
        }
    }
}