package com.example.primaryjavalongaga;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.primaryjavalongaga.Controller.models.Hand;
import com.example.primaryjavalongaga.Controller.models.Layout;
import com.example.primaryjavalongaga.Controller.models.Player;
import com.example.primaryjavalongaga.Controller.models.Round;
import com.example.primaryjavalongaga.Controller.models.Stock;
import com.example.primaryjavalongaga.Controller.models.Tournament;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Scanner;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {

    //this executes computer move after human move

    private GameView gameView;
    private int tournamentScore;

    private TextView lastSelectedTileView;
    private GameController controller;
    private Tournament currentTournament;

    int currentPlayer = 0;
    private Round currentRound; // The "Brain"

    private Player.Move currentMove = new Player.Move(); // The "Pending Action"

    public void firstTurn()
    {
        String announceFirstPlayer = currentRound.startRound();
        Toast.makeText(this, announceFirstPlayer, Toast.LENGTH_LONG).show();


        if(currentRound.getCurrentPlayer() == 1)
        {
            currentRound.setNextPlayer(currentPlayer);

            compMove();

            currentPlayer = currentRound.getCurrentPlayer();
            currentPlayer = (currentPlayer + 1) % 2;  // will cycle 0 → 1 → 0
            currentRound.setCurrentPlayer(currentPlayer);
            refreshUI();

            currentMove = currentRound.takeTurn();
        }
        else
        {
            int nextPlayer = ((currentPlayer + 1) % 2);
            currentRound.setNextPlayer(nextPlayer);
            currentMove = currentRound.takeTurn();
        }
        refreshUI();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        controller = new GameController();
        currentRound = new Round();
        currentTournament = new Tournament();


        gameView = new GameView(this);

        //option 1
        int option = getIntent().getIntExtra("LOAD_OPTION", 0);

        //option 2
        String data = getIntent().getStringExtra("FILE_CONTENT");
        int resID = getIntent().getIntExtra("FILE_NAME", 0);



        Intent intent = getIntent();


        if(option == 1)
        {
            //option 1
            tournamentScore = intent.getIntExtra("TOURNAMENT_SCORE", 0);
            currentTournament.setTournScore(tournamentScore);
        }
        else if (option == 2 &&  getIntent().hasExtra("FILE_CONTENT")) {

            String fileContent = getIntent().getStringExtra("FILE_CONTENT");
            loadFile(fileContent);


        }

        firstTurn();



        findViewById(R.id.leftButton).setOnClickListener(v -> handleSideChoice('L'));
        findViewById(R.id.rightButton).setOnClickListener(v -> handleSideChoice('R'));
        findViewById(R.id.nextRoundButton).setOnClickListener(v -> startNextRound());


        //3. Additionally set up the other buttons as well
        findViewById(R.id.drawButton).setOnClickListener(v -> handleDraw());
        findViewById(R.id.passButton).setOnClickListener(v -> handlePass());
        findViewById(R.id.helpButton).setOnClickListener(v -> handleHelp());
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findViewById(R.id.editSaveFileName).setVisibility(View.VISIBLE);
                findViewById(R.id.enterSave).setVisibility(View.VISIBLE);

                Button saveGameButton = findViewById(R.id.enterSave);
                saveGameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText input = findViewById(R.id.editSaveFileName);

                        String fileName = input.getText().toString().trim();

                        if (fileName.isEmpty()) {
                            input.setError("Enter a tournament score");
                            return;
                        }

                        initSave(fileName);
                    }
                });

        }
        });


        findViewById(R.id.doneButton).setOnClickListener(v -> switchTurn());
        findViewById(R.id.enterSave).setVisibility(View.GONE);

        findViewById(R.id.editSaveFileName).setVisibility(View.GONE);
        findViewById(R.id.nextRoundButton).setVisibility(View.GONE);
        findViewById(R.id.doneButton).setVisibility(View.GONE);

        findViewById(R.id.nextRoundButton).setOnClickListener(v -> {
            currentMove = null;
            refreshUI();
            startNextRound();
            findViewById(R.id.nextRoundButton).setVisibility(View.GONE);

        });
        // 4. Draw the initial board

        //otherwise we will wait for the event listener

    }
    public void compMove()
    {
        currentMove = currentRound.takeTurn();
        applyMove(currentMove);
    }


    public void loadFile(String content) {
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            boolean success = currentTournament.loadGameState(reader, currentRound);
            if (success) {
                Toast.makeText(this, "Game Loaded!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String fileContent = intent.getStringExtra("FILE_CONTENT");
        if (fileContent != null) {
            // You can parse saved .txt files directly
            try (BufferedReader reader = new BufferedReader(new StringReader(fileContent))) {
                boolean success = currentTournament.loadGameState(reader, currentRound);
                if (success) {
                    Toast.makeText(this, "Game Loaded!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to parse saved file.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } */
// You would call it like this:
// loadFromRaw(R.raw.case1);

    private void applyMove(Player.Move move)
    {
        currentRound.applyMove(move);
        findViewById(R.id.doneButton).setVisibility(View.VISIBLE);

    }
    private void handlePass()
    {
        String passText = currentRound.pass(currentMove);

        if(!passText.equals(" "))
        {
            Toast.makeText(this, passText, Toast.LENGTH_SHORT).show();
            return;
        }

        applyMove(currentMove);

        refreshUI();

    }

    private void handleHelp()
    {
        String advice = currentRound.getHelp(currentMove);
        // 2. Show it to the user
        new AlertDialog.Builder(this)
                .setTitle("Tip")
                .setMessage(advice)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss(); // closes when OK is pressed
                })
                .show();
        //Toast.makeText(this, advice, Toast.LENGTH_LONG).show();

        //gameView.refreshUI(currentRound);
        refreshUI();

    }



    private void handleDraw()
    {

        String drawText = currentRound.draw(currentMove);
        if((!drawText.equals(" ")) && (!drawText.equals("unplayable")))
        {
            Toast.makeText(this, drawText, Toast.LENGTH_SHORT).show();
            return;
        }
        else if(drawText.equals("unplayable"))
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
                applyMove(currentMove);
            }
        }


        refreshUI();

        findViewById(R.id.doneButton).setVisibility(View.VISIBLE);



    }

    private void handleSideChoice(char side) {
        if (currentMove.chosenTile == null) {
            Toast.makeText(this, "Select a tile first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // STEP 2: Show the L/R buttons. NO AUTO-PLAY.
        findViewById(R.id.sideButtonsContainer).setVisibility(View.VISIBLE);

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
        applyMove(currentMove);

        refreshUI(); // Draw the new state for both players
        findViewById(R.id.sideButtonsContainer).setVisibility(View.GONE);
        findViewById(R.id.doneButton).setVisibility(View.VISIBLE);

        //switchTurn();

    }


    public void nextTurn()
    {
        currentRound.nextTurn();
    }

    private void switchTurn()
    {
        findViewById(R.id.doneButton).setVisibility(View.GONE);
        checkForRoundWinner();


        //computer turn
        nextTurn();
        refreshUI();
        currentMove = currentRound.takeTurn();
        currentRound.applyMove(currentMove);
        checkForRoundWinner();

        //human turn
        nextTurn();
        refreshUI();
        currentMove = currentRound.takeTurn();



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

        // Draw Computer Hand (NOT Clickable - use a placeholder or the tile string)
        for (String tile : currentRound.getComputerPlayer().getHandTiles()) {
            View iv = createTileView(tile);
            compHand.addView(iv); // Or tile if you want to see them
        }


        // Draw Board (Layout)
        for (String tile : currentRound.getLayout().getChain()) {
            View iv = createTileView(tile);
            board.addView(iv);
        }


        // Draw Human Hand (Clickable)
        for (String tile : currentRound.getHumanPlayer().getHandTiles()) {
            final TextView iv = createTileView(tile);
            //tv.setTextSize(12); // try 10–14 range
            iv.setOnClickListener(v -> {

                if (lastSelectedTileView != null) {
                    lastSelectedTileView.setBackgroundColor(Color.TRANSPARENT);
                    lastSelectedTileView.setTextColor(Color.BLACK);
                }

                // STEP 2: Highlight the CURRENTLY clicked tile
                iv.setBackgroundColor(Color.BLUE);
                iv.setTextColor(Color.WHITE);

                // STEP 3: Update the Controller's memory
                lastSelectedTileView = iv;

                currentMove.chosenTile = tile;

                findViewById(R.id.sideButtonsContainer).setVisibility(View.VISIBLE);

                // STEP 3: Optional - highlight the selected tile so you know it's active
                //iv.setBackgroundColor(android.graphics.Color.YELLOW);
            });
            humHand.addView(iv);
        }


        for (String tile : currentRound.getGameStock().getBoneyard()) {
            View iv = createTileView(tile);
            boneyard.addView(iv);
        }

        TextView scoreView = findViewById(R.id.scoreText);
        TextView statusView = findViewById(R.id.statusText);

        TextView tournScoreView = findViewById(R.id.tournScoreText);
        tournScoreView.setText("Required Tournament Score: " + currentTournament.getTournScore());

        int hScore = currentRound.getHumanPlayer().getScore();
        int cScore = currentRound.getComputerPlayer().getScore();
        scoreView.setText("Human score: " + hScore + " | Computer score: " + cScore);

        TextView roundText = findViewById(R.id.roundNumber);
        roundText.setText("Round No.:" + currentRound.getRoundNum());

        TextView nextPlayerText = findViewById(R.id.nextPlayer);
        nextPlayerText.setText("Next Player:" + currentRound.getPlayerByIndex(currentRound.getNextPlayer()).returnID());
        // 2. SET THE PASS STATUS
        // We check the 'passed' array in your Round class
        StringBuilder status = new StringBuilder();

        if (currentRound.isPassed(0)) {
            status.append("Human Passed! ");
        }
        if (currentRound.isPassed(1)) {
            status.append("Computer Passed! ");
        }

        if (status.length() == 0) {
            status.append("No passes yet");
        }

        statusView.setText(status.toString());


   }


    // Helper to keep code clean



/*
    private View createTileView(String tile) { // Added a 'side' check
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
                iv.setLayoutParams(new LinearLayout.LayoutParams((int)(22 * density), (int)(44 * density)));
            } else {
                // NORMAL: Wide Box, Rotate 90
                iv.setLayoutParams(new LinearLayout.LayoutParams((int)(22 * density), (int)(44 * density)));
                iv.setRotation(90); // The "Simple" Way

                // 2. THE FLIP LOGIC
                // If we are playing on the LEFT, and 'a' is what matches the board,
                // we need to spin it 180 more degrees to put 'b' on the outside.
                int boardLeft = currentRound.getLayout().returnLeft();
                /*if (isLeft && a == boardLeft) {
                    iv.setRotation(270); // 90 + 180
                }
                // If we are playing on the RIGHT, and 'b' is what matches the board...
                else if (!isLeft && b == currentRound.getLayout().returnRight()) {
                    iv.setRotation(270);
                }
            }
        }
        //iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

        return iv;
    }
    */




    private TextView createTileView(String tile) {
        TextView tv = new TextView(this);
        tv.setText(tile);
        tv.setPadding(2, 2, 2, 2);
        return tv;
    }


    private void checkForRoundWinner() {
        String winText = "";
        // 1. Check the flags from your Round class
        if (currentRound.getHumanPlayer().getHandTiles().isEmpty()) {
            Toast.makeText(this, "Human wins the round!", Toast.LENGTH_LONG).show();
            winText = currentRound.winPoints(currentRound.getHumanPlayer(), currentRound.getComputerPlayer());
            Toast.makeText(this, winText, Toast.LENGTH_LONG).show();
        } else if (currentRound.getComputerPlayer().getHandTiles().isEmpty()) {
            Toast.makeText(this, "Computer wins the round!", Toast.LENGTH_LONG).show();
            winText = currentRound.winPoints(currentRound.getComputerPlayer(), currentRound.getHumanPlayer());
            Toast.makeText(this, winText, Toast.LENGTH_LONG).show();
        } else if (currentRound.bothPassed() && currentRound.getGameStock().getBoneyard().isEmpty()) {
            Toast.makeText(this, "Game Blocked! It's a tie.", Toast.LENGTH_LONG).show();
            winText = currentRound.tiePoints(currentRound.getHumanPlayer(), currentRound.getComputerPlayer());
            Toast.makeText(this, winText, Toast.LENGTH_LONG).show();
        }
        else
        {
            refreshUI();
            return;
        }


        addScores();
        refreshUI();

        checkTournamentWinner();
        endRound();
    }

    public void addScores()
    {
        currentTournament.setHumanScore(currentRound.getHumanPlayer().getScore());
        currentTournament.setComputerScore(currentRound.getComputerPlayer().getScore());
    }

    private void checkTournamentWinner()
    {

        String winner = currentTournament.determineTournamentWinner();

        Intent intent = new Intent(this, TournamentWinnerActivity.class);


        int winnerScore = 0;

        if(winner.equals("human"))
        {
            winnerScore = currentRound.getHumanPlayer().getScore();
            intent.putExtra("PLAYER_NAME", winner);
            intent.putExtra("TOURNAMENT_SCORE", winnerScore);
            startActivity(intent);

        }
        else if(winner.equals("computer"))
        {

            winnerScore = currentRound.getComputerPlayer().getScore();
            intent.putExtra("PLAYER_NAME", winner);
            intent.putExtra("TOURNAMENT_SCORE", winnerScore);
            startActivity(intent);

        }




    }

    private void startNextRound()
    {

        findViewById(R.id.leftButton).setEnabled(true);
        findViewById(R.id.rightButton).setEnabled(true);
        findViewById(R.id.drawButton).setEnabled(true);
        findViewById(R.id.passButton).setEnabled(true);
        findViewById(R.id.helpButton).setEnabled(true);
        currentRound.resetStats();
        currentRound.nextRound();
        refreshUI();
        firstTurn();

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

    }


    public void initSave(String fileName)
    {

        File saveFolder = new File(getExternalFilesDir(null), "SavedGames");
        if (!saveFolder.exists()) saveFolder.mkdirs(); // make sure folder exists

        File saveFile = new File(saveFolder, fileName + ".txt"); // inside folder

        currentTournament.initSave(saveFolder, saveFile, currentRound.getHumanPlayer().getHand(), currentRound.getComputerPlayer().getHand(), currentRound.getGameStock(), currentRound.getLayout(), currentRound);
        ActivityCompat.finishAffinity(this); //

    }




}