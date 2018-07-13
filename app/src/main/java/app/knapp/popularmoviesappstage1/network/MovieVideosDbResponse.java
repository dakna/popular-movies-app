package app.knapp.popularmoviesappstage1.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.knapp.popularmoviesappstage1.model.MovieVideo;

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
