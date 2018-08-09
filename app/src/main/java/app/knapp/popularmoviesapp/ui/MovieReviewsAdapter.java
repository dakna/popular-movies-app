package app.knapp.popularmoviesapp.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.knapp.popularmoviesapp.R;
import app.knapp.popularmoviesapp.model.MovieReview;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsAdapterViewHolder> {
    private static final String TAG = "MovieReviewsAdapter";

    private List<MovieReview> reviews;

    public MovieReviewsAdapter(List<MovieReview> reviews) {

        this.reviews = reviews;
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }



    @NonNull
    @Override
    public MovieReviewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_movie_review_list, parent, false);
        return new MovieReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewsAdapterViewHolder holder, int position) {
        MovieReview review = reviews.get(position);
        holder.tvContent.setText(review.getContent());
        holder.tvAuthor.setText(review.getAuthor());

    }

    public class MovieReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvContent;
        TextView tvAuthor;

        public MovieReviewsAdapterViewHolder(View view) {
            super(view);
            tvContent = view.findViewById(R.id.tvContent);
            tvAuthor = view.findViewById(R.id.tvAuthor);


        }

        @Override
        public void onClick(View view) {
            MovieReview review = reviews.get(getAdapterPosition());
            Log.d(TAG, "onClick: review author " + review.getAuthor());

        }
    }


    public void setReviews(List<MovieReview> reviews) {
        this.reviews.clear();
        this.reviews.addAll(reviews);
        notifyDataSetChanged();
    }
}
