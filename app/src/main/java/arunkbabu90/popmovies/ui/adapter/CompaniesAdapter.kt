package arunkbabu90.popmovies.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.IMG_SIZE_MID
import arunkbabu90.popmovies.data.model.Company
import arunkbabu90.popmovies.getImageUrl
import arunkbabu90.popmovies.inflate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.item_company.view.*

class CompaniesAdapter(private val companyList: List<Company>)
    : RecyclerView.Adapter<CompaniesAdapter.CompanyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val v = parent.inflate(R.layout.item_company)
        return CompanyViewHolder(v)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.bind(companyList[position])
    }

    override fun getItemCount() = companyList.size

    inner class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(company: Company?) {
            val logoUrl = getImageUrl(company?.logoPath ?: "", IMG_SIZE_MID)

            Glide.with(itemView.context)
                .load(logoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_movie)
                .into(itemView.itemCompany_displayPicture)

            itemView.itemCompany_name.text = company?.name
        }
    }
}