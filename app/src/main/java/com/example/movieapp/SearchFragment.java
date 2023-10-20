package com.example.movieapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private DatabaseReference database;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private List<Movie> allMovies;

    public SearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        database = FirebaseDatabase.getInstance().getReference().child("movies");
        recyclerView = rootView.findViewById(R.id.recyclerViewMovieSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        allMovies = new ArrayList<>(); // Khởi tạo danh sách phim gốc
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allMovies.clear(); // Xóa danh sách phim gốc để cập nhật lại
                for (DataSnapshot movieSnapshot : dataSnapshot.getChildren()) {
                    Movie movie = movieSnapshot.getValue(Movie.class);
                    if (movie != null) {
                        allMovies.add(movie); // Thêm phim vào danh sách gốc
                    }
                }

                // Khởi tạo adapter và hiển thị danh sách phim gốc ban đầu
                movieAdapter = new MovieAdapter(requireContext(), allMovies);

                recyclerView.setAdapter(movieAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });

        EditText etSearch = rootView.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Khi văn bản sau khi đã thay đổi
                String searchKeyword = editable.toString().trim().toLowerCase();
                List<Movie> filteredMovies = new ArrayList<>();

                for (Movie movie : allMovies) {
                    if (movie.getTitle().toLowerCase().contains(searchKeyword)) {
                        filteredMovies.add(movie);
                    }
                }

                // Cập nhật danh sách phim hiển thị trên RecyclerView
                movieAdapter.setMovies(filteredMovies);
                movieAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }
}
