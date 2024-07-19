package com.example.testapp;

import android.os.Handler;
import android.os.Looper;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Serializable {
    private final String serverAddress;

    public Server (String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerAddress(){
        return serverAddress;
    }


    public void sendMessage(String message, String topic) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL(this.serverAddress + "/" + topic);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = message.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                conn.disconnect();

                handler.post(() -> {
                    System.out.println("Response Code: " + responseCode);
                });

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    System.out.println("Request failed.");
                });
            }
        });
    }

}
