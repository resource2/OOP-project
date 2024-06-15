package com.example.ktvproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    //已點歌曲資料庫
    private static final String DATABASE_NAME = "SelectedSongs.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "selected_songs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_POSITION = "position";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_ARTIST + " TEXT NOT NULL, " +
                    COLUMN_DATE + " INTEGER NOT NULL, " +
                    COLUMN_POSITION + " INTEGER);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    //加入歌曲後儲存已點歌曲清單
    public void saveSelectedSong(Song song, boolean isInsertAtTop) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, song.getTitle());
        values.put(COLUMN_ARTIST, song.getArtist());
        values.put(COLUMN_DATE, song.getDate());

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT MAX(" + COLUMN_POSITION + ") FROM " + TABLE_NAME, null);
            int maxPosition = 0;
            if (cursor != null && cursor.moveToFirst()) {
                maxPosition = cursor.getInt(0);
            }

            if (isInsertAtTop) {
                db.execSQL("UPDATE " + TABLE_NAME + " SET " + COLUMN_POSITION + " = " + COLUMN_POSITION + " + 1");
                values.put(COLUMN_POSITION, 0);
            } else {
                values.put(COLUMN_POSITION, maxPosition + 1);
            }

            db.insert(TABLE_NAME, null, values);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
    }
    //刪除同名已點歌曲
    public void deleteSelectedSongByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_NAME + "=?", new String[]{name});
        db.close();
    }
    //get
    public List<Song> getAllSelectedSongs() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_POSITION + " ASC");
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String artist = cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST));
                int date = cursor.getInt(cursor.getColumnIndex(COLUMN_DATE));
                songs.add(new Song(artist, name, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }
}