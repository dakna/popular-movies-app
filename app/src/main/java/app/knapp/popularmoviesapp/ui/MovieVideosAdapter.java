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
import app.knapp.popularmoviesapp.model.MovieVideo;
import app.knapp.popularmoviesapp.network.MovieDbUtil;

public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.MovieVideosAdapterViewHolder>{
    private static final String TAG = "MovieVideosAdapter";

    private List<MovieVideo> videos;
    private OnVideoSelectedListener videoSelectedListener;

    public MovieVideosAdapter(List<MovieVideo> videos, OnVideoSelectedListener videoSelectedListener) {

        this.videos = videos;
        this.videoSelectedListener = videoSelectedListener;
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }



    @NonNull
    @Override
    public MovieVideosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_movie_video_list, parent, false);
        return new MovieVideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieVideosAdapterViewHolder holder, int position) {
        MovieVideo video = videos.get(position);
        Log.d(TAG, "onBindViewHolder: video " + video.getKey() + " at position " + position);
        Picasso.get()
                .load(MovieDbUtil.YOUTUBE_URL_IMAGE_PREFIX + video.getKey() + MovieDbUtil.YOUTUBE_URL_IMAGE_POSTFIX)
                .into(holder.youtubePoster);



    }

    public class MovieVideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView youtubePoster;

        public MovieVideosAdapterViewHolder(View view) {
            super(view);
            youtubePoster = view.findViewById(R.id.ivYoutubePoster);
            youtubePoster.setOnClickListener(MovieVideosAdapterViewHolder.this);


        }

        @Override
        public void onClick(View view) {
            MovieVideo video = videos.get(getAdapterPosition());
            Log.d(TAG, "onClick: video title " + video.getName());
            videoSelectedListener.onVideoSelected(video);

        }
    }

    public interface OnVideoSelectedListener {
        void onVideoSelected(MovieVideo video);
    }

    public void setVideos(List<MovieVideo> videos) {
        this.videos.clear();
        this.videos.addAll(videos);
        notifyDataSetChanged();
    }
}
