package com.example.movieapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class FullScreenVideoActivity extends AppCompatActivity {

    private VideoView dialogVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);

        dialogVideoView = findViewById(R.id.dialogVideoViewFullscreen);

        // Lấy URL video từ intent
        String videoUrl = getIntent().getStringExtra("videoUrl");
        dialogVideoView.setVideoURI(Uri.parse(videoUrl));

        // Tạo và thiết lập MediaController
        MediaController mediaController = new MediaController(this);
        dialogVideoView.setMediaController(mediaController);

        // Bắt đầu phát video
        dialogVideoView.start();

        AppCompatImageButton closeButton = findViewById(R.id.closeButtonFullscreen);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogVideoView.isPlaying()) {
                        dialogVideoView.pause();
                }
                dialogVideoView.stopPlayback();
                finish();
                }
            });
        }
    }
