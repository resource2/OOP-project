package com.example.ktvproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class setActivity extends AppCompatActivity {
    private static final String TAG = "setActivity";
    private static final int RECONNECT_INTERVAL = 5000; // 5秒

    Button btn_voiceup;
    Button btn_voicedown;
    Button btn_musicup;
    Button btn_musicdown;
    Button btn_lead;
    Button btn_rise;
    Button btn_flat;
    Button btn_mute;

    private SocketManager socketManager;
    private PrintWriter out;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set);

        btn_voiceup = findViewById(R.id.btn_voiceup);
        btn_voicedown = findViewById(R.id.btn_voicedown);
        btn_musicup = findViewById(R.id.btn_musicup);
        btn_musicdown = findViewById(R.id.btn_musicdown);
        btn_lead = findViewById(R.id.btn_lead);
        btn_rise = findViewById(R.id.btn_rise);
        btn_flat = findViewById(R.id.btn_flat);
        btn_mute = findViewById(R.id.btn_mute);
        //初始化socket
        socketManager = (SocketManager) getApplication();
        initializeSocket();

        btn_voiceup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Voice Up");
            }
        });

        btn_voicedown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Voice Down");
            }
        });

        btn_musicup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Music Up");
            }
        });

        btn_musicdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Music Down");
            }
        });

        btn_lead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Lead");
            }
        });

        btn_rise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Rise");
            }
        });

        btn_flat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Flat");
            }
        });

        btn_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Mute");
            }
        });

        // 檢查socket
        executorService.execute(checkSocketRunnable);
    }

    private void initializeSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketManager.initializeSocket(); //初始化
                    out = socketManager.getOut();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(setActivity.this, "Error initializing socket", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private Runnable checkSocketRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (socketManager.getSocket() == null || !socketManager.getSocket().isConnected() || socketManager.getSocket().isClosed()) {
                    Log.d(TAG, "Socket is disconnected, attempting to reconnect...");
                    try {
                        socketManager.initializeSocket();
                        out = socketManager.getOut();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(RECONNECT_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    //發送訊息到server
    private void sendMessage(final String message) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (out != null) {
                    out.println(message);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(setActivity.this, "Output stream is not initialized", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}