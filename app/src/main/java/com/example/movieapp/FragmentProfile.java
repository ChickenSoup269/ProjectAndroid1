package com.example.movieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentProfile extends Fragment {
    private SharedPreferences sharedPreferences;
    private ImageView imgBackgroundUser,imgUser,imgUserEdit, imgBackgroundEdit;
    private TextView txtUserName,txtUserEmail,txtPhone,txtSex,txtDateCreated;
    private EditText edtUserName, edtPhone;
    private Spinner spinnerGender;
    private FloatingActionButton btnThayDoiThongTin;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPreferences = getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        addControls(view);
        loadUserDataFromDatabase();
        return view;
    }

    private void addControls(View view) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        imgBackgroundUser = view.findViewById(R.id.imgBackgroundUser);
        imgUser = view.findViewById(R.id.imgUser);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtUserEmail = view.findViewById(R.id.txtUserEmail);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtSex = view.findViewById(R.id.txtSex);
        txtDateCreated = view.findViewById(R.id.txtDateCreated);
        btnThayDoiThongTin = view.findViewById(R.id.btnThayDoiThongTin);

        btnThayDoiThongTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo dialog và thiết lập layout của nó
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);
                builder.setView(dialogView);
                Button btnDangXuat;
                String userId = sharedPreferences.getString("USER_ID", "");
                String userImage = sharedPreferences.getString("USER_IMAGE", "");
                String userBackground = sharedPreferences.getString("USER_BACKGROUND", "");

                edtUserName = dialogView.findViewById(R.id.edtUserName);
                spinnerGender = dialogView.findViewById(R.id.spinnerGender);
                edtPhone = dialogView.findViewById(R.id.edtPhone);
                imgUserEdit = dialogView.findViewById(R.id.imgUserEdit);
                imgBackgroundEdit = dialogView.findViewById(R.id.imgBackgroundEdit);
                btnDangXuat = dialogView.findViewById(R.id.btnDangXuat);

                edtUserName.setText(txtUserName.getText());
                edtPhone.setText(txtPhone.getText());
                Glide.with(getContext()).load(userImage).into(imgUserEdit);
                Glide.with(getContext()).load(userBackground).into(imgBackgroundEdit);

                Spinner spinnerGender = dialogView.findViewById(R.id.spinnerGender);

                String[] gendersArray = {"Nam", "Nữ"};
                ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, gendersArray) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextColor(getResources().getColor(R.color.white)); // Sử dụng màu từ resource

                        return view;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextColor(getResources().getColor(R.color.black)); // Sử dụng màu từ resource

                        return view;
                    }
                };

                btnDangXuat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Sign out confirmation");
                        builder.setMessage("Are you sure you want to sign out?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Chuyển đến trang đăng nhập
                                Intent intent = new Intent(getActivity(), DangNhap.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
                genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGender.setAdapter(genderAdapter);

                builder.setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newUserName = edtUserName.getText().toString();
                        String newSex = spinnerGender.getSelectedItem().toString();
                        String newPhone = edtPhone.getText().toString();

                        // Cập nhật dữ liệu lên realtime database
                        DatabaseReference userRef = mDatabase.child("users").child(userId);
                        userRef.child("username").setValue(newUserName);
                        userRef.child("sex").setValue(newSex);
                        userRef.child("phone").setValue(newPhone);

                        // Cập nhật UI
                        txtUserName.setText(newUserName);
                        txtSex.setText(newSex);
                        txtPhone.setText(newPhone);
                        Toast.makeText(getContext(), "Successfully updated", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }


    private void loadUserDataFromDatabase() {
        String userId = sharedPreferences.getString("USER_ID", "");
        // Lấy tham chiếu đến người dùng hiện tại trong Realtime Database dựa trên ID người dùng (userId)
        DatabaseReference userRef = mDatabase.child("users").child(userId); // userId là ID của người dùng hiện tại

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        txtUserName.setText(user.getUsername());
                        txtUserEmail.setText(user.getEmail());
                        Glide.with(getContext()).load(user.getUserImg()).into(imgUser);
                        Glide.with(getContext()).load(user.getUserBackground()).into(imgBackgroundUser);
                        txtPhone.setText(user.getPhone());
                        txtSex.setText(user.getSex());
                        txtDateCreated.setText(user.getDateCreated());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}


