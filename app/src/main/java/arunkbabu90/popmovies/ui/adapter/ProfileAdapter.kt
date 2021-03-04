package arunkbabu90.popmovies.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.inflate
import kotlinx.android.synthetic.main.item_two_line_list.view.*

class ProfileAdapter(private val data: ArrayList<Pair<String, String>>,
                     private val itemCLickListener: (Pair<String, String>) -> Unit )
    : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val v = parent.inflate(R.layout.item_two_line_list)
        return ProfileViewHolder(v)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: Pair<String, String>) {
            val (title, subtitle) = data
            itemView.tv_twoLineList_title.text = title
            itemView.tv_twoLineList_subtitle.text = subtitle

            itemView.setOnClickListener { itemCLickListener(title to subtitle) }
        }
    }
}