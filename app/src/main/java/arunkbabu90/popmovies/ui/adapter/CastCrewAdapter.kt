package arunkbabu90.popmovies.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.IMG_SIZE_MID
import arunkbabu90.popmovies.data.model.Person
import arunkbabu90.popmovies.databinding.ItemPersonBinding
import arunkbabu90.popmovies.getImageUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class CastCrewAdapter(private val isCast: Boolean = false,
                      private val personList: ArrayList<Person>,
) : RecyclerView.Adapter<CastCrewAdapter.CastCrewViewHolder>() {
    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastCrewViewHolder {
        val binding = ItemPersonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CastCrewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CastCrewViewHolder, position: Int) {
        val person = personList[position]
        holder.bind(person)
    }

    override fun getItemCount() = personList.size

    inner class CastCrewViewHolder(private val binding: ItemPersonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(person: Person) {
            val dpUrl = getImageUrl(person.dpPath ?: "", IMG_SIZE_MID)
            Glide.with(binding.root.context) // Use binding.root.context
                .load(dpUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.default_dp)
                .into(binding.itemPersonDisplayPicture) // Access views via binding object

            if (isCast) {
                // Cast
                binding.itemPersonName.text = person.name
                binding.itemPersonDesignation.text = person.characterName
            } else {
                // Crew
                binding.itemPersonName.text = person.name
                binding.itemPersonDesignation.text = person.department
            }

            binding.root.setOnClickListener {
                itemClickListener?.onPersonClick(absoluteAdapterPosition, person)
            }
        }
    }

    interface ItemClickListener {
        fun onPersonClick(position: Int, person: Person)
    }
}
