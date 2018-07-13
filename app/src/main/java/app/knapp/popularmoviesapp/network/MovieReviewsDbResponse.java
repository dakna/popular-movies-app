package app.knapp.popularmoviesapp.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.knapp.popularmoviesapp.model.MovieReview;

public class MovieReviewsDbResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<MovieReview> results;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("total_pages")
    private int totalPages;


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<MovieReview> getResults() {
        return results;
    }

    public void setResults(List<MovieReview> results) {
        this.results = results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
