package com.example.movieapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Movie> movies;
    private Context context;
    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMovieDetailActivity(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView coverImageSearch, titleImgSearch;
        private TextView titleMovieSearch;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageSearch = itemView.findViewById(R.id.coverImageSearch);
            titleImgSearch = itemView.findViewById(R.id.titleImgSearch);
//            titleMovieSearch = itemView.findViewById(R.id.titleMovieSearch);
        }

        void bind(Movie movie) {
//            titleMovieSearch.setText(movie.getTitle());

            Glide.with(itemView)
                    .load(movie.getCoverImageURL())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(coverImageSearch);

            Glide.with(itemView)
                    .load(movie.getImageTitleURL())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(titleImgSearch);
        }
    }
    private void openMovieDetailActivity(Movie movie) {
        // Tạo Intent để mở Activity chi tiết và truyền dữ liệu phim
        Intent intent = new Intent(context, MovieDetailedActivity.class);
        intent.putExtra("coverImageURL", movie.getCoverImageURL());
        // Thay "MovieDetailedActivity" bằng tên lớp Activity chi tiết của bạn
        context.startActivity(intent);
    }

}
