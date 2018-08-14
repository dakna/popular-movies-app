package app.knapp.popularmoviesapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import app.knapp.popularmoviesapp.model.Movie;

@Dao
public interface FavoriteMovieDao {

    @Insert
    void insertFavoriteMovie(Movie movie);

    @Delete
    void deleteFavoriteMovie(Movie movie);

    @Query("select * from favorite_movies where id = :id")
    LiveData<Movie> getFavoriteMoviebyId(int id);

    @Query("select * from favorite_movies")
    LiveData<List<Movie>> getFavoriteMovies();
}
