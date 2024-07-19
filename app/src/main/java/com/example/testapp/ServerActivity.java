package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerActivity extends AppCompatActivity {

    private Server server;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_server);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void connectToServer(View view) {
        EditText serverAddress = findViewById(R.id.serverText);
        String address = serverAddress.getText().toString().trim();

        if (address.isEmpty()) {
            TextView statusText = findViewById(R.id.connectionStatus);
            statusText.setText(R.string.invalid_server_address);
            return;
        }

        pingServer(address, isReachable -> {
            if (isReachable) {
                server = new Server(address);
                Intent intent = new Intent(ServerActivity.this, MessageActivity.class);
                intent.putExtra("server", server);
                startActivity(intent);
            }
        });
    }

    public interface PingCallback {
        void onPingResult(boolean isReachable);
    }

    public void pingServer(String serverAddress, PingCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                URL url = new URL(serverAddress);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                conn.disconnect();

                boolean isReachable = responseCode == HttpURLConnection.HTTP_OK;

                handler.post(() -> {
                    TextView statusText = findViewById(R.id.connectionStatus);
                    if (isReachable) {
                        statusText.setText(R.string.connection_established);
                    } else {
                        statusText.setText(R.string.server_not_reachable);
                    }
                    callback.onPingResult(isReachable);
                });

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    TextView statusText = findViewById(R.id.connectionStatus);
                    statusText.setText(R.string.request_failed);
                    callback.onPingResult(false);
                });
            } finally {
                executor.shutdown();
            }
        });
    }
}
