package com.example.movieapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControls();
        addEvents();
        loadDefaultFragment();
    }

    // Hàm khởi tạo các view
    private void addControls() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    // Hàm xử lý sự kiện cho BottomNavigationView
    private void addEvents(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.bottom_home) {
                    selectedFragment = new TrangChinhFragment();
                }
                if (item.getItemId() == R.id.bottom_watch) {
                    selectedFragment = new WatchFragment();
                }
                if (item.getItemId() == R.id.bottom_search) {
                    selectedFragment = new SearchFragment();
                }
                if (item.getItemId() == R.id.bottom_loveList) {
                    selectedFragment = new LoveListFragment();
                }
                if (item.getItemId() == R.id.bottom_profile) {
                    selectedFragment = new FragmentProfile();
                }

                // Thay đổi Fragment hiển thị
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }

                return true;
            }
        });

    }

    // Phương thức hiển thị Fragment TrangChinhFragment khi ứng dụng khởi chạy
    private void loadDefaultFragment() {
        Fragment defaultFragment = new TrangChinhFragment();
        loadFragment(defaultFragment);
    }

    // Phương thức thay thế Fragment hiện tại bằng Fragment mới và thêm vào back stack
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Kiểm tra fragment đã tồn tại trong FragmentManager chưa
        Fragment existingFragment = fragmentManager.findFragmentByTag(fragment.getClass().getName());
        if (existingFragment != null) {
            // Nếu đã tồn tại, chỉ cần hiển thị lại fragment
            fragmentTransaction.show(existingFragment);
        } else {
            // Nếu chưa tồn tại, thêm fragment mới vào
            fragmentTransaction.add(R.id.fragmentContainer, fragment, fragment.getClass().getName());
        }
        // Ẩn các fragment còn lại (nếu có)
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment frag : fragments) {
            if (frag != existingFragment) {
                fragmentTransaction.hide(frag);
            }
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}


