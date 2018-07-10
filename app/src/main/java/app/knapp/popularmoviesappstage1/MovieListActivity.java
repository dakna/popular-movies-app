package app.knapp.popularmoviesappstage1;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import app.knapp.popularmoviesappstage1.model.Movie;
import app.knapp.popularmoviesappstage1.network.MovieDbResponse;
import app.knapp.popularmoviesappstage1.network.MovieDbService;
import app.knapp.popularmoviesappstage1.network.MovieDbUtil;
import app.knapp.popularmoviesappstage1.ui.MoviesAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieListActivity extends AppCompatActivity implements MoviesAdapter.OnMovieSelectedListener {

    private static final String TAG = "MovieListActivity";

    private MovieDbService movieDbService;
    private List<Movie> movies;
    private MoviesAdapter moviesAdapter;
    private RecyclerView rvMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

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
            setupMovieList("top_rated");
        }

    }


    public void setupMovieList(String list) {

        Log.d(TAG, "setupMovieList: " + list);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDbUtil.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        movieDbService = retrofit.create(MovieDbService.class);
        Call<MovieDbResponse> call = movieDbService.getMovies(list,BuildConfig.API_KEY);

        call.enqueue(new Callback<MovieDbResponse>() {
            @Override
            public void onResponse(Call<MovieDbResponse> call, Response<MovieDbResponse> response) {
                
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: success " + response.body());
                    movies = response.body().getResults();
                    Log.d(TAG, "onResponse: movie list size " + movies.size());
                    if (null == moviesAdapter) {

                        moviesAdapter = new MoviesAdapter(movies, MovieListActivity.this);
                        rvMovies.setAdapter(moviesAdapter);
                        Log.d(TAG, "onResponse: adapter " + rvMovies.getAdapter());

                    } else {
                        moviesAdapter.notifyDataSetChanged();

                    }

                } else {
                    Toast.makeText(MovieListActivity.this, "Error loading movie list: " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<MovieDbResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                Toast.makeText(MovieListActivity.this, "Error loading movie list: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }


    @Override
    public void onMovieSelected(Movie movie) {
        // create Intent with parcelable movie object and start it
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
