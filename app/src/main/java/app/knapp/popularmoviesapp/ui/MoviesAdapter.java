package app.knapp.popularmoviesapp.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.knapp.popularmoviesapp.R;
import app.knapp.popularmoviesapp.model.Movie;
import app.knapp.popularmoviesapp.network.MovieDbUtil;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder>{
    private static final String TAG = "MoviesAdapter";

    private List<Movie> movies;
    private OnMovieSelectedListener movieSelectedListener;

    public MoviesAdapter(List<Movie> movies, OnMovieSelectedListener movieSelectedListener) {

        this.movies = movies;
        this.movieSelectedListener = movieSelectedListener;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }



    @NonNull
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_movie_list, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapterViewHolder holder, int position) {
        Movie movie = movies.get(position);
        Log.d(TAG, "onBindViewHolder: movie " + movie.getTitle() + " at position " + position);
        Picasso.get()
                .load(MovieDbUtil.BASE_URL_IMAGE + MovieDbUtil.POSTER_SIZE + movie.getPosterPath())
                .into(holder.posterImage);

        Log.d(TAG, "onBindViewHolder: rating " + movie.getVoteAverage());
        float scaledRating = (float) movie.getVoteAverage() / 2;
        holder.ratingBar.setRating((scaledRating));
        Log.d(TAG, "onBindViewHolder: ratingbar " + holder.ratingBar.getRating());

        holder.ratingValue.setText(String.valueOf(movie.getVoteAverage()));

    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView posterImage;
        RatingBar ratingBar;
        TextView ratingValue;


        public MoviesAdapterViewHolder(View view) {
            super(view);
            posterImage = view.findViewById(R.id.ivPoster);
            ratingBar = view.findViewById(R.id.ratingBar);
            ratingValue = view.findViewById(R.id.ratingValue);
            ratingBar.setStepSize((float) 0.1);
            itemView.setOnClickListener(MoviesAdapterViewHolder.this);


        }

        @Override
        public void onClick(View view) {
            Movie movie = movies.get(getAdapterPosition());
            Log.d(TAG, "onClick: movie title " + movie.getTitle());
            movieSelectedListener.onMovieSelected(movie);

        }
    }

    public interface OnMovieSelectedListener {
        void onMovieSelected(Movie movie);
    }

    public void setMovies(List<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }
}
