package com.example.flashcardlearningapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
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

    private static final String TAG = "AddFlashcardSetActivity";
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
        Log.d(TAG, "UserId retrieved: " + userId);

        // Khởi tạo các view
        etSetTitle = findViewById(R.id.etSetTitle);
        llFlashcardContentContainer = findViewById(R.id.llFlashcardContentContainer);
        btnAddContent = findViewById(R.id.btnAddContent);
        btnSave = findViewById(R.id.btnSave);

        // Thêm một cặp question-answer trống ban đầu
        themTruongNoiDungMoi();

        // Sự kiện nhấp nút "Thêm"
        btnAddContent.setOnClickListener(v -> {
            themTruongNoiDungMoi();
            Log.d(TAG, "Added new question-answer pair");
        });

        // Sự kiện nhấp nút "Lưu"
        btnSave.setOnClickListener(v -> luuBoFlashcard());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        return true; // Đảm bảo menu hiển thị
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.dropdown_menu) {
            moreOption(findViewById(R.id.dropdown_menu));
            return true;
        } else if (id == R.id.user) {
            Toast.makeText(this, "Bạn đã chọn thông tin người dùng", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void moreOption(View anchor){
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "My Profile");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "My Flashcard Set");
        popupMenu.getMenu().add(Menu.NONE, 3, 3, "Log out");

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    Toast.makeText(this, "Selected My Profile", Toast.LENGTH_SHORT).show();
                    return true;
                case 2:
                    Toast.makeText(this, "Selected My Flashcard Set", Toast.LENGTH_SHORT).show();
                    return true;
                case 3:
                    Toast.makeText(this, "Selected Log out", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear(); // Xóa tất cả dữ liệu session
                    editor.apply();
                    Intent intent = new Intent(AddFlashcardSetActivity.this, Login.class);
                    startActivity(intent);
                    return true;
                default:
                    return false;
            }
        });

        // Show the PopupMenu
        popupMenu.show();
    }

    private void themTruongNoiDungMoi() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.flashcard_content_item, llFlashcardContentContainer, false);
        EditText etQuestion = contentView.findViewById(R.id.etQuestion);
        EditText etAnswer = contentView.findViewById(R.id.etAnswer);
        Button btnRemoveContent = contentView.findViewById(R.id.btnRemoveContent);

        btnRemoveContent.setOnClickListener(v -> {
            llFlashcardContentContainer.removeView(contentView);
            Log.d(TAG, "Removed question-answer pair");
        });

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