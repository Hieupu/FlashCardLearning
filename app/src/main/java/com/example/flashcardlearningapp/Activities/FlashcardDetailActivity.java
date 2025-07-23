package com.example.flashcardlearningapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.animation.Animation;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcardlearningapp.Adapter.DetailAdapter;
import com.example.flashcardlearningapp.R;
import com.example.flashcardlearningapp.ViewModel.FlashcardContentViewModel;
import com.example.flashcardlearningapp.Model.FlashcardContent;
import java.util.List;

public class FlashcardDetailActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private FlashcardContentViewModel viewModel;
    private TextView tvCardContent;
    private TextView btnLeft, btnRight;
    private RecyclerView rvPairs;
    private DetailAdapter adapter;
    private int currentIndex = 0;
    private boolean isFront = true;
    private List<FlashcardContent> contentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_detail);

        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        tvCardContent = findViewById(R.id.tvCardContent);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        rvPairs = findViewById(R.id.rvPairs);

        // Initialize RecyclerView
        rvPairs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetailAdapter();
        rvPairs.setAdapter(adapter);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(FlashcardContentViewModel.class);

        // Get flashcardId from Intent
        int flashcardId = getIntent().getIntExtra("FLASHCARD_ID", -1);
        if (flashcardId != -1) {
            viewModel.loadContentsByFlashcardId(flashcardId);
        }

        // Observe LiveData
        viewModel.getAllContents().observe(this, new Observer<List<FlashcardContent>>() {
            @Override
            public void onChanged(List<FlashcardContent> contentList) {
                if (contentList != null && !contentList.isEmpty()) {
                    FlashcardDetailActivity.this.contentList = contentList;
                    updateCardContent(currentIndex);
                    adapter.setData(contentList);
                } else {
                    tvCardContent.setText("No content available");
                    adapter.setData(null);
                }
            }
        });

        // Set click listener for flipping card
        tvCardContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });

        // Set click listeners for navigation
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex > 0) {
                    currentIndex--;
                    updateCardContent(currentIndex);
                }
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentList != null && currentIndex < contentList.size() - 1) {
                    currentIndex++;
                    updateCardContent(currentIndex);
                }
            }
        });
    }

    private void updateCardContent(int index) {
        if (contentList != null && !contentList.isEmpty() && index >= 0 && index < contentList.size()) {
            FlashcardContent content = contentList.get(index);
            if (isFront) {
                tvCardContent.setText(content.getQuestion());
            } else {
                tvCardContent.setText(content.getAnswer());
            }
        }
    }

    private void flipCard() {
        // Horizontal flip around Y-axis
        RotateAnimation rotate = new RotateAnimation(
                0, 0,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(500);
        rotate.setFillAfter(true);
        tvCardContent.startAnimation(rotate);

        isFront = !isFront;
        updateCardContent(currentIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        return true;
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

    public void moreOption(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "My Flashcard Set");
        popupMenu.getMenu().add(Menu.NONE, 3, 3, "Log out");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Take Quiz");

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    Toast.makeText(this, "Selected My Flashcard Set", Toast.LENGTH_SHORT).show();
                    return true;
                case 3:
                    Toast.makeText(this, "Selected Log out", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent logoutIntent = new Intent(FlashcardDetailActivity.this, Login.class);
                    startActivity(logoutIntent);
                    finish();
                    return true;
                case 2:
                    Intent quizIntent = new Intent(FlashcardDetailActivity.this, QuizActivity.class);
                    startActivity(quizIntent);
                    return true;
                default:
                    return false;
            }
        });

        popupMenu.show();
    }
}