package com.example.flashcardlearningapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.flashcardlearningapp.DAO.FlashcardContentDAO;
import com.example.flashcardlearningapp.DAO.FlashcardDAO;
import com.example.flashcardlearningapp.DAO.UserDAO;
import com.example.flashcardlearningapp.Model.Flashcard;
import com.example.flashcardlearningapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddFlashcardSetActivity extends AppCompatActivity {

    private EditText etSetTitle;
    private LinearLayout llFlashcardContentContainer;
    private Button btnAddContent, btnSave;
    private FlashcardDAO flashcardDAO;
    private FlashcardContentDAO flashcardContentDAO;
    private UserDAO userDAO;
    private int userId; // Lấy từ session
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flashcard_set);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // Cài đặt Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Cài đặt DAO
        flashcardDAO = new FlashcardDAO(this);
        flashcardContentDAO = new FlashcardContentDAO(this);
        userDAO = new UserDAO(this);

        // Lấy userId từ session
        userId = userDAO.getUserIdByEmailFromSession(this);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo các view
        etSetTitle = findViewById(R.id.etSetTitle);
        llFlashcardContentContainer = findViewById(R.id.llFlashcardContentContainer);
        btnAddContent = findViewById(R.id.btnAddContent);
        btnSave = findViewById(R.id.btnSave);

        // Thêm một cặp question-answer trống ban đầu
        themTruongNoiDungMoi();

        // Sự kiện nhấp nút "+" để thêm nội dung
        btnAddContent.setOnClickListener(v -> themTruongNoiDungMoi());

        // Sự kiện nhấp nút lưu
        btnSave.setOnClickListener(v -> luuBoFlashcard());
    }

    private void themTruongNoiDungMoi() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.flashcard_content_item, llFlashcardContentContainer, false);
        EditText etQuestion = contentView.findViewById(R.id.etQuestion);
        EditText etAnswer = contentView.findViewById(R.id.etAnswer);
        Button btnRemoveContent = contentView.findViewById(R.id.btnRemoveContent);

        btnRemoveContent.setOnClickListener(v -> llFlashcardContentContainer.removeView(contentView));

        llFlashcardContentContainer.addView(contentView);
    }

    private void luuBoFlashcard() {
        String title = etSetTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề bộ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tự động tạo creationTime
        String creationTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Tạo và lưu flashcard với accessCount = 0
        Flashcard flashcard = new Flashcard();
        flashcard.setUserId(userId);
        flashcard.setTitle(title);
        flashcard.setCreationTime(creationTime);
        flashcard.setAccessCount(0); // Đặt mặc định là 0

        long flashcardId = flashcardDAO.insertFlashcard(flashcard.getUserId(), flashcard.getTitle(), flashcard.getCreationTime());
        if (flashcardId != -1) {
            // Chèn nội dung flashcard
            int contentCount = llFlashcardContentContainer.getChildCount();
            for (int i = 0; i < contentCount; i++) {
                View contentView = llFlashcardContentContainer.getChildAt(i);
                EditText etQuestion = contentView.findViewById(R.id.etQuestion);
                EditText etAnswer = contentView.findViewById(R.id.etAnswer);
                String question = etQuestion.getText().toString().trim();
                String answer = etAnswer.getText().toString().trim();

                if (!question.isEmpty() && !answer.isEmpty()) {
                    flashcardContentDAO.insertContent((int) flashcardId, question, answer);
                }
            }
            Toast.makeText(this, "Bộ flashcard được lưu thành công", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại HomeActivity
        } else {
            Toast.makeText(this, "Lưu bộ flashcard thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}