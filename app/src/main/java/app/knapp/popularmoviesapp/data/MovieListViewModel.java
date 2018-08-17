package app.knapp.popularmoviesapp.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import app.knapp.popularmoviesapp.model.Movie;

public class MovieListViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> favoriteMovies;
    private FavoriteMovieRepository repository;

    public MovieListViewModel(@NonNull Application application) {
        super(application);
        repository = FavoriteMovieRepository.getInstance(application.getApplicationContext());
        favoriteMovies = repository.getFavoriteMovies();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }
}
