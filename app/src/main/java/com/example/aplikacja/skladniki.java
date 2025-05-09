package com.example.aplikacja;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class skladniki extends AppCompatActivity {

    private FirebaseDatabase database;
    private String userId;

    private EditText ingredientsEditText;
    private Button generateButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skladniki);

        // Inicjalizacja Firebase Database (z URL)
        database = FirebaseDatabase.getInstance("https://zaliczenie1-9ee5a-default-rtdb.europe-west1.firebasedatabase.app/");

        // Pobierz bieżącego użytkownika
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(this, "Nie zalogowano użytkownika!", Toast.LENGTH_LONG).show();
            finish(); // zakończ aktywność, bo nie mamy userId
            return;
        }

        // Inicjalizacja widoków
        ingredientsEditText = findViewById(R.id.ingredientsEditText);
        generateButton = findViewById(R.id.generateButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Przycisk generowania przepisu
        generateButton.setOnClickListener(v -> {
            String ingredients = ingredientsEditText.getText().toString().trim();

            if (ingredients.isEmpty()) {
                Toast.makeText(this, "Wpisz składniki", Toast.LENGTH_SHORT).show();
                return;
            }

            resultTextView.setText("Generowanie przepisu...");

            // Wywołanie metody getRecipe z klasy zapytanie
            zapytanie.getRecipe(ingredients, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> resultTextView.setText("Błąd: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String content = jsonObject.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");

                            runOnUiThread(() -> resultTextView.setText(content));
                            saveRecipeToFirebase(content);

                        } catch (JSONException e) {
                            runOnUiThread(() -> resultTextView.setText("Błąd JSON: " + e.getMessage()));
                        }
                    } else {
                        runOnUiThread(() -> resultTextView.setText("Błąd: " + response.message()));
                    }
                }
            });
        });

        // Przycisk przejścia do historiiii
        Button historyButton = findViewById(R.id.historiaButton);
        historyButton.setOnClickListener(v -> {
            startActivity(new Intent(this, historia.class));
        });
    }

    private void saveRecipeToFirebase(String recipeContent) {
        DatabaseReference myRef = database.getReference("przepis").child(userId).push();
        myRef.setValue(recipeContent).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Przepis zapisany do historii!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Firebase", "Błąd zapisu: ", task.getException());
                Toast.makeText(this, "Błąd zapisu przepisu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
