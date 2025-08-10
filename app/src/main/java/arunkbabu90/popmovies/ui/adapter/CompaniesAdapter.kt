package arunkbabu90.popmovies.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.IMG_SIZE_MID
import arunkbabu90.popmovies.data.model.Company
import arunkbabu90.popmovies.databinding.ItemCompanyBinding
import arunkbabu90.popmovies.getImageUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class CompaniesAdapter(private val companyList: List<Company>)
    : RecyclerView.Adapter<CompaniesAdapter.CompanyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val binding = ItemCompanyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompanyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.bind(companyList[position])
    }

    override fun getItemCount() = companyList.size

    inner class CompanyViewHolder(private val binding: ItemCompanyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(company: Company?) {
            val logoUrl = getImageUrl(company?.logoPath ?: "", IMG_SIZE_MID)

            Glide.with(binding.root.context)
                .load(logoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_movie)
                .into(binding.itemCompanyDisplayPicture)

            binding.itemCompanyName.text = company?.name
        }
    }
}
