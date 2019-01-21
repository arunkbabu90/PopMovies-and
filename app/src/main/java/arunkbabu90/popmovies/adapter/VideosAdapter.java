package arunkbabu90.popmovies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import arunkbabu90.popmovies.R;
import arunkbabu90.popmovies.Videos;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder>
{
    private List<Videos> mVideoList;
    private ItemClickListener mClickListener;

    // Empty constructor used for initialization
    public VideosAdapter() { }

    public void setVideoList(List<Videos> videoList) {
        mVideoList = videoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_video_list, parent, false);
        return new VideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosAdapterViewHolder holder, int position) {
        holder.mVideoTitle.setText(mVideoList.get(position).getTrailerName());
        holder.mVideoType.setText(mVideoList.get(position).getType());
    }

    @Override
    public int getItemCount() {
        return mVideoList == null ? 0 : mVideoList.size();
    }

    class VideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView mVideoTitle;
        private final TextView mVideoType;
        private final ImageButton mShareButton;

        public VideosAdapterViewHolder(View itemView) {
            super(itemView);
            mVideoTitle = itemView.findViewById(R.id.tv_video_title);
            mVideoType = itemView.findViewById(R.id.tv_video_type);
            mShareButton = itemView.findViewById(R.id.share_button);
            itemView.setOnClickListener(this);
            mShareButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClick(v, getAdapterPosition(), mVideoList);
            }
        }
    }


    /*
     * Recycler view click event code adapted from
     * https://stackoverflow.com/questions/40587168/simple-android-grid-example-using-recyclerview-with-gridlayoutmanager-like-the
     */
    public void setClickListener(ItemClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, List<Videos> videoList);
    }
}
