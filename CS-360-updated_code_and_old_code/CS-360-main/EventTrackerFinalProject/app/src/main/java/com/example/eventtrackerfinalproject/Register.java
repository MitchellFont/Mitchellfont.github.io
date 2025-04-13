package com.example.eventtrackerfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    private EditText username, password, confirmPassword;
    private Button registerButton, signInButton;
    private LoginDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.repassword);
        registerButton = findViewById(R.id.sign_up_button);
        signInButton = findViewById(R.id.sign_in_button);
        db = new LoginDatabase(this);

        // Register
        registerButton.setOnClickListener(v -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();
            String repass = confirmPassword.getText().toString();

            if (user.isEmpty() || pass.isEmpty() || repass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(repass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                boolean isUserExists = db.checkUsername(user);
                if (!isUserExists) {
                    boolean isInserted = db.insertData(user, pass);
                    if (isInserted) {
                        Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, Home.class));
                    } else {
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sign in
        signInButton.setOnClickListener(v -> {
            startActivity(new Intent(this, Main.class));
        });
    }
}
