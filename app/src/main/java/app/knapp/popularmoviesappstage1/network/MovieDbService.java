package app.knapp.popularmoviesappstage1.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDbService {

    @GET("movie/{list}")
    Call<MovieListDbResponse> getMovies(@Path("list") String preference, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/reviews")
    Call<MovieReviewsDbResponse> getMovieReviews(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<MovieVideosDbResponse> getMovieVideos(@Path("movie_id") String id, @Query("api_key") String apiKey);


}
