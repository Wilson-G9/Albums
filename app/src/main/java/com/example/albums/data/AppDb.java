package com.example.albums.data;

import android.content.Context;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.albums.model.Album;
import com.example.albums.model.Song;
import java.util.concurrent.Executors;

@Database(entities = {Album.class, Song.class}, version = 1)
public abstract class AppDb extends RoomDatabase {
    private static volatile AppDb I;
    public abstract AlbumDao albumDao();
    public abstract SongDao songDao();

    public static AppDb get(Context c){
        if(I==null){
            synchronized (AppDb.class){
                if(I==null){
                    I = Room.databaseBuilder(c.getApplicationContext(), AppDb.class, "albums.db")
                            .allowMainThreadQueries()
                            .addCallback(new Callback(){
                                @Override public void onCreate(SupportSQLiteDatabase db){
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        AppDb d = get(c);
                                        // seed 3 album mẫu
                                        long a1 = d.albumDao().insert(new Album("Ballad"));
                                        long a2 = d.albumDao().insert(new Album("Rock"));
                                        long a3 = d.albumDao().insert(new Album("EDM"));
                                        d.songDao().insert(new Song("Mưa đêm", "2023-01-02", a1));
                                        d.songDao().insert(new Song("Fire Rock", "2022-05-12", a2));
                                        d.songDao().insert(new Song("Upbeat", "2024-07-09", a3));
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return I;
    }
}
