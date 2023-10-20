package com.example.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ChildRecyclerViewAdapter extends RecyclerView.Adapter<ChildRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ChildModel> childModelArrayList;
    private Context cxt;
    private OnItemClickListener listener; // Add this member variable

    public interface OnItemClickListener {
        void onChildItemClick(ChildModel childModel);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView heroImage;
        public TextView movieName;

        public MyViewHolder(View itemView) {
            super(itemView);
            heroImage = itemView.findViewById(R.id.hero_image);
            movieName = itemView.findViewById(R.id.movie_name);
        }
    }

    public ChildRecyclerViewAdapter(ArrayList<ChildModel> arrayList, Context mContext) {
        this.cxt = mContext;
        this.childModelArrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_recyclerview_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChildModel currentItem = childModelArrayList.get(position);
        holder.movieName.setText(currentItem.getMovieName());

        // Load image using Glide or your preferred library
        Glide.with(cxt)
                .load(currentItem.getHeroImageURL())
                .into(holder.heroImage);

        // Set click listener for the child items
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onChildItemClick(currentItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return childModelArrayList.size();
    }
}

