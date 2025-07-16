package com.example.flashcardlearningapp.ViewModel;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.flashcardlearningapp.DAO.FlashcardContentDAO;
import com.example.flashcardlearningapp.Database.DatabaseHelper;
import com.example.flashcardlearningapp.Model.FlashcardContent;

import java.util.ArrayList;
import java.util.List;

public class FlashcardContentViewModel extends AndroidViewModel {
    private FlashcardContentDAO flashcardContentDao;
    private MutableLiveData<List<FlashcardContent>> allContents;

    public FlashcardContentViewModel(Application application) {
        super(application);
        flashcardContentDao = new FlashcardContentDAO(application.getApplicationContext());
        allContents = new MutableLiveData<>();
    }

    public void loadContentsByFlashcardId(int flashcardId) {
        new Thread(() -> {
            List<FlashcardContent> contents = new ArrayList<>();
            Cursor cursor = flashcardContentDao.getContentByFlashcardId(flashcardId);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int contentId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTENT_ID));
                    int fId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLASHCARD_ID_FK));
                    String question = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUESTION));
                    String answer = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ANSWER));
                    contents.add(new FlashcardContent(contentId, fId, question, answer));
                } while (cursor.moveToNext());
                cursor.close();
            }
            allContents.postValue(contents);
        }).start();
    }

    public LiveData<List<FlashcardContent>> getAllContents() {
        return allContents;
    }

    public void insertContent(int flashcardId, String question, String answer) {
        new Thread(() -> {
            flashcardContentDao.insertContent(flashcardId, question, answer);
            loadContentsByFlashcardId(flashcardId);
        }).start();
    }

    public void deleteContent(int contentId) {
        new Thread(() -> {
            flashcardContentDao.deleteContent(contentId);
            loadContentsByFlashcardId(contentId);
        }).start();
    }
}
