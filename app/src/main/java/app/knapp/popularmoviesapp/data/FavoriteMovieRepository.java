package app.knapp.popularmoviesapp.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import app.knapp.popularmoviesapp.model.Movie;

public class FavoriteMovieRepository {

    private FavoriteMovieDatabase db;
    private AppExecutors executors;

    private static FavoriteMovieRepository INSTANCE;

    public  static FavoriteMovieRepository getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new FavoriteMovieRepository(context);
        }
        return INSTANCE;
    }

    private FavoriteMovieRepository(Context context) {

        db = FavoriteMovieDatabase.getDatabase(context);
        executors = AppExecutors.getInstance();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return db.favoriteMovieDao().getFavoriteMovies();
    }

    public Movie getMovieById(int id) {
        return db.favoriteMovieDao().getFavoriteMoviebyId(id);
    }
}
