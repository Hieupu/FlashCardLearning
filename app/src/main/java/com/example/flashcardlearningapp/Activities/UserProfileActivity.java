package com.example.flashcardlearningapp.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcardlearningapp.Database.DatabaseHelper;
import com.example.flashcardlearningapp.R;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewEmail;
    private EditText editTextPassword;
    private Button buttonUpdate, buttonLogout;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("userEmail", "");

        textViewEmail = findViewById(R.id.textViewEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonLogout = findViewById(R.id.buttonLogout);

        Log.d("UserProfileActivity", "Loaded email from SharedPreferences: " + email);
        textViewEmail.setText(email.isEmpty() ? "No email found" : email);
        loadPassword(email);

        buttonUpdate.setOnClickListener(v -> {
            String newPassword = editTextPassword.getText().toString().trim();
            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentEmail = textViewEmail.getText().toString();
            if (currentEmail.equals("No email found")) {
                Toast.makeText(this, "Email not available", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PASSWORD, newPassword);

            String whereClause = DatabaseHelper.COLUMN_USER_MAIL + " = ?";
            String[] whereArgs = {currentEmail};

            int rowsAffected = dbHelper.getWritableDatabase().update(
                    DatabaseHelper.TABLE_USERS, values, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });

        buttonLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Xoá thông tin đăng nhập
            editor.apply();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(UserProfileActivity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadPassword(String email) {
        if (email.isEmpty() || email.equals("No email found")) {
            editTextPassword.setText("No password found");
            Log.d("UserProfileActivity", "No email to query password");
            return;
        }

        String[] columns = {DatabaseHelper.COLUMN_PASSWORD};
        String selection = DatabaseHelper.COLUMN_USER_MAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = dbHelper.getReadableDatabase().query(
                DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD));
            editTextPassword.setText(password);
            Log.d("UserProfileActivity", "Loaded password from DB: " + password);
        } else {
            editTextPassword.setText("No password found");
            Log.d("UserProfileActivity", "No password found for email: " + email);
        }

        cursor.close();
    }
}
