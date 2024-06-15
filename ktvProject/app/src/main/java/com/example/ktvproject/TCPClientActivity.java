package com.example.ktvproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClientActivity extends AppCompatActivity {
    private Thread mThread = null; //線程
    private EditText etIP, etPort;
    private TextView tvMessages;
    private EditText etMessage;
    private String SERVER_IP;
    private int SERVER_PORT;

    //socket的輸入與輸出
    private BufferedReader reader;
    private Socket socket;
    private String tmp;
    private PrintWriter out;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tcp_client);

        etIP = findViewById(R.id.etIP);
        etPort = findViewById(R.id.etPort);
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        Button btnSend = findViewById(R.id.btnSend);
        Button btnConnect = findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //獲得IP
                tvMessages.setText("");
                SERVER_IP = etIP.getText().toString();
                SERVER_PORT = Integer.parseInt(etPort.getText().toString());
                //啟動線程
                mThread = new Thread(new clientThread());
                mThread.start();
            }
        });
        //發送按鈕
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = etMessage.getText().toString();
                if (!message.isEmpty()) {
                    new Thread(new SendData(message)).start();
                }
            }
        });
    }
    //Client連接線程
    class clientThread implements Runnable {
        public void run() {
            try {
                //利用socket去連接
                socket = new Socket(SERVER_IP, SERVER_PORT);
                if (socket.isConnected()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvMessages.setText("Connected to server\n"); //連接成功發送訊息給server
                            ((SocketManager) getApplication()).setSocket(socket); // save socket
                            //連接成功後就跳入點歌大廳
                            Intent i = new Intent(TCPClientActivity.this, lobby.class);
                            startActivity(i);
                        }
                    });
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    while ((tmp = reader.readLine()) != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvMessages.append("收 " + socket.getInetAddress().getHostAddress() + ": " + tmp + "\n");
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMessages.setText("Connection error\n");
                    }
                });
            }
        }
    }
    //發送線程
    class SendData implements Runnable {
        private String message;
        SendData(String message) {
            this.message = message;
        }
        @Override
        public void run() {
            out.println(message);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMessages.append("發 "+socket.getLocalAddress().getHostAddress() +": " + message + "\n");
                    etMessage.setText("");
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }
}