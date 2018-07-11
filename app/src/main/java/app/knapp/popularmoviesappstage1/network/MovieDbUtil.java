package app.knapp.popularmoviesappstage1.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MovieDbUtil {

    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String BASE_URL_IMAGE = "https://image.tmdb.org/t/p/";
    public static final String POSTER_SIZE = "w342";
    public static final String BACKDROP_SIZE = "w1280";

    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!= null && networkInfo.isConnected()) ;
    }
}
