package com.example.flashcardlearningapp.Model;

public class FlashcardContent {
    private int contentId;
    private int flashcardId;
    private String question;
    private String answer;

    public FlashcardContent() {}

    public FlashcardContent(int contentId, int flashcardId, String question, String answer) {
        this.contentId = contentId;
        this.flashcardId = flashcardId;
        this.question = question;
        this.answer = answer;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(int flashcardId) {
        this.flashcardId = flashcardId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
