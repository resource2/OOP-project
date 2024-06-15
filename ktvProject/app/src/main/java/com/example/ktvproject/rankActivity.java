package com.example.ktvproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class rankActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> songList;
    SongDatabaseHelper songsDbHelper;
    DatabaseHelper selectedSongsDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank);

        listView = findViewById(R.id.listview);
        songList = new ArrayList<>();
        //已點歌曲和歌曲清單資料庫
        songsDbHelper = new SongDatabaseHelper(this);
        selectedSongsDbHelper = new DatabaseHelper(this);

        loadAllSongs();
        //建立listview
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.item_text, songList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(R.id.item_text);
                textView.setText(songList.get(position));
                View button = view.findViewById(R.id.insert_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onInsertButtonClick(v);
                    }
                });
                return view;
            }
        };
        listView.setAdapter(adapter);
        //點選文本點播
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedUnit = songList.get(i);
                String[] songDetails = selectedUnit.split(" - ");
                Song song = new Song(songDetails[1], songDetails[0], Integer.parseInt(songDetails[2]));
                saveSelectedUnit(song, false);
                Toast.makeText(rankActivity.this, "點播 " + selectedUnit, Toast.LENGTH_SHORT).show();
            }
        });
    }
    //點選按鈕插播
    public void onInsertButtonClick(View view) {
        View parentView = (View) view.getParent();
        TextView textView = parentView.findViewById(R.id.item_text);
        String buttonText = textView.getText().toString();
        String[] buttonTextDetails = buttonText.split(" - ");
        Song song = new Song(buttonTextDetails[1], buttonTextDetails[0], Integer.parseInt(buttonTextDetails[2]));
        saveSelectedUnit(song, true);
        Toast.makeText(rankActivity.this, "插播 " + buttonText, Toast.LENGTH_SHORT).show();
    }

    private void saveSelectedUnit(final Song song, final boolean isInsertAtTop) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                selectedSongsDbHelper.saveSelectedSong(song, isInsertAtTop);
                return null;
            }
        }.execute();
    }

    private void loadAllSongs() {
        new AsyncTask<Void, Void, List<Song>>() {
            @Override
            protected List<Song> doInBackground(Void... voids) {
                return songsDbHelper.getAllSongsSortedByDate();
            }

            @Override
            protected void onPostExecute(List<Song> songs) {
                songList.clear();
                for (Song song : songs) {
                    songList.add(song.toString());
                }
                ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
            }
        }.execute();
    }
}