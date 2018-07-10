package app.knapp.popularmoviesappstage1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import app.knapp.popularmoviesappstage1.model.Movie;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = "MovieDetailActivity";

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movie = getIntent().getParcelableExtra("movie");
        Log.d(TAG, "onCreate: movie " + movie.getOriginalTitle());
    }
}
