package com.example.albums.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songs")
public class Song {
    @PrimaryKey(autoGenerate = true) public long id;
    public String title;
    public String releaseDate; // yyyy-MM-dd
    public long albumId;
    public Song(String title, String releaseDate, long albumId){
        this.title=title; this.releaseDate=releaseDate; this.albumId=albumId;
    }
}
