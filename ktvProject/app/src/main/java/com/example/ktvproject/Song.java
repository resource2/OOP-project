package com.example.ktvproject;

public class Song {
    //歌曲物件
    private String artist;
    private String title;
    private int date;

    public Song(String artist, String title, int date) {
        this.artist = artist;
        this.title = title;
        this.date = date;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public int getDate() {
        return date;
    }
    @Override
    public String toString() {
        return title + " - " + artist + " - " + date;
    }
}
