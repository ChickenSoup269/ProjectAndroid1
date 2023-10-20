package com.example.movieapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class WatchMovie extends AppCompatActivity {

    TextView tvMovieTitle, tv_Nam, tv_Tuoi, tv_Mua, tv_TL;
    VideoView vd_FullPhim;
    LinearLayout lo_Total;
    FrameLayout fullscreenContainer;
    ImageView fullscreenButton,closeButton_Fullscreen;

    boolean isFullscreen = false;

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_movie);

        // Ánh xạ các view từ layout
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tv_Nam = findViewById(R.id.tv_Nam);
        tv_Tuoi = findViewById(R.id.tv_Tuoi);
        tv_Mua = findViewById(R.id.tv_Mua);
        tv_TL = findViewById(R.id.tv_TL);
        vd_FullPhim = findViewById(R.id.vd_FullPhim);
        lo_Total = findViewById(R.id.lo_Total);
        fullscreenContainer = findViewById(R.id.fullscreenContainer);
        fullscreenButton = findViewById(R.id.fullscreen_button);
        closeButton_Fullscreen = findViewById(R.id.closeButton_Fullscreen);

        // Khởi tạo video controller
        MediaController mediaController = new MediaController(this);
        vd_FullPhim.setMediaController(mediaController);

        // Xử lý sự kiện khi click vào fullscreen button
        fullscreenButton.setOnClickListener(v -> toggleFullscreen());
        closeButton_Fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String year = intent.getStringExtra("year");
            String age = intent.getStringExtra("age");
            String seasons = intent.getStringExtra("seasons");
            String category = intent.getStringExtra("category");
            String trailerURL = intent.getStringExtra("trailerURL");

            // Đặt dữ liệu vào các view
            tvMovieTitle.setText(title);
            tv_Nam.setText(year + "  |  ");
            tv_Tuoi.setText(age + "  |  ");
            tv_Mua.setText(seasons + "  |  ");
            tv_TL.setText(category);

            // Lấy videoURL từ Intent
            if (trailerURL != null) {
                // Khởi tạo Uri cho video
                Uri videoUri = Uri.parse(trailerURL);

                // Đặt video URI vào vd_FullPhim
                vd_FullPhim.setVideoURI(videoUri);

                // Sự kiện khi video đã sẵn sàng
                vd_FullPhim.setOnPreparedListener(mp -> {
                    // Hiển thị video và bắt đầu phát
                    vd_FullPhim.start();
                });

                // Đặt sự kiện khi video hoàn thành
                vd_FullPhim.setOnCompletionListener(mp -> {
                    // Xử lý khi video hoàn thành (nếu cần)
                    // Ví dụ: Hiển thị thông báo hoặc chuyển sang video khác
                });

                // Sự kiện khi video bị tạm dừng (pause)
                vd_FullPhim.setOnTouchListener((v, event) -> {
                    if (vd_FullPhim.isPlaying()) {
                        vd_FullPhim.pause();
                    } else {
                        vd_FullPhim.start();
                    }
                    return false;
                });
            } else {
                // Xử lý khi trailerURL không tồn tại
                // Ví dụ: Hiển thị thông báo lỗi hoặc ghi log lỗi
                Toast.makeText(this, "Không có URL trailer cho bộ phim này", Toast.LENGTH_SHORT).show();
                // Hoặc
                Log.e("WatchMovie", "Không có URL trailer cho bộ phim này");
            }
        }
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            // Thoát chế độ toàn màn hình
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            fullscreenButton.setImageResource(R.drawable.baseline_zoom_out_map_24);
            lo_Total.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            isFullscreen = false;
        } else {
            // Chuyển sang chế độ toàn màn hình
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            fullscreenButton.setImageResource(R.drawable.baseline_zoom_out_map_24);
            lo_Total.setVisibility(View.GONE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            isFullscreen = true;
        }
    }
}
