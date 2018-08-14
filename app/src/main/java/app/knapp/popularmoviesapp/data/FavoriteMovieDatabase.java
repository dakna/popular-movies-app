package app.knapp.popularmoviesapp.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import app.knapp.popularmoviesapp.model.Movie;

@Database(entities = {Movie.class}, version = 1)
public abstract class FavoriteMovieDatabase extends RoomDatabase {

    // lock for singleton
    private static final Object LOCK = new Object();

    private static volatile FavoriteMovieDatabase DBINSTANCE;

    public abstract FavoriteMovieDao favoriteMovieDao();

    static FavoriteMovieDatabase getDatabase(final Context context) {
        if (DBINSTANCE == null) {
            synchronized(LOCK) {
                //still null after syncing all threads?
                if(DBINSTANCE == null) {
                    DBINSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FavoriteMovieDatabase.class, "favorite_movies_database")
                            .build();
                }
            }
        }
        return DBINSTANCE;

    }
}
