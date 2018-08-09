package app.knapp.popularmoviesapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import app.knapp.popularmoviesapp.model.Movie;
import app.knapp.popularmoviesapp.model.MovieVideo;
import app.knapp.popularmoviesapp.network.MovieDbService;
import app.knapp.popularmoviesapp.network.MovieDbUtil;
import app.knapp.popularmoviesapp.network.MovieVideosDbResponse;
import app.knapp.popularmoviesapp.ui.MovieVideosAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailActivity extends AppCompatActivity implements MovieVideosAdapter.OnVideoSelectedListener {

    private static final String TAG = "MovieDetailActivity";

    private MovieDbService movieDbService;
    private List<MovieVideo> movieVideos;
    private Movie movie;

    private ImageView ivHeader, ivPoster;
    private TextView tvRating, tvRelease, tvTitle, tvStory;
    private RatingBar ratingBar;

    private RecyclerView rvVideos;
    private MovieVideosAdapter videosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movie = getIntent().getParcelableExtra("movie");
        Log.d(TAG, "onCreate: movie " + movie.getTitle());

        ivHeader = findViewById(R.id.ivHeader);
        ivPoster = findViewById(R.id.ivPoster);
        ratingBar = findViewById(R.id.ratingBar);
        tvRating = findViewById(R.id.tvRating);
        tvRelease = findViewById(R.id.tvRelease);
        tvTitle = findViewById(R.id.tvTitle);
        tvStory = findViewById(R.id.tvStory);

        Picasso.get()
                .load(MovieDbUtil.BASE_URL_IMAGE + MovieDbUtil.POSTER_SIZE + movie.getBackdropPath())
                .into(ivHeader);


        Picasso.get()
                .load(MovieDbUtil.BASE_URL_IMAGE + MovieDbUtil.POSTER_SIZE + movie.getPosterPath())
                .into(ivPoster);

        float scaledRating = (float) movie.getVoteAverage() / 2;
        ratingBar.setRating((scaledRating));
        tvRating.setText(String.valueOf(movie.getVoteAverage()));
        Log.d(TAG, "onCreate: release " + movie.getReleaseDate());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date releaseDate = formatter.parse(movie.getReleaseDate());
            tvRelease.setText(new SimpleDateFormat("MM/dd/yy ").format(releaseDate));
        } catch (ParseException e) {
            Log.e(TAG, "onCreate: Error parsing Movie Date" , e);
            tvRelease.setText("n/a");
        }

        tvTitle.setText(movie.getTitle());
        tvStory.setText(movie.getOverView());


        rvVideos = findViewById(R.id.rvVideoList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvVideos.setLayoutManager(layoutManager);
        //rvVideos.addItemDecoration(new DividerItemDecoration(this, GridLayoutManager.HORIZONTAL));


        if (TextUtils.isEmpty(BuildConfig.API_KEY)) {
            Toast.makeText(this, "Please enter a valid API KEY to Build Config", Toast.LENGTH_LONG).show();
        } else if (!MovieDbUtil.isConnected(this)) {
            Toast.makeText(this, "Please make sure you have network access to load movie trailers", Toast.LENGTH_LONG).show();
        } else {
            setupMovieVideoList(String.valueOf(movie.getId()));
        }
    }

    public void setupMovieVideoList(String movieId) {

        Log.d(TAG, "setupMovieVideoList: " + movieId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDbUtil.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        movieDbService = retrofit.create(MovieDbService.class);
        Call<MovieVideosDbResponse> call = movieDbService.getMovieVideos(movieId, BuildConfig.API_KEY);

        call.enqueue(new Callback<MovieVideosDbResponse>() {
            @Override
            public void onResponse(Call<MovieVideosDbResponse> call, Response<MovieVideosDbResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: success " + response.body());
                    movieVideos = response.body().getResults();
                    Log.d(TAG, "onResponse: movie video list size " + movieVideos.size());

                    if (null == videosAdapter) {

                        videosAdapter = new MovieVideosAdapter(movieVideos, MovieDetailActivity.this);
                        rvVideos.setAdapter(videosAdapter);
                        Log.d(TAG, "onResponse: adapter " + rvVideos.getAdapter());

                    } else {
                        videosAdapter.setVideos(movieVideos);

                    }


                } else {
                    Toast.makeText(MovieDetailActivity.this, "Error loading movie video list: " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<MovieVideosDbResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onVideoSelected(MovieVideo video) {

        Log.d(TAG, "onVideoSelected: video key " + video.getKey());

        String URL = MovieDbUtil.YOUTUBE_URL + video.getKey();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(URL));
        startActivity(intent);



    }
}
