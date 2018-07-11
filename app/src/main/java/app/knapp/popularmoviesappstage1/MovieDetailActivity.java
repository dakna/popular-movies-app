package app.knapp.popularmoviesappstage1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.knapp.popularmoviesappstage1.model.Movie;
import app.knapp.popularmoviesappstage1.network.MovieDbUtil;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = "MovieDetailActivity";

    private Movie movie;

    private ImageView ivHeader, ivPoster;
    private TextView tvRating, tvRelease, tvTitle, tvStory;
    private RatingBar ratingBar;

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
    }
}
