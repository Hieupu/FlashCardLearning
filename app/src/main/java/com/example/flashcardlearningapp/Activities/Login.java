package com.example.flashcardlearningapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcardlearningapp.Database.DatabaseHelper;
import com.example.flashcardlearningapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class Login extends AppCompatActivity {
    EditText etUsername;
    EditText etPassword;
    EditText etEmail;
    Button btnLogin;
    Button btnRegister;
    DatabaseHelper dbHelper;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
//        etEmail = findViewById(R.id.etEmail); // đừng quên gán etEmail!
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        dbHelper = new DatabaseHelper(this);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Traditional Login
        btnLogin.setOnClickListener(v -> {
//            String email = etEmail.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            } else {
                if (dbHelper.checkUser(username, password)) {
                    Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                    // Đăng nhập thành công, chuyển sang UserProfileActivity
                    Intent intent = new Intent(Login.this, UserProfileActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    // Thử đăng ký nếu chưa tồn tại
                    if (dbHelper.addUser( password, "", username)) {
                        Toast.makeText(Login.this, "User registered and logged in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, UserProfileActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Login.this, "Login failed. Username exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Đăng ký thủ công
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Google Sign-In
        findViewById(R.id.btnGoogleSignIn).setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String email = account.getEmail();

                Toast.makeText(this, "Google Sign-In successful: " + email, Toast.LENGTH_SHORT).show();

                // Lưu vào SQLite nếu chưa tồn tại
                if (!dbHelper.checkUserByEmail(email)) {
                    dbHelper.addUser(email, "google_user", ""); // bạn có thể thay đổi username nếu muốn
                }

                // Chuyển sang UserProfileActivity
                Intent intent = new Intent(Login.this, UserProfileActivity.class);
                intent.putExtra("username", "google_user");
                startActivity(intent);

            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
