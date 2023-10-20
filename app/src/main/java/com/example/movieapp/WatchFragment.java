 package com.example.movieapp;

 import android.os.Bundle;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.ProgressBar;

 import androidx.fragment.app.Fragment;

 public class WatchFragment extends Fragment {

     private ProgressBar loadingProgressBar;


     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_watch, container, false);

         loadingProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

         return view;
     }
 }
