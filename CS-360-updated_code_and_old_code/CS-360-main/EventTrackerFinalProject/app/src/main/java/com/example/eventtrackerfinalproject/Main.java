package com.example.eventtrackerfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Main extends AppCompatActivity {

    private EditText username, password;
    private Button signInButton, registerButton;
    private LoginDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username_one);
        password = findViewById(R.id.password_one);
        signInButton = findViewById(R.id.sign_in_button_one);
        registerButton = findViewById(R.id.register_button);
        db = new LoginDatabase(this);

        // Log in
        signInButton.setOnClickListener(v -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean isValid = db.checkUsernamePassword(user, pass);
                if (isValid) {
                    Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, Home.class));
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Register
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
        });
    }
}
