package com.example.albums.data;

import androidx.room.*;
import com.example.albums.model.Song;
import java.util.List;

@Dao
public interface SongDao {
    @Query("SELECT * FROM songs WHERE albumId=:albumId ORDER BY id DESC")
    List<Song> byAlbum(long albumId);
    @Insert long insert(Song s);
    @Update void update(Song s);
    @Delete void delete(Song s);
    @Query("SELECT * FROM songs WHERE id=:id LIMIT 1")
    Song findById(long id);
}
