package com.example.primaryjavalongaga;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.primaryjavalongaga.models.Computer;
import com.example.primaryjavalongaga.models.Hand;
import com.example.primaryjavalongaga.models.Human;
import com.example.primaryjavalongaga.models.Layout;
import com.example.primaryjavalongaga.models.LayoutView;
import com.example.primaryjavalongaga.models.Player;
import com.example.primaryjavalongaga.models.Round;
import com.example.primaryjavalongaga.models.Stock;


public class MainActivity extends AppCompatActivity {

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

        TextView gameLog;
        gameLog = findViewById(R.id.gameLogTextView);


        //initialize classes
        Round currentRound = new Round();

        Intent intent = getIntent();
        //if(players[0].getHandTiles().isEmpty() && players[1].getHandTiles().isEmpty())
        //{
            //deal first
          //  players[0].setTiles(gameStock.dealTiles());
            //players[1].setTiles(gameStock.dealTiles());
        //}

        currentRound.startRound();

        LinearLayout humHand = findViewById(R.id.humanHand);

        for (String domino : currentRound.getHumanPlayer().getHandTiles()) {
            TextView dominoView = new TextView(this);
            dominoView.setText(domino); // "3-4", "6-6", etc.
            dominoView.setTextSize(18);   // make it bigger
            dominoView.setPadding(16, 16, 16, 16); // space around the domino

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0); // small spacing between dominoes
            dominoView.setLayoutParams(params);

            humHand.addView(dominoView);

            dominoView.setOnClickListener(v -> {
                // handle domino selection here
                Toast.makeText(this, "You selected: " + domino, Toast.LENGTH_SHORT).show();
            });
        }

        LinearLayout compHand = findViewById(R.id.computerHand);

        for (String domino : currentRound.getComputerPlayer().getHandTiles()) {
            TextView dominoView = new TextView(this);
            dominoView.setText(domino); // "3-4", "6-6", etc.
            dominoView.setTextSize(18);   // make it bigger
            dominoView.setPadding(16, 16, 16, 16); // space around the domino

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0); // small spacing between dominoes
            dominoView.setLayoutParams(params);

            compHand.addView(dominoView);

            dominoView.setOnClickListener(v -> {
                // handle domino selection here
                Toast.makeText(this, "You selected: " + domino, Toast.LENGTH_SHORT).show();
            });
        }

        /*int leftEnd = 0;
        int rightEnd = 0;
        if(currentRound.getLayout().getChain().isEmpty())
        {
            //obtain engine and set left and right ends
            String engine = currentRound.obtainEngine();
            currentRound.getLayout().addRight(engine);
            leftEnd = engine.charAt(0) - '0';
            rightEnd = engine.charAt(2) - '0';
        } */



        //get the player's choice
        int action = intent.getIntExtra("PLAYER_CHOICE", 1);
        //setContentView(R.layout.tournament_score); // must come first!


        LinearLayout boneyard = findViewById(R.id.boneyard);

        for (String domino : currentRound.getGameStock().getBoneyard()) {
            TextView dominoView = new TextView(this);
            dominoView.setText(domino); // "3-4", "6-6", etc.
            dominoView.setTextSize(18);   // make it bigger
            dominoView.setPadding(16, 16, 16, 16); // space around the domino

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0); // small spacing between dominoes
            dominoView.setLayoutParams(params);

            boneyard.addView(dominoView);

            dominoView.setOnClickListener(v -> {
                // handle domino selection here
                Toast.makeText(this, "You selected: " + domino, Toast.LENGTH_SHORT).show();
            });
        }




    }
}