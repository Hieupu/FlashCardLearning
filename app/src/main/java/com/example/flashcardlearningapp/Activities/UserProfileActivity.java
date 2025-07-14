package com.example.flashcardlearningapp.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flashcardlearningapp.Database.DatabaseHelper;
import com.example.flashcardlearningapp.R;

public class UserProfileActivity extends AppCompatActivity {
    TextView tvUsername, tvEmail;
    EditText etPassword;
    ImageView ivTogglePassword;
    DatabaseHelper dbHelper;
    String username;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        etPassword = findViewById(R.id.etPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        dbHelper = new DatabaseHelper(this);

        username = getIntent().getStringExtra("username");

        loadUserProfile(username);

        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_closed);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_open);
            }
            isPasswordVisible = !isPasswordVisible;
            etPassword.setSelection(etPassword.getText().length()); // Giữ con trỏ cuối dòng
        });
    }

    private void loadUserProfile(String username) {
        Cursor cursor = dbHelper.getUserByUsername(username);
        if (cursor != null && cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));

            tvUsername.setText("Username: " + username);
            tvEmail.setText("Email: " + email);
            etPassword.setText(password);

            cursor.close();
        }
    }
}
