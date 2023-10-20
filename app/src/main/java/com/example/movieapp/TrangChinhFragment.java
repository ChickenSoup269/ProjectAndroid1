package com.example.movieapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrangChinhFragment extends Fragment implements SliderAdapter.OnItemClickListener, ParentRecyclerViewAdapter.OnItemClickListener {

    private ParentRecyclerViewAdapter parentAdapter;
    private List<ParentModel> parentModelList = new ArrayList<>();
    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();
    private SliderAdapter sliderAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trang_chinh, container, false);
        viewPager2 = rootView.findViewById(R.id.viewPagerImageSlider);

        // Initialize and set up buttons
        Button btnAll = rootView.findViewById(R.id.btnAll);
        Button btnPopular = rootView.findViewById(R.id.btnPopular);
        Button btnKids = rootView.findViewById(R.id.btnkids);
        Button btnAnime = rootView.findViewById(R.id.btnAnime);
        Button btnAction = rootView.findViewById(R.id.btnAction);
        Button btnComedies = rootView.findViewById(R.id.btnComedies);
        Button[] buttons = {btnAll, btnPopular, btnKids, btnAnime, btnAction, btnComedies};

        for (Button button : buttons) {
            button.setBackgroundColor(Color.parseColor("#FFFFFF"));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Button b : buttons) {
                        b.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        b.setTextColor(Color.parseColor("#000000"));
                        b.setTypeface(null, Typeface.BOLD);
                    }

                    button.setBackgroundColor(Color.parseColor("#E50914"));
                    button.setTextColor(Color.parseColor("#FFFFFF"));
                    button.setTypeface(null, Typeface.BOLD);
                }
            });
        }

        // Set default color for "All" button
        btnAll.setBackgroundColor(Color.parseColor("#E50914"));
        btnAll.setTextColor(Color.parseColor("#FFFFFF"));
        btnAll.setTypeface(null, Typeface.BOLD);


        List<SliderItem> sliderItems = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("movies");
        sliderAdapter = new SliderAdapter(sliderItems, viewPager2);

        // Set click listener for the SliderAdapter
        sliderAdapter.setOnItemClickListener(this);
        viewPager2.setAdapter(sliderAdapter);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Populate sliderItems with data
                List<SliderItem> sliderItems = new ArrayList<>();
                for (DataSnapshot movieSnapshot : dataSnapshot.getChildren()) {
                    String coverImageURL = movieSnapshot.child("coverImageURL").getValue(String.class);
                    sliderItems.add(new SliderItem(coverImageURL));
                }

                // Update the data in the sliderAdapter
                sliderAdapter.setSliderItems(sliderItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors here
            }
        });

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 5000);
            }
        });

        // Populate parentModelList with sample data for the RecyclerView
        parentModelList.add(new ParentModel("Popular Movies"));
        parentModelList.add(new ParentModel("Kids"));
        parentModelList.add(new ParentModel("Anime"));
        parentModelList.add(new ParentModel("Action"));
        parentModelList.add(new ParentModel("Comedies"));

        RecyclerView parentRecyclerView = rootView.findViewById(R.id.Parent_recyclerView);
        parentRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager parentLayoutManager = new LinearLayoutManager(requireContext());
        parentAdapter = new ParentRecyclerViewAdapter((ArrayList<ParentModel>) parentModelList, requireContext());

        // Set the click listener for the ParentRecyclerViewAdapter
        parentAdapter.setOnItemClickListener(this);

        parentRecyclerView.setLayoutManager(parentLayoutManager);
        parentRecyclerView.setAdapter(parentAdapter);
        sliderAdapter.setOnItemClickListener(this);

        parentAdapter.setOnItemClickListener(new ParentRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onParentItemClicked(ParentModel parentModel) {
            }

            @Override
            public void onChildItemClicked(ChildModel childModel) {
                Intent intent = new Intent(getActivity(), MovieDetailedActivity.class);
                intent.putExtra("coverImageURL", childModel.getHeroImageURL());
                startActivity(intent);
            }
        });
        return rootView;


    }

    @Override
    public void onParentItemClicked(ParentModel parentModel) {

    }

    @Override
    public void onChildItemClicked(ChildModel childModel) {

    }

    @Override
    public void onItemClicked(ChildModel childModel) {
        Intent intent = new Intent(getActivity(), MovieDetailedActivity.class);
        intent.putExtra("coverImageURL", childModel.getHeroImageURL());
        // Add other data as needed
        startActivity(intent);
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    public void onItemClick(int position) {
        if (sliderAdapter != null) {
            // Lấy SliderItem tại vị trí đã chọn
            SliderItem selectedMovie = sliderAdapter.getItemAt(position);

            // Kiểm tra xem selectedMovie có khác null không
            if (selectedMovie != null) {
                // Lấy coverImageURL của bộ phim đã chọn
                String coverImageURL = selectedMovie.getImageUrl();

                // Tìm kiếm bộ phim trong Realtime Database có chứa coverImageURL tương ứng
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("movies");
                databaseReference.orderByChild("coverImageURL").equalTo(coverImageURL).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Kiểm tra xem có dữ liệu trả về không
                        if (dataSnapshot.exists()) {
                            // Lấy thông tin bộ phim đầu tiên trong kết quả
                            DataSnapshot movieSnapshot = dataSnapshot.getChildren().iterator().next();

                            // Lấy dữ liệu chi tiết của bộ phim
                            String imageTitleURL = movieSnapshot.child("imageTitleURL").getValue(String.class);
                            String seasons = movieSnapshot.child("seasons").getValue(String.class);
                            String star = movieSnapshot.child("star").getValue(String.class);
                            String title = movieSnapshot.child("title").getValue(String.class);
                            int year = movieSnapshot.child("year").getValue(Integer.class);
                            String description = movieSnapshot.child("description").getValue(String.class);
                            String author = movieSnapshot.child("author").getValue(String.class);
                            String backgroundImageURL = movieSnapshot.child("backgroundImageURL").getValue(String.class);
                            String category = movieSnapshot.child("category").getValue(String.class);
                            String trailerURL = movieSnapshot.child("trailerURL").getValue(String.class);
                            int views = movieSnapshot.child("views").getValue(Integer.class);
                            String age = movieSnapshot.child("age").getValue(String.class);

                            // Tiếp tục xử lý và chuyển sang MovieDetailedActivity với các thông tin đã lấy
                            Intent intent = new Intent(getActivity(), MovieDetailedActivity.class);
                            intent.putExtra("coverImageURL", coverImageURL);
                            intent.putExtra("imageTitleURL", imageTitleURL);
                            intent.putExtra("seasons", seasons);
                            intent.putExtra("star", star);
                            intent.putExtra("title", title);
                            intent.putExtra("year", year);
                            intent.putExtra("description", description);
                            intent.putExtra("author", author);
                            intent.putExtra("backgroundImageURL", backgroundImageURL);
                            intent.putExtra("category", category);
                            intent.putExtra("trailerURL", trailerURL);
                            intent.putExtra("views", views);
                            intent.putExtra("age", age);
                            startActivity(intent);
                        } else {
                            // Xử lý khi không tìm thấy bộ phim tương ứng
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi nếu có
                    }
                });
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000); // Start the slideshow after 3 seconds of resuming the fragment
    }

}