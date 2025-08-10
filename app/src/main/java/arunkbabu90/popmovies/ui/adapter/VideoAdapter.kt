package arunkbabu90.popmovies.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.YT_IMG_SIZE_ORIGINAL
import arunkbabu90.popmovies.data.model.Video
import arunkbabu90.popmovies.databinding.ItemVideoBinding
import arunkbabu90.popmovies.getYouTubeThumbUrl
import arunkbabu90.popmovies.getYouTubeVideoUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class VideoAdapter(private val videoList: List<Video>,
                   private val itemClickListener: (String) -> Unit,
                   private val itemLongClickListener: (String) -> Unit)
    : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount() = videoList.size

    inner class VideoViewHolder(private val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(video: Video?) {
            val thumbUrl = getYouTubeThumbUrl(video?.videoId ?: "", YT_IMG_SIZE_ORIGINAL)
            var videoUrl = ""

            Glide.with(binding.root.context)
                .load(thumbUrl)
                .placeholder(R.drawable.ic_film)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.itemVideoThumbnail)

            binding.itemVideoTitle.text = video?.title

            if (video?.site == "YouTube") {
                binding.itemVideoSiteIcon.setImageResource(R.drawable.ic_youtube)
                videoUrl = getYouTubeVideoUrl(video.videoId)
            } else {
                binding.itemVideoSiteIcon.setImageResource(R.drawable.ic_play)
            }

            binding.root.setOnClickListener { itemClickListener(videoUrl) }
            binding.root.setOnLongClickListener {
                itemLongClickListener(videoUrl)
                return@setOnLongClickListener true
            }
        }
    }
}

