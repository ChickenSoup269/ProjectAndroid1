package com.example.movieapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DangKy extends AppCompatActivity {
    EditText edtUserName, edtEmail, edtPassword, edtConfirmPassword;
    CheckBox checkBoxShowPassword;
    Button btnSignUp, btnDangKyThanhCong;
    TextView tvSignIn;
//    FirebaseAuth mAuth;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);

        addControls();
        addEvents();

    }

    private void addControls(){
//        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtUserName = (EditText) findViewById(R.id.edtUserNameDangKy);
        edtEmail = (EditText) findViewById(R.id.edtEmailDangky);
        edtPassword = (EditText) findViewById(R.id.edtPasswordDangKy);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPasswordDangKy);
        checkBoxShowPassword = (CheckBox) findViewById(R.id.checkBoxShowPassWordDangKy);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnDangKyThanhCong = (Button) findViewById(R.id.btnDangKyThanhCong);
        tvSignIn = (TextView) findViewById(R.id.tvSignIn);
    }

    private void addEvents(){
        // Button đăng ký và hiện thông báo khi đăng ký thành công
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        // Textview quay về trang đăng nhập
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DangKy.this, DangNhap.class);
                startActivity(intent);
            }
        });

        // Button mũi tên quay về trang trước đó
        findViewById(R.id.floatingActionButtonBackDangKy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Hiện mật khẩu khi nhắn
        checkBoxShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edtConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                if (isChecked) {
                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                    edtPassword.setTypeface(boldTypeface);
                    edtConfirmPassword.setTypeface(boldTypeface);
                } else {
                    edtPassword.setTypeface(Typeface.DEFAULT);
                    edtConfirmPassword.setTypeface(Typeface.DEFAULT);
                }
            }
        });

    }

    // Hàm kiểm tra email hợp lệ
    private boolean isValidEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Biến tăng ID theo số tài khoản đc tạo
    private int userCount = 0;
    private void registerUser(){
        final String username = edtUserName.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Các điều kiện khi đăng ký tài khoản
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xác nhận mật khẩu
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // Kiểm tra email đã tồn tại trên database chưa
            mDatabase.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Email đã tồn tại trong database
                        Toast.makeText(DangKy.this, "Email already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // Lấy giá trị số lượng user hiện tại
                        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Tính số lượng người dùng hiện tại bằng cách đếm số lượng nút con của nút "users"
                                int userCount = (int) dataSnapshot.getChildrenCount();

                                // Mã hóa mật khẩu trước khi đăng ký
                                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

                                // id user VD: user_1 -> _2 _3
                                userCount++;
                                String userId = "user_" + userCount;

                                // Tạo đối tượng User mới
                                User user = new User(userId, username, email, hashedPassword);

                                // lưu user vào Realtime Database
                                mDatabase.child("users").child(userId).setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // User được đăng ký thành công
                                                    showSuccessDiaLog();
                                                    Toast.makeText(DangKy.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                    Log.d("DangKyActivity", "User information saved successfully.");
                                                    uploadDefaultInfomations(userId);
                                                } else {
                                                    // Lỗi khi lưu thông tin người dùng
                                                    Toast.makeText(DangKy.this, "Failed to save user information. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    Log.e("DangKyActivity", "Failed to save user information: " + task.getException().getMessage());
                                                }
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(DangKy.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("DangKyActivity", "Database Error: " + databaseError.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DangKy.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DangKyActivity", "Database Error: " + databaseError.getMessage());
                }
            });
        }
    }

    // Thông báo đăng ký thành công
    private void showSuccessDiaLog(){
        ConstraintLayout successConstraintLayout =  findViewById(R.id.thongBaoDangKyThanhCong);
        View view = LayoutInflater.from(DangKy.this).inflate(R.layout.success_dialog, successConstraintLayout);
        Button successDone = view.findViewById(R.id.btnDangKyThanhCong);

        AlertDialog.Builder builder = new AlertDialog.Builder(DangKy.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        successDone.findViewById(R.id.btnDangKyThanhCong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });
        if (alertDialog.getWindow()!= null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    private String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    // các thông tin mặc định cho user
    private void uploadDefaultInfomations(String userId) {
        // Đưa thông tin user lên realtime database
        String defaultUserImageURL = "https://cdn-icons-png.flaticon.com/512/3177/3177440.png";
        String defaultBackgroundURL = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/7d947e1c-d377-48d9-ab3e-6cfc41f6faa7/delgp3k-a94205d9-be04-4e4f-8865-71e321a55922.png/v1/fill/w_1192,h_670,q_70,strp/reflections_in_the_moonlight_by_gydw1n_delgp3k-pre.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9NzIwIiwicGF0aCI6IlwvZlwvN2Q5NDdlMWMtZDM3Ny00OGQ5LWFiM2UtNmNmYzQxZjZmYWE3XC9kZWxncDNrLWE5NDIwNWQ5LWJlMDQtNGU0Zi04ODY1LTcxZTMyMWE1NTkyMi5wbmciLCJ3aWR0aCI6Ijw9MTI4MCJ9XV0sImF1ZCI6WyJ1cm46c2VydmljZTppbWFnZS5vcGVyYXRpb25zIl19.Mc5Vmza2uAQR7zqKsObL2zh0PaXpdEU7MvtuhGGSodg";
        String defaultPhone = "N/A";
        String defaultSex = "N/A";
        long dateCreated = System.currentTimeMillis();
        String dateCreatedFormatted = formatDate(dateCreated);

        mDatabase.child("users").child(userId).child("userImg").setValue(defaultUserImageURL);
        mDatabase.child("users").child(userId).child("userBackground").setValue(defaultBackgroundURL);
        mDatabase.child("users").child(userId).child("phone").setValue(defaultPhone);
        mDatabase.child("users").child(userId).child("sex").setValue(defaultSex);
        mDatabase.child("users").child(userId).child("dateCreated").setValue(dateCreatedFormatted);

    }
}