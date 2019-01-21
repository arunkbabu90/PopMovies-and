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
import arunkbabu90.popmovies.database.MoviePopular;
import arunkbabu90.popmovies.database.MovieTopRated;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder>
{
    private ItemClickListener mItemClickListener;
    private List<MoviePopular> mMoviesList;
    private List<MovieTopRated> mTopRatedList;
    private Context mContext;
    private boolean isPopular = false;

    // Empty Constructor used for initialization
    public MoviesAdapter() {}

    public void setPopularMoviesList(Context context, List<MoviePopular> moviesList) {
        mMoviesList = moviesList;
        mContext = context;
        notifyDataSetChanged();
        isPopular = true;
    }

    public void setTopRatedMoviesList(Context context, List<MovieTopRated> moviesList) {
        mTopRatedList = moviesList;
        mContext = context;
        notifyDataSetChanged();
        isPopular = false;
    }

    @NonNull
    @Override
    public MoviesAdapter.MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_main_fragment, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.MoviesAdapterViewHolder holder, int position) {
        if (isPopular) {
            Picasso.with(mContext)
                    .load(mMoviesList.get(position).getPosterUrl())
                    .error(R.drawable.ic_img_err)
                    .into(holder.mPosterView);
            holder.mPosterTitle.setText(mMoviesList.get(position).getMovieTitle());
        } else {
            Picasso.with(mContext)
                    .load(mTopRatedList.get(position).getPosterUrl())
                    .error(R.drawable.ic_img_err)
                    .into(holder.mPosterView);
            holder.mPosterTitle.setText(mTopRatedList.get(position).getMovieTitle());
        }
    }

    @Override
    public int getItemCount() {
        int size;
        if (isPopular) {
            size = mMoviesList == null ? 0 : mMoviesList.size();
        } else {
            size = mTopRatedList == null ? 0 : mTopRatedList.size();
        }
        return size;
    }



    /**
     * Inner class for creating the ViewHolder for the Recycler View
     */
    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final ImageView mPosterView;
        private final TextView mPosterTitle;

        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            mPosterView = itemView.findViewById(R.id.iv_main_poster);
            mPosterTitle = itemView.findViewById(R.id.tv_poster_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }


    /*
     * Recycler view click event code adapted from
     * https://stackoverflow.com/questions/40587168/simple-android-grid-example-using-recyclerview-with-gridlayoutmanager-like-the
     */
    public void setClickListener(ItemClickListener clickListener) {
        this.mItemClickListener = clickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
