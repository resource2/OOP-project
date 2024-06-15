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

public class callActivity extends AppCompatActivity {
    private static final String TAG = "callActivity";
    private static final int RECONNECT_INTERVAL = 5000; // 5秒

    Button btnOrder;
    Button btnNoPower;
    Button btnWater;
    Button btnOther;
    Button btnCheckout;

    private SocketManager socketManager;
    private PrintWriter out;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);
        //各種按鈕
        btnOrder = findViewById(R.id.btn_order);
        btnNoPower = findViewById(R.id.btn_nopower);
        btnWater = findViewById(R.id.btn_water);
        btnOther = findViewById(R.id.btn_other);
        btnCheckout = findViewById(R.id.btn_checkout);
        //初始化socket
        socketManager = (SocketManager) getApplication();
        initializeSocket();

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Order");
            }
        });

        btnNoPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("No Power");
            }
        });

        btnWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Water");
            }
        });

        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Other");
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Checkout");
            }
        });

        // 定期檢查Socket
        executorService.execute(checkSocketRunnable);
    }

    private void initializeSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketManager.initializeSocket();
                    out = socketManager.getOut();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(callActivity.this, "Error initializing socket", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
    //檢查socket
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
                            Toast.makeText(callActivity.this, "Output stream is not initialized", Toast.LENGTH_SHORT).show();
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

