package com.example.mygrocerystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygrocerystore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    Button signIn;
    EditText email, password;
    TextView signUp,forgot_password;

    FirebaseAuth auth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        auth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        signIn = findViewById(R.id.login_btn);
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        signUp = findViewById(R.id.sign_up);
        forgot_password = findViewById(R.id.forgotpassword);


        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");

        if (!savedEmail.equals("") && !savedPassword.equals("")) {
            email.setText(savedEmail);
            password.setText(savedPassword);
            loginUser();
        }


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emaillogin = email.getText().toString().trim();
                String passwordlogin = password.getText().toString().trim();

                if (TextUtils.isEmpty(emaillogin)) {
                    email.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(passwordlogin)) {
                    password.setError("Password is required");
                    return;
                }

                auth.signInWithEmailAndPassword(emaillogin, passwordlogin)

                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Đăng nhập thành công
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                    // Lưu thông tin đăng nhập vào SharedPreferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("email", emaillogin);
                                    editor.putString("password", passwordlogin);
                                    editor.apply();
                                } else {
                                    // Đăng nhập thất bại
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                        });


            }
        });

    }


    private void showCustomDialog() {
        // Khởi tạo dialog
         Dialog dialog = new Dialog(this);
        // Set layout cho dialog
        dialog.setContentView(R.layout.forgot_password);
        // Không cho dialog biến mất khi click bên ngoài dialog
        dialog.setCanceledOnTouchOutside(false);

        // Khai báo các button trong dialog
        ImageView ImgCancel = dialog.findViewById(R.id.forgotpassword_cancel);
        Button btnOk = dialog.findViewById(R.id.btnsendmail);
        EditText editText = dialog.findViewById(R.id.edtenteremail);

        // Bắt sự kiện khi click vào Cancel button
        ImgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // Bắt sự kiện khi click vào OK button
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(LoginActivity.this, "Email is empty or invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.sendPasswordResetEmail(text).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LoginActivity.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show();

}





    private void loginUser() {

            String userPassword = password.getText().toString();
            String userEmail = email.getText().toString();

            if (TextUtils.isEmpty(userEmail)) {
                Toast.makeText(this, "Email is Empty !", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userPassword.length() < 6) {
                Toast.makeText(this, "Password Length must be greater then 6 letter !", Toast.LENGTH_SHORT).show();
                return;
            }

            // login user
            auth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });




    }
}