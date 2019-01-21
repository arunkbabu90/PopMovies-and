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
import arunkbabu90.popmovies.database.MovieTopRated;
import arunkbabu90.popmovies.database.MoviesDatabase;
import arunkbabu90.popmovies.database.TopMovieViewModel;
import arunkbabu90.popmovies.network.RetrofitClient;
import arunkbabu90.popmovies.network.RetrofitInterface;
import arunkbabu90.popmovies.network.TopMovieResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TopRatedFragment extends Fragment implements
        MoviesAdapter.ItemClickListener
{
    private MoviesAdapter mMoviesAdapter;
    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;

    private List<MovieTopRated> moviesList;
    private MoviesDatabase mDb;


    public TopRatedFragment() {
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

        // Load data from internet using Retrofit Lib
        RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
        Call<TopMovieResponse> call = retrofitInterface.getTopRatedMovies(BuildConfig.API_KEY);
        call.enqueue(new Callback<TopMovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<TopMovieResponse> call, @NonNull Response<TopMovieResponse> response) {
                if (!response.isSuccessful() || Objects.requireNonNull(response.body()).getResults().isEmpty()) {
                    Toast.makeText(getContext(), getText(R.string.err_check_internet), Toast.LENGTH_LONG).show();
                    return;
                }
                mRecyclerView.setVisibility(View.VISIBLE);
                mErrorTextView.setVisibility(View.GONE);

                moviesList =  Objects.requireNonNull(response.body()).getResults();
                AppExecutor.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        MovieTopRated[] moviesArray = new MovieTopRated[moviesList.size()];
                        mDb.movieDao().insertTopRatedMovies(moviesList.toArray(moviesArray));
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<TopMovieResponse> call, @NonNull Throwable t) { }
        });

        setupViewModel();
    }


    private void setupViewModel() {
        TopMovieViewModel viewModel = ViewModelProviders.of(this).get(TopMovieViewModel.class);
        viewModel.getTopMovies().observe(TopRatedFragment.this, new Observer<List<MovieTopRated>>() {
            @Override
            public void onChanged(@Nullable List<MovieTopRated> movies) {
                moviesList = movies;
                // If there is no data to be shown (even from Database) then show the No Internet TextView
                if (Objects.requireNonNull(moviesList).isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    mErrorTextView.setVisibility(View.VISIBLE);
                    mErrorTextView.setText(R.string.err_no_internet);
                } else {
                    mMoviesAdapter.setTopRatedMoviesList(getContext(), moviesList);
                }
            }
        });
    }



    @Override
    public void onItemClick(View view, int position) {
        if (moviesList != null && !this.moviesList.isEmpty()) {
            MovieTopRated movieDetails = this.moviesList.get(position);

            // Pack all the data extracted from the JSON to a Bundle so that it can be passed to the
            //  DetailActivity; for data saving purposes
            Bundle bundle = new Bundle();
            bundle.putInt(PopularFragment.MOVIE_ID_KEY, movieDetails.getMovieId());
            bundle.putString(PopularFragment.MOVIE_OVERVIEW_KEY, movieDetails.getOverview());
            bundle.putString(PopularFragment.MOVIE_POSTER_KEY, movieDetails.getPosterUrl());
            bundle.putString(PopularFragment.MOVIE_RATING_KEY, movieDetails.getStringRating());
            bundle.putString(PopularFragment.MOVIE_YEAR_KEY, movieDetails.getReleaseYear());
            bundle.putString(PopularFragment.MOVIE_TITLE_KEY, movieDetails.getMovieTitle());
            bundle.putString(PopularFragment.MOVIE_COVER_KEY, movieDetails.getBackDropUrl());
            bundle.putDouble(PopularFragment.MOVIE_D_RATING_KEY, movieDetails.getRating());

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
