package app.knapp.popularmoviesapp.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import app.knapp.popularmoviesapp.model.Movie;

public class MovieDetailViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> favoriteMovies;
    private FavoriteMovieRepository repository;

    public MovieDetailViewModel(@NonNull Application application) {
        super(application);
        repository = FavoriteMovieRepository.getInstance(application.getApplicationContext());
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void insertFavoriteMovie(Movie movie) {
        repository.insertFavoriteMovie(movie);
    }

    public void deleteFavoriteMovie(Movie movie) {
        repository.deleteFavoriteMovie(movie);
    }
}
