package com.example.movieapp;

public class ChildModel {
    private String heroImageURL; // Change the field name to heroImageURL
    private String movieName;

    public ChildModel(String heroImageURL, String movieName) {
        this.heroImageURL = heroImageURL;
        this.movieName = movieName;
    }

    public String getHeroImageURL() {
        return heroImageURL;
    }

    public String getMovieName() {
        return movieName;
    }
}
