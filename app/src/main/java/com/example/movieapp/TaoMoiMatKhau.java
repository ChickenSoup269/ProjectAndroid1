package com.example.movieapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TaoMoiMatKhau extends AppCompatActivity {

    EditText edtEmailTaoMatKhau, edtPasswordTaoMatKhau, edtConfirmPasswordTaoMatKhau;
    TextView tvGoBack;
    Button btnChangePassword, btnDangKyThanhCong;
    CheckBox checkBoxShowPassWordTaoMatKhau;
    FloatingActionButton fabBack;
//    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    String verificationCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tao_moi_mat_khau);

        addControls();
        addEvents();
    }
    private void addControls(){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtEmailTaoMatKhau = (EditText) findViewById(R.id.edtEmailTaoMatKhau);
        edtPasswordTaoMatKhau = (EditText) findViewById(R.id.edtPasswordTaoMatKhau);
        edtConfirmPasswordTaoMatKhau = (EditText) findViewById(R.id.edtConfirmPasswordTaoMatKhau);
        tvGoBack = (TextView) findViewById(R.id.tvGoback);
        checkBoxShowPassWordTaoMatKhau = (CheckBox) findViewById(R.id.checkBoxShowPassWordTaoMatKhau);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        btnDangKyThanhCong = (Button) findViewById(R.id.btnDangKyThanhCong);
        fabBack = (FloatingActionButton) findViewById(R.id.floatingActionButtonBackTaoMoiMatKhau);
    }
    private void addEvents(){
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPasswordChange();
            }
        });

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);

        checkBoxShowPassWordTaoMatKhau.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    edtPasswordTaoMatKhau.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edtConfirmPasswordTaoMatKhau.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                    // Đặt style blod cho EditText khi check
                    edtPasswordTaoMatKhau.setTypeface(boldTypeface);
                    edtConfirmPasswordTaoMatKhau.setTypeface(boldTypeface);
                } else {
                    edtPasswordTaoMatKhau.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edtConfirmPasswordTaoMatKhau.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    // Đặt lại style chữ khi uncheck
                    edtPasswordTaoMatKhau.setTypeface(Typeface.DEFAULT);
                    edtConfirmPasswordTaoMatKhau.setTypeface(Typeface.DEFAULT);
                }
            }
        });
    }
    // Hàm mã hóa địa chỉ email
    private String encodeEmail(String email) {
        try {
            // Sử dụng SHA-256 để mã hóa email thành một chuỗi hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(email.getBytes(StandardCharsets.UTF_8));

            // Chuyển đổi chuỗi hash thành chuỗi hexa
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return email; // Nếu có lỗi trong quá trình mã hóa, trả về email gốc để tránh lỗi
        }
    }

    // Hàm kiểm tra đổi mật khẩu
    private void requestPasswordChange() {
        String email = edtEmailTaoMatKhau.getText().toString().trim();
        String password = edtPasswordTaoMatKhau.getText().toString().trim();
        String confirmPassword = edtConfirmPasswordTaoMatKhau.getText().toString().trim();
        // mã hóa thành một chuỗi duy nhất
        String encodedEmail = encodeEmail(email);

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your new password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Truy vấn kiểm tra xem email đã tồn tại trong Realtime Database hay chưa
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = usersRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email đã tồn tại trong database
                    // Tiến hành gửi mã xác nhận và hiển thị dialog nhập mã xác nhận
                    verificationCode = generateVerificationCode(6);

                    mDatabase.child("password_reset_requests").child(encodedEmail).setValue(verificationCode)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                // Xử lý kết quả thêm giá trị vào Firebase Database
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Nếu thành công, tiếp tục gửi email xác nhận
                                        sendVerificationEmail(email, verificationCode);
                                        // Hiển thị dialog nhập mã xác nhận email
                                        showVerificationCodeDialog(email);
                                    } else {
                                        Toast.makeText(TaoMoiMatKhau.this, "Failed to request password change. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Email không tồn tại trong Realtime Database
                    Toast.makeText(TaoMoiMatKhau.this, "Email not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Toast.makeText(TaoMoiMatKhau.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Hàm gửi mã xác nhận email
    private void sendVerificationEmail(String email, String verificationCode) {
        String username = edtEmailTaoMatKhau.getText().toString().trim();
        String password = "gvqvigwtgkmgskvm";
        // Trang trí text trong email
        String subject = "Email Verification";
        String logoImageUrl = "https://img.icons8.com/?size=512&id=2sZ0sdlG9kWP&format=png";
        String messageContent = "<p>Hi there, this is your verification code, don't tell anyone else " +
                "<img src='" + logoImageUrl +"' style='width: 20px; height: 20px; vertical-align: middle; margin-right: 10px;' /> </p>" +
                "<p>Your verification code is: <strong style=' font-size: 18px;'> " + verificationCode + "</strong> </p>";


        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        // tạo email
        final String finalEmail = email;

        // Gửi code cho email user
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Thêm đuôi ".com" vào địa chỉ email nếu chưa có
                    String emailWithCom = finalEmail;
                    if (!emailWithCom.contains(".com")) {
                        emailWithCom += ".com";
                    }

                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailWithCom));
                    message.setSubject(subject);
                    message.setContent(messageContent, "text/html");

                    Transport.send(message);

                    // Hiển thị Toast thông báo trong tiểu trình giao diện người dùng
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TaoMoiMatKhau.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    final String errorMessage = e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(TaoMoiMatKhau.this);
                            builder.setTitle("Error");
                            builder.setMessage("Failed to send verification email. Error: " + errorMessage);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }

            }
        }).start();
    }

    // Hàm cập nhật pass cho user
    private void changePassword(String email, String newPassword) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = usersRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();

                        // Lấy mật khẩu hiện tại từ snapshot
                        String currentPassword = userSnapshot.child("password").getValue(String.class);

                        // Kiểm tra xem mật khẩu mới có trùng với mật khẩu cũ không
                        if (BCrypt.checkpw(newPassword, currentPassword)) {
                            // Mật khẩu mới trùng với mật khẩu cũ
                            showInvalidPasswordDialog();
                        } else {
                            // Mật khẩu mới không trùng với mật khẩu cũ, tiến hành cập nhật mật khẩu
                            // Mã hóa mật khẩu mới trước khi cập nhật
                            String hashedNewPassword = hashPassword(newPassword);

                            // Thực hiện cập nhật mật khẩu đã mã hóa
                            DatabaseReference userRef = usersRef.child(userId);
                            userRef.child("password").setValue(hashedNewPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Cập nhật mật khẩu thành công
                                                showSuccessDiaLog();
                                                Toast.makeText(TaoMoiMatKhau.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Cập nhật mật khẩu thất bại
                                                Toast.makeText(TaoMoiMatKhau.this, "Failed to update password. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        break;
                    }
                } else {
                    // Không tìm thấy người dùng với email tương ứng
                    Toast.makeText(TaoMoiMatKhau.this, "User with this email not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Toast.makeText(TaoMoiMatKhau.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Dialog nhập code xác nhận
    private void showVerificationCodeDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Tạo EditText cho người dùng nhập mã xác nhận
        final EditText edtVerificationCode = new EditText(this);
        edtVerificationCode.setHint("Enter Verification Code");
        edtVerificationCode.setInputType(InputType.TYPE_CLASS_NUMBER); // Chỉ cho phép nhập số

        // Đặt layout cho dialog và cài đặt bo tròn góc cho background
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(20, 20, 20, 20);
        dialogLayout.setBackgroundResource(R.drawable.rounded_background); // Sử dụng background bo tròn

        dialogLayout.addView(edtVerificationCode);
        builder.setView(dialogLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // kiểm tra mã xác nhận
                String enterCode = edtVerificationCode.getText().toString();
                if (enterCode.equals(verificationCode)) {
                    // nếu mã xác nhận tồn tại -> đổi mật khẩu người dùng
                    String newPassword = edtPasswordTaoMatKhau.getText().toString().trim();
                    changePassword(email, newPassword);
                    dialog.dismiss();
                } else {
                    showInvalidCodeDialog(email);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Dialog thông báo code xác nhận không hợp lệ
    private void showInvalidCodeDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Invalid Verification Code");
        builder.setMessage("The verification code you entered is invalid. Please try again.");

        // Add OK button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                showVerificationCodeDialog(email);
            }
        });

        // Hiện dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // Dialog thông báo mật khẩu không hợp lệ
    private void showInvalidPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Invalid Password");
        builder.setMessage("The new password cannot be the same as the old password. Please enter a different password.");

        // Add OK button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // Hàm mã hóa mật khẩu bằng BCrypt
    private String hashPassword(String password) {
        // Độ phức tạp của mã hóa, giá trị này càng cao thì việc mã hóa càng lâu nhưng càng an toàn
        int complexity = 12;

        // Mã hóa mật khẩu và trả về chuỗi đã mã hóa
        return BCrypt.hashpw(password, BCrypt.gensalt(complexity));
    }

    private String generateVerificationCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    // Thông báo đổi mật khẩu thành công
    private void showSuccessDiaLog(){
        ConstraintLayout successConstraintLayout =  findViewById(R.id.thongBaoDangKyThanhCong);
        View view = LayoutInflater.from(TaoMoiMatKhau.this).inflate(R.layout.success_dialog, successConstraintLayout);
        Button successDone = view.findViewById(R.id.btnDangKyThanhCong);

            AlertDialog.Builder builder = new AlertDialog.Builder(TaoMoiMatKhau.this);
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
}
