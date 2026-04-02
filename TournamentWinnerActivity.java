package com.example.primaryjavalongaga;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TournamentWinnerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tournament_winner); // make sure this matches your XML

        // 1. Find the TextView
        TextView resultText = findViewById(R.id.TournamentResultText);

        // 2. Get Intent extras
        Intent intent = getIntent();
        String playerName = intent.getStringExtra("PLAYER_NAME");
        int score = intent.getIntExtra("TOURNAMENT_SCORE", 0);

        // 3. Set the text
        String message = "And the winner for this tournament is "
                + playerName + " with a score of " + score;

        resultText.setText(message);
    }
}