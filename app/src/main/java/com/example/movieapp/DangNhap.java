package com.example.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

public class DangNhap extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnSignIn;
    TextView tvSignUp, tvForgotPassword;
//    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    CheckBox checkboxRememberMe;
    SharedPreferences sharedPreferences;
    ImageButton imageBtnShowPass;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER_ME = "rememberMe";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        addControls();
        addEvents();
        loadSavedAccount();
    }

    private void addControls(){
//        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        checkboxRememberMe = (CheckBox) findViewById(R.id.checkbox_RememeberAcc);
        imageBtnShowPass = (ImageButton) findViewById(R.id.imageBtnShowPass);
    }

    private void addEvents(){
        // Đăng nhập
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });

        // Chuyển sang activity (đăng ký)
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DangNhap.this, DangKy.class);
                startActivity(intent);
            }
        });
        // Chuyển sang activity  (tạo mật khẩu mới)
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DangNhap.this, TaoMoiMatKhau.class);
                startActivity(intent);
            }
        });
        // Thêm mã để xử lý sự kiện khi ImageButton được nhấn
        ImageButton btnShowPassword = findViewById(R.id.imageBtnShowPass);
        btnShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordVisibility(); // Gọi hàm để thay đổi trạng thái hiển thị mật khẩu
            }
        });
    }

    private void loadSavedAccount() {
        // Nếu người dùng đã lưu thông tin tài khoản trong SharedPreferences
        // và checkbox remember me được chọn, hiển thị thông tin tài khoản lên màn hình đăng nhập
        if (sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)) {
            String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
            String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
            edtEmail.setText(savedEmail);
            edtPassword.setText(savedPassword);
            checkboxRememberMe.setChecked(true);
        }
    }

    private void signInUser() {
        final String email = edtEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xác thực thông tin đăng nhập bằng Firebase Realtime Database
        mDatabase.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Tìm thấy người dùng có email trùng khớp
                        User user = userSnapshot.getValue(User.class);
                        // kiểm tra password và mã hóa xem có trùng khớp không
                        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                            // Mật khẩu khớp, đăng nhập thành công
                            Toast.makeText(DangNhap.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Nếu checkbox được check sẽ lưu thông tin tài khoản vào SharedPreferences
                            if (checkboxRememberMe.isChecked()) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(KEY_EMAIL, email);
                                editor.putString(KEY_PASSWORD, password);
                                editor.putBoolean(KEY_REMEMBER_ME, true);
                                editor.apply();
                            } else {
                                // Nếu checkbox "Remember Me" không được chọn, xóa thông tin tài khoản khỏi SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove(KEY_EMAIL);
                                editor.remove(KEY_PASSWORD);
                                editor.remove(KEY_REMEMBER_ME);
                                editor.apply();
                            }

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("USER_ID",user.getId());
                            editor.putString("USER_NAME", user.getUsername());
                            editor.putString("USER_EMAIL", user.getEmail());
                            editor.putString("USER_IMAGE", user.getUserImg());
                            editor.putString("USER_BACKGROUND", user.getUserBackground());
                            editor.putString("USER_DATE_CREATED", user.getDateCreated());
                            editor.apply();


                            // chuyển tới MainActivity
                            Intent intent = new Intent(DangNhap.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Sai mật khẩu
                            Toast.makeText(DangNhap.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Không tìm thấy người dùng với email tương ứng
                    Toast.makeText(DangNhap.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Toast.makeText(DangNhap.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isPasswordVisible = false; // Biến để lưu trạng thái hiển thị mật khẩu
    private void togglePasswordVisibility() {
        // Thay đổi trạng thái hiển thị mật khẩu
        isPasswordVisible = !isPasswordVisible;

        // Xác định kiểu hiển thị mật khẩu (hiển thị hoặc ẩn)
        int inputType = isPasswordVisible ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;

        // Áp dụng kiểu hiển thị vào EditText
        edtPassword.setInputType(inputType);

        // Thay đổi biểu tượng của ImageButton tùy thuộc vào trạng thái hiển thị mật khẩu
        int icon = isPasswordVisible ? R.drawable.show : R.drawable.hide;
        ImageButton imageBtnShowPass = findViewById(R.id.imageBtnShowPass);
        imageBtnShowPass.setImageResource(icon);

        // Định dạng văn bản đậm (bold) cho EditText khi hiển thị mật khẩu
        if (isPasswordVisible) {
            Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
            edtPassword.setTypeface(boldTypeface);
        } else {
            // Đặt lại kiểu văn bản ban đầu nếu không hiển thị mật khẩu
            edtPassword.setTypeface(Typeface.DEFAULT);
        }
    }

}
