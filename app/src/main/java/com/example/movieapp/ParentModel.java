package com.example.movieapp;

import java.util.ArrayList;

public class ParentModel {
    private String movieCategory;
    private ArrayList<ChildModel> childModelList;

    public ParentModel(String movieCategory) {
        this.movieCategory = movieCategory;
    }
    public String movieCategory() {
        return movieCategory;
    }
    public ArrayList<ChildModel> getChildModelList() {
        return childModelList;
    }
}