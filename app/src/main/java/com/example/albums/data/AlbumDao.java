package com.example.albums.data;

import androidx.room.*;
import com.example.albums.model.Album;
import java.util.List;

@Dao
public interface AlbumDao {
    @Query("SELECT * FROM albums ORDER BY name") List<Album> getAll();
    @Insert long insert(Album a);
    @Query("SELECT * FROM albums WHERE id=:id LIMIT 1") Album findById(long id);
    @Query("SELECT COUNT(*) FROM albums") int count();
}
