package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MessageActivity extends AppCompatActivity {
    Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        server = (Server) getIntent().getSerializableExtra("server");

        if (server != null) {
            TextView serverAddress = findViewById(R.id.serverAddress);
            serverAddress.setText(server.getServerAddress());
        }
    }

    public void sendMessage(View view) {
        EditText editText = findViewById(R.id.message);
        String message = editText.getText().toString();
        EditText topicText = findViewById(R.id.topic);
        String topic = topicText.getText().toString();
        server.sendMessage(message, topic);
        editText.setText("");
    }

    public void changeServer(View view) {
        Intent intent = new Intent(MessageActivity.this, ServerActivity.class);
        startActivity(intent);
    }

}
