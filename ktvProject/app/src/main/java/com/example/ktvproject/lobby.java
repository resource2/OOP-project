package com.example.ktvproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.Socket;

public class lobby extends AppCompatActivity {
    Button btn_call;
    Button btn_set;
    Button btn_rank;
    Button btn_list;
    Button btn_singer;
    Button btn_songs;
    private SocketManager socketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);

        socketManager = (SocketManager) getApplication();
        socketManager.initializeSocket();

        btn_call = findViewById(R.id.buttonBottomLeft);
        btn_set = findViewById(R.id.buttonBottomRight);
        btn_rank = findViewById(R.id.buttonTopLeft);
        btn_list = findViewById(R.id.buttonTopRight);
        btn_songs = findViewById(R.id.buttonMiddleLeft);
        btn_singer = findViewById(R.id.buttonMiddleRight);

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(lobby.this, callActivity.class);
                startActivity(i);
            }
        });

        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(lobby.this, setActivity.class);
                startActivity(i);
            }
        });

        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(lobby.this, listActivity.class);
                startActivity(i);
            }
        });

        btn_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(lobby.this, rankActivity.class);
                startActivity(i);
            }
        });

        btn_songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(lobby.this, songsActivity.class);
                startActivity(i);
            }
        });

        btn_singer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(lobby.this, singerActivity.class);
                startActivity(i);
            }
        });
    }
}
