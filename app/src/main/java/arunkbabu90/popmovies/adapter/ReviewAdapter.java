package arunkbabu90.popmovies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import arunkbabu90.popmovies.R;
import arunkbabu90.popmovies.Reviews;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>
{
    private List<Reviews> mReviewList;

    public ReviewAdapter() {
        // Required public constructor
    }

    public void setReviews(List<Reviews> reviewList) {
        mReviewList = reviewList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_review, parent, false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        holder.mAuthorView.setText(mReviewList.get(position).getAuthor());
        holder.mContentView.setText(mReviewList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewList == null ? 0 : mReviewList.size();
    }

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView mAuthorView;
        private final TextView mContentView;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mAuthorView = itemView.findViewById(R.id.tv_author);
            mContentView = itemView.findViewById(R.id.tv_content);
        }
    }
}
