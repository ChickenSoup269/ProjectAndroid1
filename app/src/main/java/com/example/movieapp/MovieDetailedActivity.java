package com.example.movieapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MovieDetailedActivity extends AppCompatActivity {

    private TextView txtTitleMovie, txtDescription, txtYear, txtStar, txtSeasons, txtAuthor,txtCategory,txtAge,txtView;
    private ImageView imgBackground, imgTitle,videoThumbnail;
    private Button btnPlay,  btnAdd;
    private int currentViews = 0; // số lượt xem
    private FloatingActionButton floatingActionButtonPlayTrailer;
    private ProgressBar loadingProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detailed);
        txtTitleMovie = findViewById(R.id.txtTitleMovie);
        txtDescription = findViewById(R.id.txtDescription);
        txtYear = findViewById(R.id.txtYear);
        txtStar = findViewById(R.id.txtStar);
        txtSeasons = findViewById(R.id.txtSeasons);
        txtAuthor = findViewById(R.id.txtAuthor);
        txtCategory = findViewById(R.id.txtCategory);
        txtAge = findViewById(R.id.txtAge);
        txtView = findViewById(R.id.txtView);
        imgBackground = findViewById(R.id.imgBackground);
        imgTitle = findViewById(R.id.imgTitle); // imgTitle
        videoThumbnail = findViewById(R.id.videoThumbnail);
        btnPlay = findViewById(R.id.btnPlay);
        floatingActionButtonPlayTrailer = findViewById(R.id.floatingActionButtonPlayTrailer);

        Intent intent = getIntent();
        if (intent != null) {
            String coverImageURL = intent.getStringExtra("coverImageURL");

            // Tìm kiếm bộ phim trong Realtime Database có chứa coverImageURL tương ứng
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("movies");
            databaseReference.orderByChild("coverImageURL").equalTo(coverImageURL).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Kiểm tra xem có dữ liệu trả về không
                    if (dataSnapshot.exists()) {

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

                        currentViews = movieSnapshot.child("views").getValue(Integer.class); // tính lượt view

                        // Hiển thị dữ liệu lên các view
                        txtTitleMovie.setText(title);
                        txtDescription.setText(description);
                        txtYear.setText(String.valueOf(year +" |"));
                        txtStar.setText(star);
                        txtSeasons.setText(seasons +" |");
                        txtCategory.setText(category);
                        txtAge.setText(String.valueOf(age+ " |"));
                        txtView.setText(String.valueOf(views+ "M"));
                        txtAuthor.setText(author);
                        Glide.with(MovieDetailedActivity.this).load(backgroundImageURL).into(videoThumbnail);
                        Glide.with(MovieDetailedActivity.this).load(backgroundImageURL).into(imgBackground);

                        Glide.with(MovieDetailedActivity.this).load(imageTitleURL).into(imgTitle);

                        btnPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                currentViews++; // Tăng lượt xem
                                DatabaseReference viewsReference = movieSnapshot.getRef().child("views");
                                viewsReference.setValue(currentViews); // cập nhật views trong database

                                // Chuyển sang activity phim
                                Intent intent = new Intent(MovieDetailedActivity.this, WatchMovie.class);
                                intent.putExtra("title", txtTitleMovie.getText().toString());
                                intent.putExtra("year", txtYear.getText().toString().replace(" |", ""));
                                intent.putExtra("category", txtCategory.getText().toString());
                                intent.putExtra("age", txtAge.getText().toString().replace(" |", ""));
                                intent.putExtra("seasons", txtSeasons.getText().toString().replace(" |", ""));
                                intent.putExtra("trailerURL",trailerURL);
                                startActivity(intent);

                            }
                        });

                        floatingActionButtonPlayTrailer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MovieDetailedActivity.this);
                                View dialogView = LayoutInflater.from(MovieDetailedActivity.this).inflate(R.layout.dialog_video_trailer, null);

                                alertDialogBuilder.setView(dialogView);

                                VideoView dialogVideoView = dialogView.findViewById(R.id.videoDiaLogTrailer);
                                ImageButton btnStop = dialogView.findViewById(R.id.btnStop);
                                ImageButton btnFastForward = dialogView.findViewById(R.id.btnFastForward);
                                ImageButton btnRewind = dialogView.findViewById(R.id.btnRewind);
                                ImageButton btnFullscreen = dialogView.findViewById(R.id.btnFullscreen);
                                dialogVideoView.setVideoURI(Uri.parse(trailerURL));
                                final boolean[] isPlaying = {true};

                                loadingProgressBar =  dialogView.findViewById(R.id.progress_bar);
                                dialogVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        // Khi video đã sẵn sàng, ẩn ProgressBar và bắt đầu phát video
                                        loadingProgressBar.setVisibility(View.GONE);
                                        dialogVideoView.start();
                                    }
                                });
                                // nút dừng
                                btnStop.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (isPlaying[0]) {
                                            dialogVideoView.pause();
                                            isPlaying[0] = false;
                                            btnStop.setImageResource(R.drawable.baseline_play_arrow_24);
                                        } else {
                                            dialogVideoView.start();
                                            isPlaying[0] = true;
                                            btnStop.setImageResource(R.drawable.baseline_pause_24);
                                        }
                                    }
                                });
                                // nút tua nhanh
                                btnFastForward.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int currentPosition = dialogVideoView.getCurrentPosition();
                                        dialogVideoView.seekTo(currentPosition + 10000);
                                    }
                                });
                                // nút tua ngược
                                btnRewind.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int currentPosition = dialogVideoView.getCurrentPosition();
                                        dialogVideoView.seekTo(currentPosition - 10000);
                                    }
                                });

                                btnFullscreen.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (dialogVideoView.isPlaying()) {
                                            dialogVideoView.pause();
                                        }

                                        Intent intent = new Intent(MovieDetailedActivity.this, FullScreenVideoActivity.class);
                                        intent.putExtra("videoUrl", trailerURL);
                                        startActivity(intent);
                                    }
                                });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                alertDialog.show();

                                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                                params.copyFrom(alertDialog.getWindow().getAttributes());
                                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                alertDialog.getWindow().setAttributes(params);

                                FloatingActionButton closeButton = dialogView.findViewById(R.id.closeButton);
                                closeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("MovieDetailedActivity", "Database Error: " + databaseError.getMessage());
                }
            });

            FloatingActionButton floatingBtnBackChitietPhim = findViewById(R.id.floatingBtnBackChitietPhim);
            floatingBtnBackChitietPhim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
