package com.example.flashcardlearningapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.flashcardlearningapp.Database.DatabaseHelper;

public class FlashcardContentDAO {
    private DatabaseHelper dbHelper;

    public FlashcardContentDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertContent(int flashcardId, String question, String answer) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FLASHCARD_ID_FK, flashcardId);
        values.put(DatabaseHelper.COLUMN_QUESTION, question);
        values.put(DatabaseHelper.COLUMN_ANSWER, answer);
        long newRowId = db.insert(DatabaseHelper.TABLE_FLASHCARD_CONTENT, null, values);
        db.close();
        return newRowId;
    }

    public Cursor getContentByFlashcardId(int flashcardId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_CONTENT_ID, DatabaseHelper.COLUMN_QUESTION, DatabaseHelper.COLUMN_ANSWER};
        String selection = DatabaseHelper.COLUMN_FLASHCARD_ID_FK + "=?";
        String[] selectionArgs = {String.valueOf(flashcardId)};
        return db.query(DatabaseHelper.TABLE_FLASHCARD_CONTENT, columns, selection, selectionArgs, null, null, null);
    }

    public int deleteContent(int contentId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = DatabaseHelper.COLUMN_CONTENT_ID + "=?";
        String[] whereArgs = {String.valueOf(contentId)};
        int rowsAffected = db.delete(DatabaseHelper.TABLE_FLASHCARD_CONTENT, whereClause, whereArgs);
        db.close();
        return rowsAffected; // Returns number of rows deleted
    }
}