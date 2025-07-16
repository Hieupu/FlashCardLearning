package com.example.flashcardlearningapp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FlashcardDB";
    private static final int DATABASE_VERSION = 1;

    // Table for user accounts with Google login
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_MAIL = "user_mail";
    public static final String COLUMN_PASSWORD = "password";

    // Table for flashcard metadata
    public static final String TABLE_FLASHCARD = "flashcard";
    public static final String COLUMN_FLASHCARD_ID = "flashcard_id";
    public static final String COLUMN_USER_ID_FK = "user_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CREATION_TIME = "creation_time";
    public static final String COLUMN_ACCESS_COUNT = "access_count";

    // Table for flashcard questions and answers
    public static final String TABLE_FLASHCARD_CONTENT = "flashcard_content";
    public static final String COLUMN_CONTENT_ID = "content_id";
    public static final String COLUMN_FLASHCARD_ID_FK = "flashcard_id";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_ANSWER = "answer";

    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USER_MAIL + " TEXT NOT NULL UNIQUE, " +
            COLUMN_PASSWORD + " TEXT NOT NULL)";

    private static final String CREATE_FLASHCARD_TABLE = "CREATE TABLE " + TABLE_FLASHCARD + " (" +
            COLUMN_FLASHCARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USER_ID_FK + " INTEGER, " +
            COLUMN_TITLE + " TEXT NOT NULL, " +
            COLUMN_CREATION_TIME + " TEXT, " +
            COLUMN_ACCESS_COUNT + " INTEGER DEFAULT 0, " +
            "FOREIGN KEY (" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

    private static final String CREATE_FLASHCARD_CONTENT_TABLE = "CREATE TABLE " + TABLE_FLASHCARD_CONTENT + " (" +
            COLUMN_CONTENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FLASHCARD_ID_FK + " INTEGER, " +
            COLUMN_QUESTION + " TEXT NOT NULL, " +
            COLUMN_ANSWER + " TEXT, " +
            "FOREIGN KEY (" + COLUMN_FLASHCARD_ID_FK + ") REFERENCES " + TABLE_FLASHCARD + "(" + COLUMN_FLASHCARD_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_FLASHCARD_TABLE);
        db.execSQL(CREATE_FLASHCARD_CONTENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASHCARD_CONTENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASHCARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}