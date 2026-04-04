package com.example.primaryjavalongaga;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;




public class TournamentScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tournament_score);


        Button startGButton = findViewById(R.id.startButton);

        startGButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.editTournamentScore);

                String textInside = input.getText().toString().trim();

                if (textInside.isEmpty()) {
                    input.setError("Enter a tournament score");
                    return;
                }

                int tournamentScore = Integer.parseInt(textInside);


                if(tournamentScore < 50 || tournamentScore > 250)
                {
                    input.setError("Choose a score between the displayed range");
                    return;
                }

                Intent intent = new Intent(TournamentScoreActivity.this, MainActivity.class);
                intent.putExtra("LOAD_OPTION", 1);
                intent.putExtra("TOURNAMENT_SCORE", tournamentScore);

                startActivity(intent);
            }
        });
    }
}

