package arunkbabu90.popmovies.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.databinding.ItemTwoLineListBinding

class ProfileAdapter(private val data: ArrayList<Pair<String, String>>,
                     private val itemCLickListener: (Pair<String, String>) -> Unit )
    : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ItemTwoLineListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    inner class ProfileViewHolder(private val binding: ItemTwoLineListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Pair<String, String>) {
            val (title, subtitle) = data
            binding.tvTwoLineListTitle.text = title
            binding.tvTwoLineListSubtitle.text = subtitle

            binding.root.setOnClickListener { itemCLickListener(title to subtitle) }
        }
    }
}
