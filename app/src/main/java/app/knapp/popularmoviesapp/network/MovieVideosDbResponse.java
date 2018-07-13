package app.knapp.popularmoviesapp.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.knapp.popularmoviesapp.model.MovieVideo;

public class MovieVideosDbResponse {


    @SerializedName("results")
    private List<MovieVideo> results;


    public List<MovieVideo> getResults() {
        return results;
    }

    public void setResults(List<MovieVideo> results) {
        this.results = results;
    }

}
