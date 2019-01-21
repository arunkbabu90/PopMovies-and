package arunkbabu90.popmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import arunkbabu90.popmovies.adapter.ReviewAdapter;
import arunkbabu90.popmovies.adapter.VideosAdapter;
import arunkbabu90.popmovies.database.AppExecutor;
import arunkbabu90.popmovies.database.Favourite;
import arunkbabu90.popmovies.database.MoviesDatabase;
import arunkbabu90.popmovies.fragments.PopularFragment;
import arunkbabu90.popmovies.network.RetrofitClient;
import arunkbabu90.popmovies.network.RetrofitInterface;
import arunkbabu90.popmovies.network.ReviewResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements VideosAdapter.ItemClickListener,
        View.OnClickListener {

    private int mId;
    private double rating;
    private String movieTitle;
    private String posterUrl;
    private String backDropUrl;
    private String overview;
    private String releaseYear;

    private int favId = -1;

    private RecyclerView mRecyclerView;
    private RecyclerView mReviewRecyclerView;
    private VideosAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private ImageView mPosterView;
    private ImageView mCoverView;
    private FloatingActionButton mFavouriteButton;  // This button will be implemented in v2
    private TextView mYearView;
    private TextView mRatingView;
    private TextView mDescriptionView;
    private TextView mTitleView;
    private TextView mVideoFailedTextView;
    private TextView mReviewFailedTextView;

    private MoviesDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.detail_collapsing_toolbar);
        final AppBarLayout appBar = findViewById(R.id.app_bar);

        mDb = MoviesDatabase.getInstance(this);

        // Initialize the Views
        mRecyclerView = findViewById(R.id.rv_video_list);
        mReviewRecyclerView = findViewById(R.id.rv_reviews);
        mPosterView = findViewById(R.id.iv_details_poster);
        mYearView = findViewById(R.id.tv_details_year);
        mRatingView = findViewById(R.id.tv_details_rating);
        mFavouriteButton = findViewById(R.id.btn_favourites);
        mDescriptionView = findViewById(R.id.tv_details_description);
        mCoverView = findViewById(R.id.iv_collapsing_cover);
        mTitleView = findViewById(R.id.tv_video_title);
        mVideoFailedTextView = findViewById(R.id.tv_video_failed);
        mReviewFailedTextView = findViewById(R.id.tv_review_err);

        // Setup the Trailer Recycler view
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mVideoAdapter = new VideosAdapter();
        mVideoAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mVideoAdapter);

        // Setup the Review Recycler view
        mReviewRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mReviewAdapter = new ReviewAdapter();
        mReviewRecyclerView.setAdapter(mReviewAdapter);


        // Get the movie data from the MainActivity intent to prevent loading the movie data again
        final Bundle movieBundle = getIntent().getExtras();
        mId = Objects.requireNonNull(movieBundle).getInt(PopularFragment.MOVIE_ID_KEY);
        movieTitle = movieBundle.getString(PopularFragment.MOVIE_TITLE_KEY);
        posterUrl = movieBundle.getString(PopularFragment.MOVIE_POSTER_KEY);
        backDropUrl = movieBundle.getString(PopularFragment.MOVIE_COVER_KEY);
        overview = movieBundle.getString(PopularFragment.MOVIE_OVERVIEW_KEY);
        rating = movieBundle.getDouble(PopularFragment.MOVIE_D_RATING_KEY);
        releaseYear = movieBundle.getString(PopularFragment.MOVIE_YEAR_KEY);

        mRatingView.setText(movieBundle.getString(PopularFragment.MOVIE_RATING_KEY));
        mDescriptionView.setText(overview);
        mYearView.setText(releaseYear);
        mTitleView.setText(movieTitle);
        /* Convert the movie poster's String URL to a Uri object so that the correct poster can be
            loaded by Picasso. Since we already loaded the poster in the MainActivity the Picasso
            will load it from the Cache; that way it will be good on Internet Data
        */
        Uri posterUri = Uri.parse(posterUrl);
        Uri backdropUri = Uri.parse(backDropUrl);
        Picasso.with(this).load(posterUri).error(R.drawable.ic_img_err).into(mPosterView);
        Picasso.with(this).load(backdropUri).error(R.drawable.ic_img_err).into(mCoverView);

        // Remove the title from the CollapsingToolbar when expanded and show it when collapsed
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollPos = appBar.getTotalScrollRange();

                if (scrollPos + verticalOffset == 0) {
                    // If appbar collapsed; Show the movie title
                    collapsingToolbar.setTitle(movieBundle.getString(PopularFragment.MOVIE_TITLE_KEY));

                } else {
                    // If appbar expanded; Don't show the title. NOTE, the <space> is important
                    collapsingToolbar.setTitle(" ");
                }
            }
        });

        // Load Trailer Data from Internet using Retrofit
        RetrofitInterface loadVideoInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
        Call<VideoResponse> call = loadVideoInterface.getVideos(mId, BuildConfig.API_KEY);
        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideoResponse> call, @NonNull Response<VideoResponse> response) {
                if (!response.isSuccessful() || Objects.requireNonNull(response.body()).getVideoResults().isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    mVideoFailedTextView.setVisibility(View.VISIBLE);
                    mVideoFailedTextView.setText(R.string.err_no_videos);
                    return;
                }

                mRecyclerView.setVisibility(View.VISIBLE);
                mVideoFailedTextView.setVisibility(View.GONE);

                // Extract the Trailer Videos data from JSON and pass it on to the adapter for populating list
                mVideoAdapter.setVideoList(response.body().getVideoResults());
            }

            @Override
            public void onFailure(@NonNull Call<VideoResponse> call,@NonNull Throwable t) {
                mRecyclerView.setVisibility(View.GONE);
                mVideoFailedTextView.setVisibility(View.VISIBLE);
            }
        });

        // Load Trailer Data from the internet using Retrofit
        RetrofitInterface loadReviewInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
        Call<ReviewResponse> reviewResponseCall = loadReviewInterface.getReviews(mId, BuildConfig.API_KEY);
        reviewResponseCall.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewResponse> call, @NonNull Response<ReviewResponse> response) {
                if (!response.isSuccessful() || Objects.requireNonNull(response.body()).getReviewResults().isEmpty()) {
                    mReviewRecyclerView.setVisibility(View.GONE);
                    mReviewFailedTextView.setVisibility(View.VISIBLE);
                    mReviewFailedTextView.setText(R.string.err_no_reviews);
                    return;
                }

                mReviewRecyclerView.setVisibility(View.VISIBLE);
                mReviewFailedTextView.setVisibility(View.GONE);

                mReviewAdapter.setReviews(response.body().getReviewResults());
            }

            @Override
            public void onFailure(@NonNull Call<ReviewResponse> call, @NonNull Throwable t) {
                mReviewRecyclerView.setVisibility(View.GONE);
                mReviewFailedTextView.setVisibility(View.VISIBLE);
            }
        });

        mFavouriteButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set the corresponding image to the Favourites button based on whether it's marked as Favourite or not
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                favId = mDb.movieDao().loadFavouritesById(mId);
                if (favId == mId) {
                    setFavouriteButtonImage(R.drawable.ic_favourite);
                } else {
                    setFavouriteButtonImage(R.drawable.ic_fav_stroke);
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position, List<Videos> videoList) {
        switch (view.getId())
        {
            case R.id.share_button:
                Intent shareLinkIntent = new Intent(Intent.ACTION_SEND);
                shareLinkIntent.putExtra(Intent.EXTRA_TEXT, videoList.get(position).getYouTubeURL());
                shareLinkIntent.setType("text/plain");

                if (shareLinkIntent.resolveActivity(DetailActivity.this.getPackageManager()) != null) {
                    startActivity(Intent.createChooser(shareLinkIntent, getString(R.string.send_to)));
                } else {
                    Toast.makeText(DetailActivity.this, R.string.err_no_app_found, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Intent playTrailerIntent = new Intent(Intent.ACTION_VIEW);
                playTrailerIntent.setData(Uri.parse(videoList.get(position).getYouTubeURL()));
                startActivity(playTrailerIntent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_favourites:
                setFavouritesState();
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            // Override the default activity exit animation when the Up Button in this activity is pressed
            //  to show the Shared Exit Transition
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setFavouritesState() {
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                favId = mDb.movieDao().loadFavouritesById(mId);
                if (favId == mId) {
                    // Remove from Fav List; since it's marked as favourite and the user pressed the Fav Button again
                    mDb.movieDao().deleteFavMovieById(mId);
                    setFavouriteButtonImage(R.drawable.ic_fav_stroke);
                } else {
                    // Mark as Favourite
                    mDb.movieDao().insertFavMovie(new Favourite(mId, movieTitle, posterUrl,
                            backDropUrl, rating, overview, releaseYear, System.currentTimeMillis()));
                    setFavouriteButtonImage(R.drawable.ic_favourite);
                }
            }
        });
    }

    public void setFavouriteButtonImage(final int imageResourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFavouriteButton.setImageResource(imageResourceId);
            }
        });
    }
}
