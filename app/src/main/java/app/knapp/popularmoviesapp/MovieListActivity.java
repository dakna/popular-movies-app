package app.knapp.popularmoviesapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.knapp.popularmoviesapp.data.AppExecutors;
import app.knapp.popularmoviesapp.data.FavoriteMovieDatabase;
import app.knapp.popularmoviesapp.data.MovieListViewModel;
import app.knapp.popularmoviesapp.model.Movie;
import app.knapp.popularmoviesapp.network.MovieListDbResponse;
import app.knapp.popularmoviesapp.network.MovieDbService;
import app.knapp.popularmoviesapp.network.MovieDbUtil;
import app.knapp.popularmoviesapp.ui.MoviesAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieListActivity extends AppCompatActivity implements MoviesAdapter.OnMovieSelectedListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "MovieListActivity";

    private MovieDbService movieDbService;
    private FavoriteMovieDatabase favMovieDb;
    private AppExecutors appExecutors;

    private MovieListViewModel viewModel;


    private List<Movie> movies;
    private List<Movie> favoriteMovies;
    private MoviesAdapter moviesAdapter;
    private MoviesAdapter favoriteMoviesAdapter;
    private RecyclerView rvMovies;
    private ProgressBar progressBar;

    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        favMovieDb = FavoriteMovieDatabase.getDatabase(getApplicationContext());
        appExecutors = AppExecutors.getInstance();

        movies = new ArrayList<>();
        favoriteMovies = new ArrayList<>();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);



        if (TextUtils.isEmpty(BuildConfig.API_KEY)) {
            Toast.makeText(this, R.string.no_api_key, Toast.LENGTH_LONG).show();
        } else if (!MovieDbUtil.isConnected(this)) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        }

        initRecyclerView();
        initViewModel();

    }

    private void initRecyclerView() {
        rvMovies = findViewById(R.id.rvMovieList);
        layoutManager = new GridLayoutManager(this, getGridSize());
        rvMovies.setLayoutManager(layoutManager);
        rvMovies.addItemDecoration(new DividerItemDecoration(this, GridLayoutManager.VERTICAL));
        rvMovies.addItemDecoration(new DividerItemDecoration(this, GridLayoutManager.HORIZONTAL));

        moviesAdapter = new MoviesAdapter(movies, MovieListActivity.this);
        favoriteMoviesAdapter = new MoviesAdapter(favoriteMovies, MovieListActivity.this);
        Log.d(TAG, "initRecyclerView: adapter " + rvMovies.getAdapter());

    }

    private void initViewModel() {

        viewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);
        viewModel.getFavoriteMovies().observe(this, new Observer<List<Movie>>() {

            @Override
            public void onChanged(@Nullable List<Movie> favMovies) {

                Log.d(TAG, "onChanged: movies list size favMovies " + favMovies.size());

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(MovieDbUtil.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                movieDbService = retrofit.create(MovieDbService.class);

                favoriteMovies.clear();
                favoriteMoviesAdapter.setMovies(favoriteMovies);

                if (favMovies.size() > 0 ) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                // countdown for progress bar visibility
                final int[] countDown = {favMovies.size()};

                //iterate over list and get each movie via api call

                for (Movie favMovie: favMovies ) {

                    Call<Movie> call = movieDbService.getMovie(String.valueOf(favMovie.getId()),BuildConfig.API_KEY);

                    call.enqueue(new Callback<Movie>() {
                        @Override
                        public void onResponse(Call<Movie> call, Response<Movie> response) {

                            countDown[0]--;

                            if (response.isSuccessful()) {
                                //Log.d(TAG, "onResponse: success " + response.body());
                                Movie movie = response.body();

                                Log.d(TAG, "onResponse: Movie " + movie);
                                favoriteMovies.add(movie);

                                if (countDown[0] == 0) {
                                    progressBar.setVisibility(View.GONE);
                                    // setup new adapter to clear old list
                                    favoriteMoviesAdapter = new MoviesAdapter(favoriteMovies, MovieListActivity.this);
                                    Log.d(TAG, "onResponse: favoriteMovies list size " + favoriteMovies.size());
                                    Log.d(TAG, "onResponse: rv getAdapter list size " + rvMovies.getAdapter().getItemCount());
                                    Log.d(TAG, "onResponse: favMovie adapter list size " + favoriteMoviesAdapter.getItemCount());

                                }

                                Log.d(TAG, "onResponse: movie id " + movie.getId());

                            } else {
                                Toast.makeText(MovieListActivity.this, "Error loading movie " + response.code(), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Movie> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "onFailure: ", t);
                            Toast.makeText(MovieListActivity.this, "Error loading movie " + t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_list_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);

        Spinner spinner = (Spinner) item.getActionView();
        List<String> dropDownOptions = new ArrayList<>();
        dropDownOptions.add(getResources().getString(R.string.spinner_select_popular));
        dropDownOptions.add(getResources().getString(R.string.spinner_select_toprated));
        dropDownOptions.add(getResources().getString(R.string.spinner_select_favorites));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dropDownOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String selection = sharedPref.getString(getString(R.string.preference_list), MovieDbUtil.POPULAR);

        spinner.setAdapter(spinnerAdapter);
        if (selection.equals(MovieDbUtil.POPULAR)) {
            spinner.setSelection(dropDownOptions.indexOf(getResources().getString(R.string.spinner_select_popular)));

        } else if (selection.equals(MovieDbUtil.TOP_RATED)) {
            spinner.setSelection(dropDownOptions.indexOf(getResources().getString(R.string.spinner_select_toprated)));

        } else if (selection.equals(MovieDbUtil.FAVORITE)) {
            spinner.setSelection(dropDownOptions.indexOf(getResources().getString(R.string.spinner_select_favorites)));

        }

        spinner.setOnItemSelectedListener(this);

        return true;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // change to options menu, show list name in app header bar instead of movies

        String item = parent.getItemAtPosition(position).toString();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Log.d(TAG, "onItemSelected: item " + item);

        if (item.equals(getResources().getString(R.string.spinner_select_popular))) {
            editor.putString(getString(R.string.preference_list), MovieDbUtil.POPULAR);
            editor.commit();

            setupMovieList(MovieDbUtil.POPULAR);

        } else if ((item.equals(getResources().getString(R.string.spinner_select_toprated)))) {
            editor.putString(getString(R.string.preference_list), MovieDbUtil.TOP_RATED);
            editor.commit();

            setupMovieList(MovieDbUtil.TOP_RATED);
        } else if ((item.equals(getResources().getString(R.string.spinner_select_favorites)))) {
            editor.putString(getString(R.string.preference_list), MovieDbUtil.FAVORITE );
            editor.commit();
            Log.d(TAG, "onItemSelected: favorites");
            setupFavoriteMovieList();
        }

    }

    private void setupFavoriteMovieList() {
        Log.d(TAG, "setupFavoriteMovieList: movies list size " + favoriteMovies.size());

        //all changes should have been done by now in view model and its observer
        rvMovies.setAdapter(favoriteMoviesAdapter);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setupMovieList(String list) {

        Log.d(TAG, "setupMovieList: " + list);

        rvMovies.setAdapter(moviesAdapter);
        //moviesAdapter.setMovies(movies);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDbUtil.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        movieDbService = retrofit.create(MovieDbService.class);
        Call<MovieListDbResponse> call = movieDbService.getMovies(list,BuildConfig.API_KEY);

        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<MovieListDbResponse>() {
            @Override
            public void onResponse(Call<MovieListDbResponse> call, Response<MovieListDbResponse> response) {

                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: success " + response.body());
                    movies = response.body().getResults();
                    Log.d(TAG, "onResponse: movie list size " + movies.size());
                    moviesAdapter.setMovies(movies);
                } else {
                    Toast.makeText(MovieListActivity.this, "Error loading movie list: " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<MovieListDbResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: ", t);
                Toast.makeText(MovieListActivity.this, "Error loading movie list: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }


    @Override
    public void onMovieSelected(Movie movie) {
        Log.d(TAG, "onMovieSelected: ");
        // create Intent with parcelable movie object and start it
        Intent detailsIntent = new Intent(this, MovieDetailActivity.class);
        detailsIntent.putExtra("movie", movie);
        startActivity(detailsIntent);

    }

    private int getGridSize() {
        int size = 1;
        switch(getResources().getConfiguration().orientation){
            case Configuration.ORIENTATION_PORTRAIT:
                size= 2;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                size= 4;
                break;
        }
        return size;
    }

}
