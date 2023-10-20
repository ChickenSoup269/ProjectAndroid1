package com.example.movieapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SliderItem> sliderItems  = new ArrayList<>();
    private ViewPager2 viewPager2;
    private OnItemClickListener itemClickListener;

    public SliderAdapter(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }

    public void setSliderItems(List<SliderItem> items) {
        this.sliderItems = items;
        notifyDataSetChanged();
    }
    public SliderAdapter(List<SliderItem> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }
    public SliderItem getItemAt(int position) {
        if (sliderItems != null && position >= 0 && position < sliderItems.size()) {
            return sliderItems.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item_container, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        if (sliderItems.size() == 0) {
            return; // Return early if the list is empty
        }

        SliderItem sliderItem = sliderItems.get(position % sliderItems.size());
        if (sliderItem.getImageResId() != 0) {
            holder.bindLocalImage(sliderItem.getImageResId());
        } else if (sliderItem.getImageUrl() != null) {
            holder.bindRemoteImage(sliderItem.getImageUrl());
        }

        // Implement infinite scrolling
        if (sliderItems.size() > 1) {
            if (position == sliderItems.size() - 2) {
                viewPager2.post(runnable);
            }
        }

        // Set click listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Notify the click event to the listener
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(position % sliderItems.size());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // Set a large number to simulate infinite scroll
        return Integer.MAX_VALUE;
    }
    public List<SliderItem> getSliderItems() {
        return sliderItems;
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        void bindLocalImage(int imageResId) {
            imageView.setImageResource(imageResId);
        }

        void bindRemoteImage(String imageUrl) {
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .apply(requestOptions)
                    .into(imageView);
        }
    }

    // vòng lặp hình ảnh
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    public interface OnItemClickListener {
        void onParentItemClicked(ParentModel parentModel);

        void onItemClicked(ChildModel childModel);

        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
}
