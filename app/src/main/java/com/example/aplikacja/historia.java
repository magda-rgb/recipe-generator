package com.example.aplikacja;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class historia extends AppCompatActivity {

    private LinearLayout historiaLayout;
    private DatabaseReference historiaRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        historiaLayout = findViewById(R.id.historiaLayout);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        historiaRef = FirebaseDatabase.getInstance("https://zaliczenie1-9ee5a-default-rtdb.europe-west1.firebasedatabase.app/").getReference("przepis").child(userId);

        historiaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    TextView emptyText = new TextView(historia.this);
                    emptyText.setText("Brak zapisanych przepisów.");
                    recipeLayoutStyle(emptyText);
                    historiaLayout.addView(emptyText);
                    return;
                }

                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    String recipe = recipeSnapshot.getValue(String.class);
                    TextView recipeText = new TextView(historia.this);
                    recipeText.setText(recipe + "\n\n");
                    recipeLayoutStyle(recipeText);
                    historiaLayout.addView(recipeText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                TextView errorText = new TextView(historia.this);
                errorText.setText("Błąd ładowania historii");
                historiaLayout.addView(errorText);
            }
        });
    }

    private void recipeLayoutStyle(TextView view) {
        view.setTextSize(16);
        view.setPadding(0, 24, 0, 24);
    }
}
