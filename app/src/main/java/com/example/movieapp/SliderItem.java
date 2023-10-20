package com.example.movieapp;

public class SliderItem {
    private int imageResId;
    private String imageUrl;
    private String title;
    private String description;

    // Constructor for local resource image
    public SliderItem(int imageResId,String title, String description) {
        this.imageResId = imageResId;
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
    // Constructor for image URL
    public SliderItem(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}


