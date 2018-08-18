package app.knapp.popularmoviesapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import app.knapp.popularmoviesapp.data.AppExecutors;
import app.knapp.popularmoviesapp.data.FavoriteMovieDatabase;
import app.knapp.popularmoviesapp.data.MovieDetailViewModel;
import app.knapp.popularmoviesapp.model.Movie;
import app.knapp.popularmoviesapp.model.MovieReview;
import app.knapp.popularmoviesapp.model.MovieVideo;
import app.knapp.popularmoviesapp.network.MovieDbService;
import app.knapp.popularmoviesapp.network.MovieDbUtil;
import app.knapp.popularmoviesapp.network.MovieReviewsDbResponse;
import app.knapp.popularmoviesapp.network.MovieVideosDbResponse;
import app.knapp.popularmoviesapp.ui.MovieReviewsAdapter;
import app.knapp.popularmoviesapp.ui.MovieVideosAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailActivity extends AppCompatActivity implements MovieVideosAdapter.OnVideoSelectedListener {

    private static final String TAG = "MovieDetailActivity";

    private MovieDbService movieDbService;
    private FavoriteMovieDatabase favMovieDb;
    private AppExecutors appExecutors;
    private MovieDetailViewModel viewModel;

    
    private List<MovieVideo> movieVideos;
    private List<MovieReview> movieReviews;
    private Movie movie;

    private ImageView ivHeader, ivPoster;
    private TextView tvRating, tvRelease, tvTitle, tvStory;
    private RatingBar ratingBar;

    private RecyclerView rvVideos;
    private RecyclerView rvReviews;
    private MovieVideosAdapter videosAdapter;
    private MovieReviewsAdapter reviewsAdapter;
    private LikeButton btnFavorite;
    boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        favMovieDb = FavoriteMovieDatabase.getDatabase(getApplicationContext());
        appExecutors = AppExecutors.getInstance();
        viewModel = ViewModelProviders.of(this).get(MovieDetailViewModel.class);
        
        movie = getIntent().getParcelableExtra("movie");
        Log.d(TAG, "onCreate: movie " + movie.getTitle());

        ivHeader = findViewById(R.id.ivHeader);
        ivPoster = findViewById(R.id.ivPoster);
        ratingBar = findViewById(R.id.ratingBar);
        tvRating = findViewById(R.id.tvRating);
        tvRelease = findViewById(R.id.tvRelease);
        tvTitle = findViewById(R.id.tvTitle);
        tvStory = findViewById(R.id.tvStory);
        btnFavorite = findViewById(R.id.btnFavorite);

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
        LinearLayoutManager layoutManagerVideos = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvVideos.setLayoutManager(layoutManagerVideos);
        //rvVideos.addItemDecoration(new DividerItemDecoration(this, GridLayoutManager.HORIZONTAL));

        rvReviews = findViewById(R.id.rvReviewList);
        LinearLayoutManager layoutManagerReviews = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.VERTICAL, false);
        rvReviews.setLayoutManager(layoutManagerReviews);
        rvReviews.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // check if movie is favorite based on ID from parcelable
        
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {


                // todo: use view model, not dao directly. chain from viewmodel to repository to dao
                Movie favMovie = favMovieDb.favoriteMovieDao().getFavoriteMoviebyId(movie.getId());
                if (favMovie == null) {
                    Log.d(TAG, "run: movie id " + movie.getId() + " is NOT a favorite");
                    isFavorite = false;

                } else {
                    Log.d(TAG, "run: movie id " + movie.getId() + " IS a favorite");
                    isFavorite = true;
                }
                btnFavorite.setLiked(isFavorite);

            }
        });

        // listen to like button
        btnFavorite.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                Log.d(TAG, "liked: saved movie id " + movie.getId() + " as favorite");
                viewModel.insertFavoriteMovie(movie);
            }

            @Override
            public void unLiked(LikeButton likeButton) {

                Log.d(TAG, "unLiked: delete movie id " + movie.getId() + " from favorites");
                viewModel.deleteFavoriteMovie(movie);
            }
        });
        
        if (TextUtils.isEmpty(BuildConfig.API_KEY)) {
            Toast.makeText(this, R.string.no_api_key, Toast.LENGTH_LONG).show();
        } else if (!MovieDbUtil.isConnected(this)) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        } else {
            setupMovieVideoList(String.valueOf(movie.getId()));
            setupMovieReviewList(String.valueOf(movie.getId()));
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

    public void setupMovieReviewList(String movieId) {

        Log.d(TAG, "setupMovieReviewList: " + movieId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDbUtil.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        movieDbService = retrofit.create(MovieDbService.class);
        Call<MovieReviewsDbResponse> call = movieDbService.getMovieReviews(movieId, BuildConfig.API_KEY);

        call.enqueue(new Callback<MovieReviewsDbResponse>() {
            @Override
            public void onResponse(Call<MovieReviewsDbResponse> call, Response<MovieReviewsDbResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: success " + response.body());
                    movieReviews = response.body().getResults();
                    Log.d(TAG, "onResponse: movie review list size " + movieReviews.size());

                    if (null == reviewsAdapter) {

                        reviewsAdapter = new MovieReviewsAdapter(movieReviews);
                        rvReviews.setAdapter(reviewsAdapter);
                        Log.d(TAG, "onResponse: adapter " + rvReviews.getAdapter());

                    } else {
                        reviewsAdapter.setReviews(movieReviews);

                    }


                } else {
                    Toast.makeText(MovieDetailActivity.this, "Error loading movie review list: " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<MovieReviewsDbResponse> call, Throwable t) {

            }
        });
    }
}
