package com.example.aplikacja;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText email;
    private EditText password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwdEditText);
        login = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();

        // Nasłuch na zmiany autentykacji
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.i("Uwaga", "Użytkownik jest zalogowany!");
            } else {
                Log.i("Uwaga", "Użytkownik nie jest zalogowany!");
            }
        };

        // Kliknięcie przycisku logowania
        login.setOnClickListener(v -> {
            String emailString = email.getText().toString();
            String passwordString = password.getText().toString();

            if (!emailString.isEmpty() && !passwordString.isEmpty()) {
                mAuth.signInWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Niepoprawne logowanie", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Zalogowano poprawnie", Toast.LENGTH_SHORT).show();

                                //  PRZEJŚCIE DO KOLEJNEJ AKTYWNOŚCI
                                Intent intent = new Intent(MainActivity.this, skladniki.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            } else {
                Toast.makeText(MainActivity.this, "Wprowadź email i hasło", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
