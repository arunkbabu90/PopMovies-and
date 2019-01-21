package arunkbabu90.popmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import arunkbabu90.popmovies.R;
import arunkbabu90.popmovies.database.Favourite;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouritesAdapterViewHolder>
{
    private ItemClickListener mItemClickListener;
    private Context mContext;
    private List<Favourite> mFavouriteList;

    public FavouritesAdapter() {
        // Required Public Constructor
    }

    public void setFavouriteList(Context context, List<Favourite> favouriteList) {
        mContext = context;
        mFavouriteList = favouriteList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavouritesAdapter.FavouritesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_favourites, parent, false);
        return new FavouritesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesAdapter.FavouritesAdapterViewHolder holder, int position) {
        Picasso.with(mContext).load(mFavouriteList.get(position).getPosterPath()).into(holder.mPosterFav);
        holder.mTitleFav.setText(mFavouriteList.get(position).getMovieTitle());
        holder.mReleasedFav.setText(mFavouriteList.get(position).getPrefixedReleaseYear());
        holder.mRatingFav.setText(mFavouriteList.get(position).getPrefixedRating());
    }

    @Override
    public int getItemCount() {
        return mFavouriteList == null ? 0 : mFavouriteList.size();
    }

    public List<Favourite> getFavouritesList() {
        return mFavouriteList;
    }

    class FavouritesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final ImageView mPosterFav;
        private final TextView mTitleFav;
        private final TextView mRatingFav;
        private final TextView mReleasedFav;

        public FavouritesAdapterViewHolder(View itemView) {
            super(itemView);
            mPosterFav = itemView.findViewById(R.id.iv_fav_poster);
            mTitleFav = itemView.findViewById(R.id.tv_fav_title);
            mRatingFav = itemView.findViewById(R.id.tv_fav_rating);
            mReleasedFav = itemView.findViewById(R.id.tv_fav_year);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition(), mFavouriteList);
            }
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, List<Favourite> favouritesList);
    }
}
