package arunkbabu90.popmovies.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.IMG_SIZE_MID
import arunkbabu90.popmovies.data.model.Person
import arunkbabu90.popmovies.getImageUrl
import arunkbabu90.popmovies.inflate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.item_person.view.*

class CastCrewAdapter(private val isCast: Boolean = false,
                      private val personList: ArrayList<Person?>,
) : RecyclerView.Adapter<CastCrewAdapter.CastCrewViewHolder>() {
    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastCrewViewHolder {
        val view = parent.inflate(R.layout.item_person)
        return CastCrewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastCrewViewHolder, position: Int) {
        val person = personList[position]

        if (person != null)
            holder.bind(person)
    }

    override fun getItemCount() = personList.size

    inner class CastCrewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(person: Person) {
            val dpUrl = getImageUrl(person.dpPath ?: "", IMG_SIZE_MID)
            Glide.with(itemView.context)
                .load(dpUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.default_dp)
                .into(itemView.itemPerson_displayPicture)

            if (isCast) {
                // Cast
                itemView.itemPerson_name.text = person.name
                itemView.itemPerson_designation.text = person.characterName
            } else {
                // Crew
                itemView.itemPerson_name.text = person.name
                itemView.itemPerson_designation.text = person.department
            }

            itemView.setOnClickListener {
                itemClickListener?.onPersonClick(absoluteAdapterPosition, person)
            }
        }
    }

    interface ItemClickListener {
        fun onPersonClick(position: Int, person: Person)
    }
}