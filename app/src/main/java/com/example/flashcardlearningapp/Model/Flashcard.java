package com.example.flashcardlearningapp.Model;

import android.content.Context;

import com.example.flashcardlearningapp.DAO.UserDAO;

public class Flashcard {
    private int flashcardId;
    private int userId;
    private String title;
    private String creationTime;
    private int accessCount;
    private Context context; // To access UserDao

    public Flashcard() {}

    public Flashcard(int flashcardId, int userId, String title, String creationTime, int accessCount, Context context) {
        this.flashcardId = flashcardId;
        this.userId = userId;
        this.title = title;
        this.creationTime = creationTime;
        this.accessCount = accessCount;
        this.context = context;
    }

    public int getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(int flashcardId) {
        this.flashcardId = flashcardId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }

    public String getCreatorName() {
        UserDAO userDao = new UserDAO(context);
        User user = userDao.getUserById(userId); // Implement this method
        return user != null ? user.getUserMail() : "Unknown User";
    }
}