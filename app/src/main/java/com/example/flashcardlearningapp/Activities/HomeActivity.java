package com.example.flashcardlearningapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcardlearningapp.Adapter.FlashcardAdapter;
import com.example.flashcardlearningapp.Model.Flashcard;
import com.example.flashcardlearningapp.R;
import com.example.flashcardlearningapp.ViewModel.FlashcardViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private FlashcardAdapter flashcardAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddProduct;
    private Flashcard selectedFlashcard;
    private FloatingActionButton fabAddFlashcard;
    private FlashcardViewModel flashcardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // RecyclerView setup
        recyclerView = findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        flashcardAdapter = new FlashcardAdapter(flashcard -> {
            Intent intent = new Intent(HomeActivity.this, FlashcardDetailActivity.class);
            intent.putExtra("FLASHCARD_ID", flashcard.getFlashcardId());
            startActivity(intent);
        }, (flashcard, view) -> {
            selectedFlashcard = flashcard;
            view.showContextMenu();
        });
        recyclerView.setAdapter(flashcardAdapter);

        // Register context menu
        registerForContextMenu(recyclerView);

        recyclerView.setAdapter(flashcardAdapter);

        // ViewModel setup
        flashcardViewModel = new ViewModelProvider(this).get(FlashcardViewModel.class);
        flashcardViewModel.getAllFlashcards().observe(this, flashcardAdapter::setFlashcardList);

        // FAB thêm sản phẩm
        fabAddProduct = findViewById(R.id.fab_add_product);
        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddFlashcardSetActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().equalsIgnoreCase("all")) {
                    flashcardViewModel.getAllFlashcards().observe(HomeActivity.this, flashcardAdapter::setFlashcardList);
                } else {
                    query = "%" + query.replaceAll(" ", "%") + "%";
                    flashcardViewModel.searchFlashcardsByTitle(query).observe(HomeActivity.this, flashcardAdapter::setFlashcardList);
                }
                searchView.clearFocus();
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true; // Đảm bảo menu hiển thị
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.dropdown_menu) {
            moreOption(findViewById(R.id.dropdown_menu));
            return true;
        } else if (id == R.id.user) {
            Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void moreOption(View anchor){
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "My Flashcard Set");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Log out");
        popupMenu.getMenu().add(Menu.NONE, 3, 3, "Take Quiz");

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    Toast.makeText(this, "Selected My Flashcard Set", Toast.LENGTH_SHORT).show();
                    return true;
                case 2:
                    Toast.makeText(this, "Selected Log out", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear(); // Xóa tất cả dữ liệu session
                    editor.apply();
                    Intent logoutIntent = new Intent(HomeActivity.this, Login.class);
                    startActivity(logoutIntent);
                    finish();
                    return true;
                case 3:
                    Intent quizIntent = new Intent(HomeActivity.this, QuizActivity.class);
                    startActivity(quizIntent);
                    return true;
                default:
                    return false;
            }
        });

        // Show the PopupMenu
        popupMenu.show();
    }
}