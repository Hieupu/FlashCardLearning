package com.example.flashcardlearningapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.flashcardlearningapp.Database.DatabaseHelper;

public class FlashcardDAO {
    private DatabaseHelper dbHelper;

    public FlashcardDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertFlashcard(int userId, String title, String creationTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_ID_FK, userId);
        values.put(DatabaseHelper.COLUMN_TITLE, title);
        values.put(DatabaseHelper.COLUMN_CREATION_TIME, creationTime);
        long newRowId = db.insert(DatabaseHelper.TABLE_FLASHCARD, null, values);
        db.close();
        return newRowId;
    }

    public int updateAccessCount(int flashcardId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ACCESS_COUNT, getAccessCount(flashcardId) + 1);
        String whereClause = DatabaseHelper.COLUMN_FLASHCARD_ID + "=?";
        String[] whereArgs = {String.valueOf(flashcardId)};
        int rowsAffected = db.update(DatabaseHelper.TABLE_FLASHCARD, values, whereClause, whereArgs);
        db.close();
        return rowsAffected;
    }

    public int getAccessCount(int flashcardId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_ACCESS_COUNT};
        String selection = DatabaseHelper.COLUMN_FLASHCARD_ID + "=?";
        String[] selectionArgs = {String.valueOf(flashcardId)};
        Cursor cursor = db.query(DatabaseHelper.TABLE_FLASHCARD, columns, selection, selectionArgs, null, null, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCESS_COUNT));
        }
        cursor.close();
        db.close();
        return count;
    }
    public Cursor searchFlashcardsByTitle(String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(DatabaseHelper.TABLE_FLASHCARD,
                null, DatabaseHelper.COLUMN_TITLE + " LIKE ?", new String[]{"%" + query.replaceAll(" ", "%") + "%"},
                null, null, null);
    }

    public void deleteFlashcard(int flashcardId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_FLASHCARD, DatabaseHelper.COLUMN_FLASHCARD_ID + "=?",
                new String[]{String.valueOf(flashcardId)});
        db.close();
    }

    public Cursor getAllFlashcardsCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(DatabaseHelper.TABLE_FLASHCARD,
                null, null, null, null, null, null);
    }
}