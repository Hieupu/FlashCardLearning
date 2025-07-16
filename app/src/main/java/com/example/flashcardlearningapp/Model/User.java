package com.example.flashcardlearningapp.Model;

public class User {
    private int userId;
    private String userMail;
    private String password;

    public User() {
    }

    public User(int userId, String userMail, String password) {
        this.userId = userId;
        this.userMail = userMail;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
