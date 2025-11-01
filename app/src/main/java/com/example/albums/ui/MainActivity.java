package com.example.albums.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.albums.R;
import com.example.albums.data.AppDb;
import com.example.albums.data.AlbumDao;
import com.example.albums.data.SongDao;
import com.example.albums.model.Album;
import com.example.albums.model.Song;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spAlbum;
    private TextView tvTitle, tvDate;
    private ListView lvSongs;
    private Button btnAdd;

    private AlbumDao albumDao;
    private SongDao songDao;

    private final ArrayList<Album> albums = new ArrayList<>();
    private final ArrayList<Song> songs = new ArrayList<>();
    private final ArrayList<String> songDisplay = new ArrayList<>();
    private ArrayAdapter<String> songAdapter;

    private long currentAlbumId = -1;

    public static final String EXTRA_SONG_ID  = "SONG_ID";
    public static final String EXTRA_ALBUM_ID = "ALBUM_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DAO
        albumDao = AppDb.get(this).albumDao();
        songDao  = AppDb.get(this).songDao();

        // Views
        spAlbum = findViewById(R.id.spAlbum);
        tvTitle = findViewById(R.id.tvTitle);
        tvDate  = findViewById(R.id.tvDate);
        lvSongs = findViewById(R.id.lvSongs);
        btnAdd  = findViewById(R.id.btnAdd);

        // Adapter cho ListView bài hát
        songAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                songDisplay);
        lvSongs.setAdapter(songAdapter);
        registerForContextMenu(lvSongs);

        // Spinner albums
        setupAlbumsSpinner();

        spAlbum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Album a = albums.get(position);
                currentAlbumId = a.id;
                tvTitle.setText("");
                tvDate.setText("");
                loadSongs();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        lvSongs.setOnItemClickListener((parent, view, position, id) -> {
            Song s = songs.get(position);
            tvTitle.setText(s.title);
            tvDate.setText(s.releaseDate);
        });

        btnAdd.setOnClickListener(v -> {
            if (currentAlbumId == -1) {
                Toast.makeText(this, "Chưa có album", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent it = new Intent(this, SongEditActivity.class);
            it.putExtra(EXTRA_ALBUM_ID, currentAlbumId);
            startActivity(it);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Sau khi thêm/sửa/xoá quay lại sẽ refresh danh sách hiện tại
        loadSongs();
    }

    private void setupAlbumsSpinner() {
        albums.clear();
        List<Album> all = albumDao.getAll();
        albums.addAll(all);

        // Dùng ArrayAdapter<Album> nhưng KHÔNG override getItem.
        // Chỉ custom text hiển thị bằng getView / getDropDownView để tránh lỗi kiểu trả về.
        ArrayAdapter<Album> albumAdapter = new ArrayAdapter<Album>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                albums
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setText(albums.get(position).name);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView v = (TextView) super.getDropDownView(position, convertView, parent);
                v.setText(albums.get(position).name);
                return v;
            }
        };

        spAlbum.setAdapter(albumAdapter);

        // Chọn album đầu nếu có
        if (!albums.isEmpty()) {
            currentAlbumId = albums.get(0).id;
        } else {
            currentAlbumId = -1;
        }
    }

    private void loadSongs() {
        if (currentAlbumId == -1) {
            songs.clear();
            songDisplay.clear();
            songAdapter.notifyDataSetChanged();
            return;
        }
        List<Song> list = songDao.byAlbum(currentAlbumId);
        songs.clear();
        songs.addAll(list);

        songDisplay.clear();
        for (Song s : songs) {
            songDisplay.add(s.title + " | " + s.releaseDate);
        }
        songAdapter.notifyDataSetChanged();
    }

    // ===== Context Menu: Thêm / Sửa / Xoá =====
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_song_ctx, menu); // mnAdd, mnEdit, mnDelete
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = info != null ? info.position : -1;
        Song selected = (pos >= 0 && pos < songs.size()) ? songs.get(pos) : null;

        int id = item.getItemId();
        if (id == R.id.mnAdd) {
            if (currentAlbumId == -1) {
                Toast.makeText(this, "Chưa có album", Toast.LENGTH_SHORT).show();
                return true;
            }
            Intent it = new Intent(this, SongEditActivity.class);
            it.putExtra(EXTRA_ALBUM_ID, currentAlbumId);
            startActivity(it);
            return true;

        } else if (id == R.id.mnEdit) {
            if (selected == null) return true;
            Intent it = new Intent(this, SongEditActivity.class);
            it.putExtra(EXTRA_SONG_ID, selected.id);
            startActivity(it);
            return true;

        } else if (id == R.id.mnDelete) {
            if (selected == null) return true;
            songDao.delete(selected);
            loadSongs();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
