package com.example.primaryjavalongaga;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.example.primaryjavalongaga.models.Tournament;


public class MainActivity extends AppCompatActivity {

    //this executes computer move after human move

    private Tournament currentTournament;
    private Round currentRound; // The "Brain"
    private Player.Move currentMove = new Player.Move(); // The "Pending Action"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Setup the Brain
        currentRound = new Round();
        currentRound.startRound();

        // 2. Setup the "Side" Buttons ONCE
        // These just sit there waiting for a tile to be selected first
        findViewById(R.id.leftButton).setOnClickListener(v -> handleSideChoice('L'));
        findViewById(R.id.rightButton).setOnClickListener(v -> handleSideChoice('R'));

        //3. Additionally set up the other buttons as well
        findViewById(R.id.drawButton).setOnClickListener(v -> handleDraw());
        findViewById(R.id.passButton).setOnClickListener(v -> handlePass());
        findViewById(R.id.helpButton).setOnClickListener(v -> handleHelp());

        // 4. Draw the initial board
        refreshUI();
        currentRound.makeMove();

    }

    private void handlePass()
    {
        boolean validPass = currentRound.pass(currentMove);

        if(!validPass)
        {
            refreshUI();
            return;
        }

        currentRound.setCurrentPlayer(1);
        Player.Move compMove = currentRound.makeMove();
        if (compMove != null && !compMove.passed) {
            currentRound.updateMove(compMove);
        }

        // 4. Reset for next Human turn
        currentRound.setCurrentPlayer(0);
        currentMove = new Player.Move(); // Clear the selection
        refreshUI();



    }

    private void handleHelp()
    {
        String advice = currentRound.getHelp(currentMove);
        // 2. Show it to the user
        Toast.makeText(this, advice, Toast.LENGTH_LONG).show();

        refreshUI();
    }

    private void handleDraw()
    {

        boolean validDraw = currentRound.draw(currentMove);
        if(!validDraw)
        {
            return;
        }

        if(!currentMove.draw)
        {
            Toast.makeText(this, "Unable to play drawn tile. Passing", Toast.LENGTH_SHORT).show();
            handlePass();
        }
        else
        {
            if(currentMove.side == ' ')
            {
                handleSideChoice(currentMove.side);
            }
            else
            {
                currentRound.updateMove(currentMove);
            }
        }
        refreshUI();

    }

    private void handleSideChoice(char side) {
        if (currentMove.chosenTile == null) {
            Toast.makeText(this, "Select a tile first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Assign the side
        currentMove.side = side;

        //public boolean checkValidity(Player.Move move, Round gameRound, int leftEnd, int rightEnd)
        boolean isValid = currentRound.validate(currentMove);

        if(!isValid)
        {
            Toast.makeText(this, "Bad tile!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Human plays
        currentRound.updateMove(currentMove);


        determineRoundWinner(); // <--- Check after Human moves

        // 3. IMMEDIATELY switch to Computer
        currentRound.setCurrentPlayer(1);
        Player.Move compMove = currentRound.makeMove();
        if (compMove != null && !compMove.passed) {
            currentRound.updateMove(compMove);
        }

        determineRoundWinner(); // <--- Check after Computer moves as well

        // 4. Reset for next Human turn
        currentRound.setCurrentPlayer(0);
        currentMove = new Player.Move(); // Clear the selection
        findViewById(R.id.sideButtonsContainer).setVisibility(View.GONE);


        refreshUI(); // Draw the new state for both players
    }

    private void refreshUI() {
        LinearLayout humHand = findViewById(R.id.humanHand);
        LinearLayout compHand = findViewById(R.id.computerHand);
        LinearLayout boneyard = findViewById(R.id.boneyard);
        LinearLayout board = findViewById(R.id.layout);

        humHand.removeAllViews();
        compHand.removeAllViews();
        board.removeAllViews();
        boneyard.removeAllViews();



        TextView scoreView = findViewById(R.id.scoreText);
        TextView statusView = findViewById(R.id.statusText);

        // 1. SET THE SCORE
        // Pulling directly from your Round methods
        int hScore = currentRound.getHumanScore();
        int cScore = currentRound.getComputerScore();
        scoreView.setText("Score - Human: " + hScore + " | Computer: " + cScore);

        // 2. SET THE PASS STATUS
        // We check the 'passed' array in your Round class
        StringBuilder status = new StringBuilder();

        if (currentRound.isPassed(0)) {
            status.append("Human PASSED. ");
        }
        if (currentRound.isPassed(1)) {
            status.append("Computer PASSED. ");
        }

        if (status.length() == 0) {
            status.append("Game in progress...");
        }

        statusView.setText(status.toString());


        // Draw Human Hand (Clickable)
        for (String tile : currentRound.getHumanPlayer().getHandTiles()) {
            TextView tv = createTileView(tile);
            tv.setOnClickListener(v -> {
                // STEP 1: Just record what was clicked
                currentMove.chosenTile = tile;

                // STEP 2: Show the L/R buttons. NO AUTO-PLAY.
                findViewById(R.id.sideButtonsContainer).setVisibility(View.VISIBLE);

                // STEP 3: Optional - highlight the selected tile so you know it's active
                //iv.setBackgroundColor(android.graphics.Color.YELLOW);
            });
            humHand.addView(tv);
        }

        // Draw Computer Hand (NOT Clickable - use a placeholder or the tile string)
        for (String tile : currentRound.getComputerPlayer().getHandTiles()) {
            TextView tv = createTileView(tile);
            compHand.addView(tv); // Or tile if you want to see them
        }

        // Draw Board (Layout)
        for (String tile : currentRound.getLayout().getChain()) {
            TextView tv = createTileView(tile);
            board.addView(tv);
        }

        for (String tile : currentRound.getGameStock().getBoneyard()) {
            TextView tv = createTileView(tile);
            boneyard.addView(tv);
        }
   }


    // Helper to keep code clean



    /*
    private View createTileView(String tile, boolean isLeft) { // Added a 'side' check
        ImageView iv = new ImageView(this);
        String trimmed = tile.trim();
        int a = trimmed.charAt(0) - '0';
        int b = trimmed.charAt(2) - '0';

        // 1. Find the Image
        String nameA = "tile_" + a + "_" + b;
        String nameB = "tile_" + b + "_" + a;
        int resID = getResources().getIdentifier(nameA, "drawable", getPackageName());
        if (resID == 0) resID = getResources().getIdentifier(nameB, "drawable", getPackageName());

        if (resID != 0) {
            float density = getResources().getDisplayMetrics().density;
            iv.setImageResource(resID);

            if (a == b) {
                // DOUBLE: Tall Box, No Rotation
                iv.setLayoutParams(new LinearLayout.LayoutParams((int)(44 * density), (int)(88 * density)));
            } else {
                // NORMAL: Wide Box, Rotate 90
                iv.setLayoutParams(new LinearLayout.LayoutParams((int)(88 * density), (int)(44 * density)));
                iv.setRotation(90); // The "Simple" Way

                // 2. THE FLIP LOGIC
                // If we are playing on the LEFT, and 'a' is what matches the board,
                // we need to spin it 180 more degrees to put 'b' on the outside.
                int boardLeft = currentRound.getLayout().returnLeft();
                if (isLeft && a == boardLeft) {
                    iv.setRotation(270); // 90 + 180
                }
                // If we are playing on the RIGHT, and 'b' is what matches the board...
                else if (!isLeft && b == currentRound.getLayout().returnRight()) {
                    iv.setRotation(270);
                }
            }
        }
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return iv;
    }
*/

    private TextView createTileView(String tile) {
        TextView tv = new TextView(this);
        tv.setText(tile);
        tv.setPadding(20, 20, 20, 20);
        return tv;
    }


    private void determineRoundWinner() {
        // 1. Check the flags from your Round class
        if (currentRound.getHumanPlayer().getHandTiles().isEmpty()) {
            Toast.makeText(this, "Human wins the round!", Toast.LENGTH_LONG).show();
            currentRound.winPoints(currentRound.getHumanPlayer(), currentRound.getComputerPlayer());
        } else if (currentRound.getComputerPlayer().getHandTiles().isEmpty()) {
            Toast.makeText(this, "Computer wins the round!", Toast.LENGTH_LONG).show();
            currentRound.winPoints(currentRound.getComputerPlayer(), currentRound.getHumanPlayer());
        } else if (currentRound.bothPassed() && currentRound.getGameStock().getBoneyard().isEmpty()) {
            Toast.makeText(this, "Game Blocked! It's a tie.", Toast.LENGTH_LONG).show();
            currentRound.tiePoints(currentRound.getHumanPlayer(), currentRound.getComputerPlayer());
            }

        checkTournamentWinner();
        endRound();
    }

    private void checkTournamentWinner()
    {
        currentTournament.determineTournamentWinner();
    }
    private void endRound() {

        // Disable buttons so no more moves can be made
        findViewById(R.id.leftButton).setEnabled(false);
        findViewById(R.id.rightButton).setEnabled(false);
        findViewById(R.id.drawButton).setEnabled(false);
        findViewById(R.id.passButton).setEnabled(false);
        findViewById(R.id.helpButton).setEnabled(false);


        // Show a "Next Round" button (you'll need to add this to your XML)
        Button nextBtn = findViewById(R.id.nextRoundButton);
        nextBtn.setVisibility(View.VISIBLE);
        nextBtn.setOnClickListener(v -> {
            currentRound.resetStats(); // This calls your reset logic
            currentRound.startRound(); // Sets up the new engine
            refreshUI();
            nextBtn.setVisibility(View.GONE);
            findViewById(R.id.leftButton).setEnabled(true);
            findViewById(R.id.rightButton).setEnabled(true);
            findViewById(R.id.drawButton).setEnabled(true);
            findViewById(R.id.passButton).setEnabled(true);
            findViewById(R.id.helpButton).setEnabled(true);

        });
    }


}