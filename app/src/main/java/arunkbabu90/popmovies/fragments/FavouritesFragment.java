package arunkbabu90.popmovies.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import arunkbabu90.popmovies.DetailActivity;
import arunkbabu90.popmovies.R;
import arunkbabu90.popmovies.adapter.FavouritesAdapter;
import arunkbabu90.popmovies.database.AppExecutor;
import arunkbabu90.popmovies.database.Favourite;
import arunkbabu90.popmovies.database.FavouritesViewModel;
import arunkbabu90.popmovies.database.MoviesDatabase;

import static arunkbabu90.popmovies.fragments.PopularFragment.MOVIE_COVER_KEY;
import static arunkbabu90.popmovies.fragments.PopularFragment.MOVIE_D_RATING_KEY;
import static arunkbabu90.popmovies.fragments.PopularFragment.MOVIE_ID_KEY;
import static arunkbabu90.popmovies.fragments.PopularFragment.MOVIE_OVERVIEW_KEY;
import static arunkbabu90.popmovies.fragments.PopularFragment.MOVIE_POSTER_KEY;
import static arunkbabu90.popmovies.fragments.PopularFragment.MOVIE_RATING_KEY;
import static arunkbabu90.popmovies.fragments.PopularFragment.MOVIE_TITLE_KEY;
import static arunkbabu90.popmovies.fragments.PopularFragment.MOVIE_YEAR_KEY;

public class FavouritesFragment extends Fragment implements FavouritesAdapter.ItemClickListener
{
    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private FavouritesAdapter mFavouritesAdapter;
    private MoviesDatabase mDb;
    private  Favourite undoCache;

    public FavouritesFragment() {
        // Required public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.rv_favourites);
        mErrorTextView = view.findViewById(R.id.tv_fav_no_internet);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
        mFavouritesAdapter = new FavouritesAdapter();
        mFavouritesAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mFavouritesAdapter);

        mDb = MoviesDatabase.getInstance(getContext());

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final List<Favourite> favourites = mFavouritesAdapter.getFavouritesList();
                undoCache = favourites.get(position);
                AppExecutor.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.movieDao().deleteFavMovie(favourites.get(position));
                    }
                });
                // Undo action
                Snackbar snackbar = Snackbar.make(view.findViewById(R.id.favourites_fragment),
                        undoCache.getMovieTitle() + " Deleted", Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AppExecutor.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDb.movieDao().insertFavMovie(undoCache);
                                        undoCache = null;
                                    }
                                });
                            }
                        });
                snackbar.setActionTextColor(getResources().getColor(R.color.colorGreen));
                snackbar.show();
            }
        }).attachToRecyclerView(mRecyclerView);

        setupViewModel();
    }

    private void setupViewModel() {
        FavouritesViewModel viewModel = ViewModelProviders.of(this).get(FavouritesViewModel.class);
        viewModel.getFavouriteMovies().observe(FavouritesFragment.this, new Observer<List<Favourite>>() {
            @Override
            public void onChanged(@Nullable List<Favourite> favourites) {
                if (Objects.requireNonNull(favourites).isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    mErrorTextView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mErrorTextView.setVisibility(View.GONE);

                    mFavouritesAdapter.setFavouriteList(getContext(), favourites);
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position, List<Favourite> favouritesList) {
        // Pack all the data extracted from the JSON to a Bundle so that it can be passed to the
        //  DetailActivity; for data saving purposes
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_ID_KEY, favouritesList.get(position).getMovieId());
        bundle.putString(MOVIE_OVERVIEW_KEY, favouritesList.get(position).getOverview());
        bundle.putString(MOVIE_POSTER_KEY, favouritesList.get(position).getPosterPath());
        bundle.putString(MOVIE_RATING_KEY, favouritesList.get(position).getStringRating());
        bundle.putString(MOVIE_YEAR_KEY, favouritesList.get(position).getReleaseYear());
        bundle.putString(MOVIE_TITLE_KEY, favouritesList.get(position).getMovieTitle());
        bundle.putString(MOVIE_COVER_KEY, favouritesList.get(position).getBackDropPath());
        bundle.putDouble(MOVIE_D_RATING_KEY, favouritesList.get(position).getRating());

        Intent showDetailIntent = new Intent(getContext(), DetailActivity.class);
        showDetailIntent.putExtras(bundle);
        ActivityOptionsCompat transitionOptions =
                ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()),
                        view.findViewById(R.id.iv_fav_poster), getString(R.string.main_poster_transition_name));
        startActivity(showDetailIntent, transitionOptions.toBundle());
    }
}
