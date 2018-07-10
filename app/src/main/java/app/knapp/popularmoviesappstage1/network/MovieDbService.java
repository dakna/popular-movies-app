package app.knapp.popularmoviesappstage1.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDbService {

    @GET("movie/{list}")
    Call<MovieDbResponse> getMovies(@Path("list") String preference, @Query("api_key") String apiKey);
}
