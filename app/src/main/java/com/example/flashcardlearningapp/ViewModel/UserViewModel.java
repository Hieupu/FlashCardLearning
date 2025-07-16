package com.example.flashcardlearningapp.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.flashcardlearningapp.Model.User;
import com.example.flashcardlearningapp.DAO.UserDAO;

public class UserViewModel extends AndroidViewModel {
    private UserDAO userDAO;
    private MutableLiveData<User> currentUser;

    public UserViewModel(Application application) {
        super(application);
        userDAO = new UserDAO(application.getApplicationContext());
        currentUser = new MutableLiveData<>();
    }

    public void insertUser(String email, String password) {
        new Thread(() -> {
            long id = userDAO.insertUser(email, password);
            if (id != -1) {
                User user = userDAO.getUserById((int) id);
                currentUser.postValue(user);
            }
        }).start();
    }

    public boolean checkUser(String email, String password) {
        return userDAO.checkUser(email, password);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }
}