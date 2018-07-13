package app.knapp.popularmoviesappstage1;

import android.content.Intent;
import android.content.res.Configuration;
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

import app.knapp.popularmoviesappstage1.model.Movie;
import app.knapp.popularmoviesappstage1.network.MovieListDbResponse;
import app.knapp.popularmoviesappstage1.network.MovieDbService;
import app.knapp.popularmoviesappstage1.network.MovieDbUtil;
import app.knapp.popularmoviesappstage1.ui.MoviesAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieListActivity extends AppCompatActivity implements MoviesAdapter.OnMovieSelectedListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "MovieListActivity";

    private MovieDbService movieDbService;
    private List<Movie> movies;
    private MoviesAdapter moviesAdapter;
    private RecyclerView rvMovies;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        rvMovies = findViewById(R.id.rvMovieList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, getGridSize());
        rvMovies.setLayoutManager(layoutManager);
        rvMovies.addItemDecoration(new DividerItemDecoration(this, GridLayoutManager.VERTICAL));
        rvMovies.addItemDecoration(new DividerItemDecoration(this, GridLayoutManager.HORIZONTAL));

        if (TextUtils.isEmpty(BuildConfig.API_KEY)) {
            Toast.makeText(this, "Please enter a valid API KEY to Build Config", Toast.LENGTH_LONG).show();
        } else if (!MovieDbUtil.isConnected(this)) {
            Toast.makeText(this, "Please make sure you have network access", Toast.LENGTH_LONG).show();
        } else {
            setupMovieList(MovieDbUtil.POPULAR);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_list_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);

        Spinner spinner = (Spinner) item.getActionView();
        List<String> dropDownOptions = new ArrayList<>();
        dropDownOptions.add(getResources().getString(R.string.spinner_select_popular));
        dropDownOptions.add(getResources().getString(R.string.spinner_select_toprated));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dropDownOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        return true;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        Log.d(TAG, "onItemSelected: item " + item);

        if (item.equals(getResources().getString(R.string.spinner_select_popular))) {

            //Toast.makeText(this, "item selected " + item, Toast.LENGTH_SHORT).show();
            setupMovieList(MovieDbUtil.POPULAR);

        } else if ((item.equals(getResources().getString(R.string.spinner_select_toprated)))) {

            //Toast.makeText(this, "item selected " + item, Toast.LENGTH_SHORT).show();
            setupMovieList(MovieDbUtil.TOP_RATED);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setupMovieList(String list) {

        Log.d(TAG, "setupMovieList: " + list);

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
                    if (null == moviesAdapter) {

                        moviesAdapter = new MoviesAdapter(movies, MovieListActivity.this);
                        rvMovies.setAdapter(moviesAdapter);
                        Log.d(TAG, "onResponse: adapter " + rvMovies.getAdapter());

                    } else {
                        moviesAdapter.setMovies(movies);

                    }

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
