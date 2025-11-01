package com.example.albums.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "albums")
public class Album {
    @PrimaryKey(autoGenerate = true) public long id;
    public String name;
    public Album(String name){ this.name = name; }
}
