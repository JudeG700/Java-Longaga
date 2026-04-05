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
        String winner = intent.getStringExtra("WINNER");
        String loser = intent.getStringExtra("LOSER");

        int score = intent.getIntExtra("TOURNAMENT_SCORE", 0);

        // 3. Set the text

        StringBuilder message = new StringBuilder();

        message.append("And the winner for this tournament is ").append(winner).append(" with a score of ").append(score).append(" .");
        message.append("The loser is ").append(loser);
        resultText.setText(message);
    }
}