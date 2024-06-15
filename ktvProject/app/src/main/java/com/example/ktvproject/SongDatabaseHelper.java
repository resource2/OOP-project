package com.example.ktvproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SongDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SongList.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "songs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_DATE = "date";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_ARTIST + " TEXT NOT NULL, " +
                    COLUMN_DATE + " INTEGER NOT NULL);";

    public SongDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        insertDefaultSongs(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    //自訂歌單
    private void insertDefaultSongs(SQLiteDatabase db) {
        insertSong(db, new Song("deca joins", "大雨", 2021));
        insertSong(db, new Song("落日飛車", "Angel Disco Love", 2018));
        insertSong(db, new Song("Kendrick Lamar", "They not like us", 2024));
        insertSong(db, new Song("milet", "Anytime Anywhere", 2024));
        insertSong(db, new Song("溫室雜草", "水槍", 2021));
        insertSong(db, new Song("Yorushika", "花に亡霊", 2020));
        insertSong(db, new Song("Who Cares 胡凱兒", "你給過我的快樂", 2023));
        insertSong(db, new Song("露波合唱團", "我想要一台車", 2023));
        insertSong(db, new Song("Schoolgirl byebye", "軟弱", 2020));
        insertSong(db, new Song("Yuuri", "ドライフラワー", 2022));
        insertSong(db, new Song("wannasleep", "情書", 2024));
        insertSong(db, new Song("wannasleep", "前往百合花田時繞了點遠路", 2024));
        insertSong(db, new Song("wannasleep", "紅心A", 2022));
        insertSong(db, new Song("Yorushika", "言って。", 2017));
        insertSong(db, new Song("ZUTOMAYO", "Time Left", 2022));
        insertSong(db, new Song("PSY.P", "水晶球", 2022));
        insertSong(db, new Song("eill", "ここで息をして", 2021));
        insertSong(db, new Song("Yorushika", "春泥棒", 2021));
        insertSong(db, new Song("趙奕帆", "初春", 2023));
        insertSong(db, new Song("Gummy B", "SIRI說明天降雨率100%", 2022));
    }

    public void insertSong(SQLiteDatabase db, Song song) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, song.getTitle());
        values.put(COLUMN_ARTIST, song.getArtist());
        values.put(COLUMN_DATE, song.getDate());
        db.insert(TABLE_NAME, null, values);
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String artist = cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST));
                int date = cursor.getInt(cursor.getColumnIndex(COLUMN_DATE));
                songs.add(new Song(artist, name, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songs;
    }
    //搜尋歌名
    public List<Song> searchSongsByTitle(String title) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_NAME + " LIKE ?", new String[]{"%" + title + "%"}, null, null, null);
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
    //搜尋歌手
    public List<Song> searchSongsByArtist(String artist) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ARTIST + " LIKE ?", new String[]{"%" + artist + "%"}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String artistName = cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST));
                int date = cursor.getInt(cursor.getColumnIndex(COLUMN_DATE));
                songs.add(new Song(artistName, name, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }
    //照日期排序
    public List<Song> getAllSongsSortedByDate() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_DATE + " DESC");
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
