package arunkbabu90.popmovies.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import arunkbabu90.popmovies.BuildConfig;
import arunkbabu90.popmovies.DetailActivity;
import arunkbabu90.popmovies.MovieUtils;
import arunkbabu90.popmovies.R;
import arunkbabu90.popmovies.adapter.MoviesAdapter;
import arunkbabu90.popmovies.database.AppExecutor;
import arunkbabu90.popmovies.database.MoviePopular;
import arunkbabu90.popmovies.database.MovieViewModel;
import arunkbabu90.popmovies.database.MoviesDatabase;
import arunkbabu90.popmovies.network.MovieResponse;
import arunkbabu90.popmovies.network.RetrofitClient;
import arunkbabu90.popmovies.network.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PopularFragment extends Fragment implements
        MoviesAdapter.ItemClickListener
{
    public static final String BASE_IMG_URL = "http://image.tmdb.org/t/p";
    public static final String MOVIE_TITLE_KEY = "movie_title";
    public static final String MOVIE_POSTER_KEY = "movie_poster";
    public static final String MOVIE_RATING_KEY = "movie_rating";
    public static final String MOVIE_ID_KEY = "movie_id";
    public static final String MOVIE_OVERVIEW_KEY = "movie_overview";
    public static final String MOVIE_YEAR_KEY = "movie_release_year";
    public static final String MOVIE_COVER_KEY = "movie_backdrop";
    public static final String MOVIE_D_RATING_KEY = "double_rating";

    private MoviesAdapter mMoviesAdapter;
    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private MoviesDatabase mDb;

    private List<MoviePopular> moviesList;


    public PopularFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int noOfCols = MovieUtils.calculateNoOfColumns(Objects.requireNonNull(getActivity()).getApplicationContext());

        mRecyclerView = view.findViewById(R.id.rv_main_grid);
        mErrorTextView = view.findViewById(R.id.tv_no_internet);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), noOfCols));
        mMoviesAdapter = new MoviesAdapter();
        mMoviesAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        mErrorTextView.setText(R.string.loading_movies);

        mDb = MoviesDatabase.getInstance(getActivity().getApplicationContext());

        // Load Data from internet using Retrofit Lib
        RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
        Call<MovieResponse> call = retrofitInterface.getPopularMovies(BuildConfig.API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (!response.isSuccessful() || Objects.requireNonNull(response.body()).getPopularResults().isEmpty()) {
                    Toast.makeText(getContext(), getText(R.string.err_check_internet), Toast.LENGTH_LONG).show();
                    return;
                }
                mRecyclerView.setVisibility(View.VISIBLE);
                mErrorTextView.setVisibility(View.GONE);

                moviesList = Objects.requireNonNull(response.body()).getPopularResults();
                AppExecutor.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        MoviePopular[] moviesArray = new MoviePopular[moviesList.size()];
                        mDb.movieDao().insertPopularMovies(moviesList.toArray(moviesArray));
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) { }
        });

        setupViewModel();
    }

    private void setupViewModel() {
        MovieViewModel viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        viewModel.getMovies().observe(PopularFragment.this, new Observer<List<MoviePopular>>() {
            @Override
            public void onChanged(@Nullable List<MoviePopular> movies) {
                moviesList = movies;
                // If there is no data to be shown (even from Database) then show the No Internet TextView
                if (Objects.requireNonNull(moviesList).isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    mErrorTextView.setVisibility(View.VISIBLE);
                    mErrorTextView.setText(R.string.err_no_internet);
                } else {
                    mMoviesAdapter.setPopularMoviesList(getContext(), moviesList);
                }
            }
        });
    }


    @Override
    public void onItemClick(View view, int position) {
        // Launch the DetailActivity if the movie data is not empty
        if (moviesList != null && !this.moviesList.isEmpty()) {
            MoviePopular movieDetails = this.moviesList.get(position);

            // Pack all the data extracted from the JSON to a Bundle so that it can be passed to the
            //  DetailActivity; for data saving purposes
            Bundle bundle = new Bundle();
            bundle.putInt(MOVIE_ID_KEY, movieDetails.getMovieId());
            bundle.putString(MOVIE_OVERVIEW_KEY, movieDetails.getOverview());
            bundle.putString(MOVIE_POSTER_KEY, movieDetails.getPosterUrl());
            bundle.putString(MOVIE_RATING_KEY, movieDetails.getStringRating());
            bundle.putString(MOVIE_YEAR_KEY, movieDetails.getReleaseYear());
            bundle.putString(MOVIE_TITLE_KEY, movieDetails.getMovieTitle());
            bundle.putString(MOVIE_COVER_KEY, movieDetails.getBackDropUrl());
            bundle.putDouble(MOVIE_D_RATING_KEY, movieDetails.getRating());

            // Intent to start DetailActivity with a small transition animation for the movie poster
            Intent showDetailsIntent = new Intent(getContext(), DetailActivity.class);
            showDetailsIntent.putExtras(bundle);
            ActivityOptionsCompat transitionOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()),
                            view.findViewById(R.id.iv_main_poster),
                            getString(R.string.main_poster_transition_name));
            startActivity(showDetailsIntent, transitionOptions.toBundle());
        }
    }
}
