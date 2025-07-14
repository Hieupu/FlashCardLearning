package com.example.flashcardlearningapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flashcardlearningapp.Database.DatabaseHelper;
import com.example.flashcardlearningapp.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etUsername;
    private Button btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        btnRegister = findViewById(R.id.btnRegister);
        dbHelper = new DatabaseHelper(this);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String username = etUsername.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem email đã tồn tại chưa (dành cho Google hoặc email trùng)
            if (dbHelper.checkGoogleUser(email)) {
                Toast.makeText(this, "Email đã được sử dụng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem username đã tồn tại chưa
            if (dbHelper.checkUser(username, "")) {
                Toast.makeText(this, "Username đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đăng ký người dùng
            boolean success = dbHelper.addUser(password, email, username);
            if (success) {
                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Login.class)); // chuyển sang màn hình login
                finish();
            } else {
                Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

