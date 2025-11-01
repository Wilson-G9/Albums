package com.example.albums.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import com.example.albums.R;
import com.example.albums.data.AppDb;
import com.example.albums.data.SongDao;
import com.example.albums.model.Song;

public class SongEditActivity extends AppCompatActivity {
    private EditText edtTitle, edtDate;
    private SongDao dao;
    private long editingSongId = -1;
    private long albumId = -1;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_song_edit);
        dao = AppDb.get(this).songDao();

        edtTitle = findViewById(R.id.edtTitle);
        edtDate  = findViewById(R.id.edtDate);

        editingSongId = getIntent().getLongExtra(MainActivity.EXTRA_SONG_ID, -1);
        albumId = getIntent().getLongExtra(MainActivity.EXTRA_ALBUM_ID, -1);

        if (editingSongId!=-1){
            Song s = dao.findById(editingSongId);
            if(s!=null){
                edtTitle.setText(s.title);
                edtDate.setText(s.releaseDate);
                albumId = s.albumId;
            }
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String t = edtTitle.getText().toString().trim();
            String d = edtDate.getText().toString().trim();
            if(t.isEmpty()){ edtTitle.setError("Nhập tên bài hát"); return; }
            if(d.isEmpty()){ edtDate.setError("Nhập ngày"); return; }
            if(albumId==-1){ Toast.makeText(this,"Chưa chọn album",Toast.LENGTH_SHORT).show(); return; }

            if(editingSongId==-1){
                dao.insert(new Song(t, d, albumId));
            } else {
                Song s = dao.findById(editingSongId);
                if(s!=null){ s.title=t; s.releaseDate=d; dao.update(s); }
            }
            finish();
        });
    }
}
