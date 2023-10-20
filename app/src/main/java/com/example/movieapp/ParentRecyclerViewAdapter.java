package com.example.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ParentRecyclerViewAdapter extends RecyclerView.Adapter<ParentRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ParentModel> parentModelArrayList;
    private Context cxt;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onParentItemClicked(ParentModel parentModel);
        void onChildItemClicked(ChildModel childModel);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView category;
        public RecyclerView childRecyclerView;

        public MyViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.Movie_category);
            childRecyclerView = itemView.findViewById(R.id.Child_RV);
        }
    }

    public ParentRecyclerViewAdapter(ArrayList<ParentModel> exampleList, Context context) {
        this.parentModelArrayList = exampleList;
        this.cxt = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_recyclerview_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ParentModel currentItem = parentModelArrayList.get(position);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(cxt, LinearLayoutManager.HORIZONTAL, false);
        holder.childRecyclerView.setLayoutManager(layoutManager);
        holder.childRecyclerView.setHasFixedSize(true);

        String truncatedCategory = currentItem.movieCategory();
        holder.category.setText(truncatedCategory);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("movies");

        if (currentItem.movieCategory().equals("Popular Movies")) {
            databaseReference.orderByChild("views").startAt(150)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<ChildModel> arrayList = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String coverImageURL = snapshot.child("coverImageURL").getValue(String.class);
                                String movieName = snapshot.child("title").getValue(String.class);
                                arrayList.add(new ChildModel(coverImageURL, movieName));
                            }

                            ChildRecyclerViewAdapter childRecyclerViewAdapter = new ChildRecyclerViewAdapter(arrayList, holder.childRecyclerView.getContext());
                            holder.childRecyclerView.setAdapter(childRecyclerViewAdapter);

                            // Set the click listener for child items
                            childRecyclerViewAdapter.setOnItemClickListener(new ChildRecyclerViewAdapter.OnItemClickListener() {
                                @Override
                                public void onChildItemClick(ChildModel childModel) {
                                    if (listener != null) {
                                        listener.onChildItemClicked(childModel);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors here
                        }
                    });
        } else {
            // For other categories, use the existing query
            databaseReference.orderByChild("category").equalTo(currentItem.movieCategory())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<ChildModel> arrayList = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String coverImageURL = snapshot.child("coverImageURL").getValue(String.class);
                                String movieName = snapshot.child("title").getValue(String.class);
                                arrayList.add(new ChildModel(coverImageURL, movieName));
                            }

                            ChildRecyclerViewAdapter childRecyclerViewAdapter = new ChildRecyclerViewAdapter(arrayList, holder.childRecyclerView.getContext());
                            holder.childRecyclerView.setAdapter(childRecyclerViewAdapter);

                            childRecyclerViewAdapter.setOnItemClickListener(new ChildRecyclerViewAdapter.OnItemClickListener() {
                                @Override
                                public void onChildItemClick(ChildModel childModel) {
                                    if (listener != null) {
                                        listener.onChildItemClicked(childModel);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return parentModelArrayList.size();
    }
}
