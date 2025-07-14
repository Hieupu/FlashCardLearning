package com.example.flashcardlearningapp.ViewModel;

import android.app.Application;
import android.database.Cursor;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.flashcardlearningapp.DAO.FlashcardDAO;
import com.example.flashcardlearningapp.Database.DatabaseHelper;
import com.example.flashcardlearningapp.Model.Flashcard;
import com.example.flashcardlearningapp.DAO.UserDAO;

import java.util.ArrayList;
import java.util.List;

public class FlashcardViewModel extends AndroidViewModel {
    private FlashcardDAO flashcardDAO;
    private UserDAO userDAO;
    private MutableLiveData<List<Flashcard>> allFlashcards;

    public FlashcardViewModel(Application application) {
        super(application);
        flashcardDAO = new FlashcardDAO(application.getApplicationContext());
        userDAO = new UserDAO(application.getApplicationContext());
        allFlashcards = new MutableLiveData<>();
        loadAllFlashcards();
    }

    private void loadAllFlashcards() {
        new Thread(() -> {
            List<Flashcard> flashcards = new ArrayList<>();
            Cursor cursor = flashcardDAO.getAllFlashcardsCursor();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int flashcardId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLASHCARD_ID));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID_FK));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
                    String creationTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATION_TIME));
                    int accessCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCESS_COUNT));
                    Flashcard flashcard = new Flashcard(flashcardId, userId, title, creationTime, accessCount, getApplication().getApplicationContext());
                    flashcards.add(flashcard);
                } while (cursor.moveToNext());
                cursor.close();
            }
            allFlashcards.postValue(flashcards);
        }).start();
    }

    public LiveData<List<Flashcard>> getAllFlashcards() {
        return allFlashcards;
    }

    public void insertFlashcard(int userId, String title, String creationTime) {
        new Thread(() -> {
            long id = flashcardDAO.insertFlashcard(userId, title, creationTime);
            if (id != -1) {
                loadAllFlashcards(); // Refresh list
            }
        }).start();
    }

    public void deleteFlashcard(Flashcard flashcard) {
        new Thread(() -> {
            flashcardDAO.deleteFlashcard(flashcard.getFlashcardId());
            loadAllFlashcards(); // Refresh list
        }).start();
    }

    public LiveData<List<Flashcard>> searchFlashcardsByTitle(String query) {
        MutableLiveData<List<Flashcard>> searchResults = new MutableLiveData<>();
        new Thread(() -> {
            List<Flashcard> flashcards = new ArrayList<>();
            Cursor cursor = flashcardDAO.searchFlashcardsByTitle(query);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int flashcardId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLASHCARD_ID));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID_FK));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
                    String creationTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATION_TIME));
                    int accessCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCESS_COUNT));
                    Flashcard flashcard = new Flashcard(flashcardId, userId, title, creationTime, accessCount, getApplication().getApplicationContext());
                    flashcards.add(flashcard);
                } while (cursor.moveToNext());
                cursor.close();
            }
            searchResults.postValue(flashcards);
        }).start();
        return searchResults;
    }
}