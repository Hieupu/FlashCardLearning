package com.example.flashcardlearningapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flashcardlearningapp.Database.DatabaseHelper;
import com.example.flashcardlearningapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    private TextView textViewQuestion, textViewResult, textViewScore, textViewReview;
    private EditText editTextAnswer;
    private Button buttonSubmit, buttonReview, buttonBack;
    private Spinner spinnerFlashcardSets;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private List<FlashcardContent> flashcardContents;
    private Map<Integer, String> flashcardTitles;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int totalQuestions = 0;

    private static class FlashcardContent {
        String question;
        String answer;
        int flashcardId;

        FlashcardContent(String question, String answer, int flashcardId) {
            this.question = question;
            this.answer = answer;
            this.flashcardId = flashcardId;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", "");
        Log.d("QuizActivity", "User email: " + userEmail);

        textViewQuestion = findViewById(R.id.textViewQuestion);
        editTextAnswer = findViewById(R.id.editTextAnswer);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        textViewResult = findViewById(R.id.textViewResult);
        textViewScore = findViewById(R.id.textViewScore);
        spinnerFlashcardSets = findViewById(R.id.spinnerFlashcardSets);
        buttonReview = findViewById(R.id.buttonReview);
        textViewReview = findViewById(R.id.textViewReview);
        buttonBack = findViewById(R.id.buttonBack);

        // Load flashcard sets
        loadFlashcardSets(userEmail);
        if (flashcardTitles == null || flashcardTitles.isEmpty()) {
            textViewQuestion.setText("No flashcard sets available.");
            buttonSubmit.setEnabled(false);
        } else {
            buttonSubmit.setEnabled(false);
        }

        buttonSubmit.setOnClickListener(v -> checkAnswer());
        buttonReview.setOnClickListener(v -> showReview());
        buttonBack.setOnClickListener(v -> goBackToHome());
    }

    private void loadFlashcardSets(String userEmail) {
        flashcardContents = new ArrayList<>();
        flashcardTitles = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] userColumns = {DatabaseHelper.COLUMN_USER_ID};
        String userSelection = DatabaseHelper.COLUMN_USER_MAIL + " = ?";
        String[] userSelectionArgs = {userEmail};
        Cursor userCursor = db.query(DatabaseHelper.TABLE_USERS, userColumns, userSelection, userSelectionArgs, null, null, null);
        int userId = -1;
        if (userCursor.moveToFirst()) {
            userId = userCursor.getInt(userCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID));
            Log.d("QuizActivity", "Found userId: " + userId);
        } else {
            Log.d("QuizActivity", "No user found for email: " + userEmail);
        }
        userCursor.close();

        if (userId != -1) {
            String[] flashcardColumns = {DatabaseHelper.COLUMN_FLASHCARD_ID, DatabaseHelper.COLUMN_TITLE};
            String flashcardSelection = DatabaseHelper.COLUMN_USER_ID_FK + " = ?";
            String[] flashcardSelectionArgs = {String.valueOf(userId)};
            Cursor flashcardCursor = db.query(DatabaseHelper.TABLE_FLASHCARD, flashcardColumns, flashcardSelection, flashcardSelectionArgs, null, null, null);

            List<String> titles = new ArrayList<>();
            while (flashcardCursor.moveToNext()) {
                int flashcardId = flashcardCursor.getInt(flashcardCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLASHCARD_ID));
                String title = flashcardCursor.getString(flashcardCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
                if (title != null && !title.isEmpty()) {
                    flashcardTitles.put(flashcardId, title);
                    titles.add(title);
                }
            }
            flashcardCursor.close();

            if (titles.isEmpty()) {
                textViewQuestion.setText("No flashcard sets available.");
                buttonSubmit.setEnabled(false);
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, titles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFlashcardSets.setAdapter(adapter);

                spinnerFlashcardSets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedTitle = titles.get(position);
                        int selectedFlashcardId = getKeyByValue(flashcardTitles, selectedTitle);
                        loadFlashcardContent(selectedFlashcardId);
                        currentQuestionIndex = 0;
                        correctAnswers = 0;
                        totalQuestions = flashcardContents.size();
                        Log.d("QuizActivity", "Loaded " + totalQuestions + " questions for flashcardId: " + selectedFlashcardId);
                        if (totalQuestions > 0) {
                            displayNextQuestion();
                            buttonSubmit.setEnabled(true);
                        } else {
                            textViewQuestion.setText("No questions in this set.");
                            buttonSubmit.setEnabled(false);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        } else {
            textViewQuestion.setText("User not found.");
            buttonSubmit.setEnabled(false);
        }
        db.close();
    }

    private void loadFlashcardContent(int flashcardId) {
        flashcardContents.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] contentColumns = {DatabaseHelper.COLUMN_QUESTION, DatabaseHelper.COLUMN_ANSWER};
        String contentSelection = DatabaseHelper.COLUMN_FLASHCARD_ID_FK + " = ?";
        String[] contentSelectionArgs = {String.valueOf(flashcardId)};
        Cursor contentCursor = db.query(DatabaseHelper.TABLE_FLASHCARD_CONTENT, contentColumns, contentSelection, contentSelectionArgs, null, null, null);
        while (contentCursor.moveToNext()) {
            String question = contentCursor.getString(contentCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUESTION));
            String answer = contentCursor.getString(contentCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ANSWER));
            flashcardContents.add(new FlashcardContent(question, answer, flashcardId));
        }
        contentCursor.close();
        db.close();
        Collections.shuffle(flashcardContents);
    }

    private int getKeyByValue(Map<Integer, String> map, String value) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private void displayNextQuestion() {
        if (currentQuestionIndex < flashcardContents.size()) {
            FlashcardContent current = flashcardContents.get(currentQuestionIndex);
            textViewQuestion.setText(current.question);
            editTextAnswer.setText("");
            textViewResult.setText("");
            buttonReview.setVisibility(View.GONE);
        } else if (totalQuestions > 0) {
            textViewQuestion.setText("Quiz completed!");
            buttonSubmit.setEnabled(false);
            textViewScore.setText("Score: " + correctAnswers + "/" + totalQuestions + " (" +
                    (totalQuestions > 0 ? (correctAnswers * 100 / totalQuestions) : 0) + "%)");
            buttonReview.setVisibility(View.VISIBLE);
        } else {
            textViewQuestion.setText("No questions available.");
            buttonSubmit.setEnabled(false);
        }
    }

    private void checkAnswer() {
        String userAnswer = editTextAnswer.getText().toString().trim().toLowerCase();
        FlashcardContent current = flashcardContents.get(currentQuestionIndex);
        String correctAnswer = current.answer.trim().toLowerCase();

        if (userAnswer.equals(correctAnswer)) {
            textViewResult.setText("Correct!");
            correctAnswers++;
        } else {
            textViewResult.setText("Wrong! The correct answer is: " + current.answer);
        }

        currentQuestionIndex++;
        displayNextQuestion();
    }

    private void showReview() {
        StringBuilder reviewText = new StringBuilder("Review:\n");
        for (int i = 0; i < flashcardContents.size(); i++) {
            FlashcardContent content = flashcardContents.get(i);
            String userAnswer = (i == currentQuestionIndex - 1 && !textViewResult.getText().toString().isEmpty()) ?
                    editTextAnswer.getText().toString().trim() : "Not answered";
            boolean isCorrect = userAnswer.toLowerCase().equals(content.answer.toLowerCase());
            reviewText.append("\nQ").append(i + 1).append(": ").append(content.question)
                    .append("\nYour Answer: ").append(userAnswer)
                    .append("\nCorrect Answer: ").append(content.answer)
                    .append("\nResult: ").append(isCorrect ? "Correct" : "Wrong")
                    .append("\n------------------------");
        }
        textViewReview.setText(reviewText.toString());
        buttonReview.setVisibility(View.GONE);
        findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
        textViewReview.setVisibility(View.VISIBLE);
        buttonBack.setVisibility(View.VISIBLE); // Đảm bảo nút Back luôn hiển thị
    }

    private void goBackToHome() {
        Intent intent = new Intent(QuizActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}