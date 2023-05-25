package com.example.androidtvremote.logic;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;


public class ATVRemote {
    public static final int PORT = 6466;
    private final String ip;
    private final SSLContext sslContext;
    private OutputStream socketOutputStream;
    private boolean isConnected;

    public ATVRemote(SSLContext sslContext, String ip) {
        this.ip = ip;
        this.sslContext = sslContext;
        this.isConnected = false;
    }

    private void connect(Runnable callback) {
        new Thread(() -> {
            try {
                if (isConnected) {
                    callback.run();
                    return;
                }
                SSLSocket socket = (SSLSocket) sslContext.getSocketFactory().createSocket(ip, PORT);
                socket.setSoTimeout(60000);
                socket.startHandshake();
                this.socketOutputStream = socket.getOutputStream();
                socketOutputStream.write((new byte[]{92, 10, 90, 8, (byte) 239, 4, 18, 85, 10, 10, 83, 69, 73, 45, 83, 57, 48, 53, 88, 50, 18, 12, 83, 69, 73, 32, 82, 111, 98, 111, 116, 105, 99, 115, 24, 1, 34, 2, 49, 48, 42, 36, 99, 111, 109, 46, 103, 111, 111, 103, 108, 101, 46, 97, 110, 100, 114, 111, 105, 100, 46, 116, 118, 46, 114, 101, 109, 111, 116, 101, 46, 115, 101, 114, 118, 105, 99, 101, 50, 13, 53, 46, 50, 46, 52, 55, 51, 50, 53, 52, 49, 51, 51}));
                //socketOutputStream.write((new byte[]{5,18, 3, 8, (byte) 238, 4}));
                InputStream read = socket.getInputStream();
                int length = read.read();
                for (int i = 0; i < length; i++) {
                    Log.e("", read.read() + "");
                    if (i == length - 1) {
                        Log.e("ATVRemote", "connected");
                        isConnected = true;
                        callback.run();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void execute(MainMVP.Presenter presenter) {
        connect(() -> presenter.ATVRemoteFinished(this));
    }

    public void volume(MotionEvent event, byte code) {
        connect(() -> {
            try {
                byte[] msg = {6, 82, 4, 8, code, 16, 1};
                byte[] msg2 = {6, 82, 4, 8, code, 16, 2};
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.e("MainActivity", "down");
                    socketOutputStream.write(msg);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    socketOutputStream.write((msg2));
                }
            } catch (IOException ignored) {
                isConnected = false;
                volume(event, code);
            }
        });
    }

    public void sendCommand(byte code) {
        connect(() -> {
            try {
                if (code == (byte) KeyEvent.KEYCODE_CHANNEL_UP || code == (byte) KeyEvent.KEYCODE_CHANNEL_DOWN) {
                    //socketOutputStream.write(new byte[]{7});
                    socketOutputStream.write((new byte[]{7, 82, 5, 8, code, 1, 16, 3}));
                } else {
                    socketOutputStream.write((new byte[]{6, 82, 4, 8, code, 16, 1}));
                    socketOutputStream.write((new byte[]{6, 82, 4, 8, code, 16, 2}));
                }
                Log.e("ATVRemote", "sent");
            } catch (IOException ignored) {
                isConnected = false;
                sendCommand(code);
            }
        });
    }

    public void openApp(String appPackage) {
        byte[] content = appPackage.getBytes(StandardCharsets.UTF_8);
        connect(() -> {
            byte[] header = {(byte) 210, 5, (byte) (content.length + 2), 10, (byte) content.length};
            try {
                socketOutputStream.write(new byte[]{(byte) (header.length + content.length)});
                socketOutputStream.write(header);
                socketOutputStream.write(content);
            } catch (IOException e) {
                isConnected = false;
                openApp(appPackage);
            }
        });
    }
}
