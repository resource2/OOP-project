package com.example.ktvproject;

import android.app.Application;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketManager extends Application {
    private Socket socket;
    private PrintWriter out;
    private static final String SERVER_IP = "192.168.0.163"; // IP
    private static final int SERVER_PORT = 6666; // PORT

    public synchronized Socket getSocket() {
        return socket;
    }

    public synchronized PrintWriter getOut() {
        return out;
    }
    //建構
    public synchronized void setSocket(Socket socket) {
        this.socket = socket;
        try {
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //初始化socket
    public synchronized void initializeSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket == null || !socket.isConnected() || socket.isClosed()) {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT), 5000);
                        setSocket(socket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //關閉socket 目前用不到
    public synchronized void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }
}