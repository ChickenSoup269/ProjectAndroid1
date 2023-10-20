package com.example.movieapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import book.Book;
import category.Category;
import category.CategoryAdapter;

public class LoveListFragment extends Fragment {
    RecyclerView rcvCategory;
    CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_love_list, container, false);

        rcvCategory = view.findViewById(R.id.rcv_category);
        categoryAdapter = new CategoryAdapter(getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvCategory.setLayoutManager(linearLayoutManager);
        categoryAdapter.setData(getListCategory());
        rcvCategory.setAdapter(categoryAdapter);

        return view;
    }

    private List<Category> getListCategory() {
        List<Category> listCategory = new ArrayList<>();
        List<Book> listBook = new ArrayList<>();
        listBook.add(new Book(R.drawable.anh4, "FOROZEN II"));
        listBook.add(new Book(R.drawable.anh3, "LION KING"));
        listBook.add(new Book(R.drawable.anh5, "Tom and Jerry"));
        listBook.add(new Book(R.drawable.anh2, "Chị Em"));

        listCategory.add(new Category("Phim hành động", listBook));
        listCategory.add(new Category("Phim đã xem", listBook));
        listCategory.add(new Category("Phim hoạt hình", listBook));
        listCategory.add(new Category("Phim cổ tích", listBook));
        listCategory.add(new Category("Phim tình cảm", listBook));
        return listCategory;
    }
}