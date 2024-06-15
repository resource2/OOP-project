package com.example.ktvproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class listActivity extends AppCompatActivity {
    ListView selectedListView;
    ArrayList<String> selectedUnits = new ArrayList<>();
    DatabaseHelper dbHelper;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        selectedListView = findViewById(R.id.selectedListView);
        dbHelper = new DatabaseHelper(this);
        //載入歌單
        loadSelectedUnits();
        //listview規劃
        adapter = new ArrayAdapter<String>(this, R.layout.list_item_selected, R.id.item_text, selectedUnits) {
            @Override
            public View getView(final int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(R.id.item_text);
                textView.setText(selectedUnits.get(position));
                View button = view.findViewById(R.id.delete_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDeleteButtonClick(position);
                    }
                });
                return view;
            }
        };

        selectedListView.setAdapter(adapter);
    }
    //listview刪除按鈕
    private void onDeleteButtonClick(int position) {
        String selectedUnit = selectedUnits.get(position);
        String[] songDetails = selectedUnit.split(" - ");
        String songName = songDetails[0];
        deleteSelectedUnit(songName);
    }
    private void deleteSelectedUnit(final String songName) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbHelper.deleteSelectedSongByName(songName);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                loadSelectedUnits();
            }
        }.execute();
    }

    private void loadSelectedUnits() {
        new AsyncTask<Void, Void, List<Song>>() {
            @Override
            protected List<Song> doInBackground(Void... voids) {
                return dbHelper.getAllSelectedSongs();
            }

            @Override
            protected void onPostExecute(List<Song> songs) {
                selectedUnits.clear();
                for (Song song : songs) {
                    selectedUnits.add(song.toString());
                }
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }
}