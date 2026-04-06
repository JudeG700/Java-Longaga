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

import com.example.primaryjavalongaga.Controller.models.Player;
import com.example.primaryjavalongaga.Controller.models.Round;
import com.example.primaryjavalongaga.Controller.models.Tournament;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //this executes computer move after human move

    private GameView gameView;
    private int tournamentScore;

   // private TextView lastSelectedTileView;
    private View lastSelectedTileView;

    boolean hasFlipped = false;
    private GameController controller;
    private Tournament currentTournament;

    int currentPlayer = 0;
    private Round currentRound; // The "Brain"

    private Player.Move currentMove = new Player.Move(); // The "Pending Action"

    private void updateButtonVisibility() {

        if (lastSelectedTileView == null) {
            findViewById(R.id.leftButton).setVisibility(View.GONE);
            findViewById(R.id.rightButton).setVisibility(View.GONE);
        } else {
            findViewById(R.id.leftButton).setVisibility(View.VISIBLE);
            findViewById(R.id.rightButton).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        controller = new GameController();
        currentRound = new Round();
        currentTournament = new Tournament();


        gameView = new GameView(this);


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


        findViewById(R.id.layout).setOnClickListener(v -> {
            // If they click the background, deselect everything
            if (lastSelectedTileView != null) {
                lastSelectedTileView.setBackgroundColor(Color.TRANSPARENT);
                lastSelectedTileView.setPadding(0,0,0,0);
                lastSelectedTileView = null;
                updateButtonVisibility(); // This hides the buttons automatically!
            }
        });

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
            String announceFirstPlayer = currentRound.startRound();
            Toast.makeText(this, announceFirstPlayer, Toast.LENGTH_LONG).show();
            switchTurn();

        }
        else if (option == 2 &&  getIntent().hasExtra("FILE_CONTENT")) {

            String fileContent = getIntent().getStringExtra("FILE_CONTENT");
            loadFile(fileContent);
            String gameText = currentRound.startRound();
            refreshUI();

            if (gameText.equals("Game resumed"))
            {
                //for case 2 && 3
                checkPlayerTurn();
            }
            else
            {
                //for case 1
                switchTurn();

            }



        }


    }
    public void compMove()
    {
        currentMove = currentRound.takeTurn();
        updateMove(currentMove);
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

    private void updateMove(Player.Move move)
    {
        String moveText = currentRound.updateMove(move);
        if(currentRound.getCurrentPlayer() == 0)
        {
            Toast.makeText(this, moveText, Toast.LENGTH_SHORT).show();
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle("Reasoning for move")
                    .setMessage(moveText)
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss(); // closes when OK is pressed
                    })
                    .show();
        }
        nextTurn();
        refreshUI();
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

        updateMove(currentMove);
        //refreshUI();

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
                findViewById(R.id.leftButton).setVisibility(View.VISIBLE);
                findViewById(R.id.rightButton).setVisibility(View.VISIBLE);

            }
            else
            {
                updateMove(currentMove);
            }
        }


        //refreshUI();

        //findViewById(R.id.doneButton).setVisibility(View.VISIBLE);



    }

    private void handleSideChoice(char side) {
        if (currentMove.chosenTile == null) {
            Toast.makeText(this, "Select a tile first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // STEP 2: Show the L/R buttons. NO AUTO-PLAY.
        findViewById(R.id.leftButton).setVisibility(View.VISIBLE);
        findViewById(R.id.rightButton).setVisibility(View.VISIBLE);

        // 1. Assign the side
        currentMove.side = side;

        //public boolean checkValidity(Player.Move move, Round gameRound, int leftEnd, int rightEnd)
        String validText = currentRound.validate(currentMove);

        if(!validText.equals(" "))
        {
            Toast.makeText(this, validText, Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Human plays
        updateMove(currentMove);

        //refreshUI(); // Draw the new state for both players
        findViewById(R.id.leftButton).setVisibility(View.GONE);
        findViewById(R.id.rightButton).setVisibility(View.GONE);

        //findViewById(R.id.doneButton).setVisibility(View.VISIBLE);


    }


    public void nextTurn()
    {
        currentRound.nextTurn();
    }


    private void switchTurn()
    {
        enableButtons();

        refreshUI();
        findViewById(R.id.doneButton).setVisibility(View.GONE);

        if(checkForRoundWinner())
        {
            return;
        }
        else
        {

            checkPlayerTurn();

        }


    }
    private void checkPlayerTurn()
    {

        if(currentRound.getCurrentPlayer() == 0)
        {
            currentMove = currentRound.takeTurn();

        }
        else if(currentRound.getCurrentPlayer() == 1)
        {
            disableButtons();
            compMove();

        }

    }

    private void removeViews(LinearLayout humHand, LinearLayout compHand, LinearLayout boneyard, LinearLayout board)
    {

        humHand.removeAllViews();
        compHand.removeAllViews();
        board.removeAllViews();
        boneyard.removeAllViews();

    }

    private void addViews(LinearLayout humHand, LinearLayout compHand, LinearLayout boneyard, LinearLayout board)
    {


        for (String tile : currentRound.getHumanPlayer().getHandTiles()) {
            final View iv = createTileView(tile);
            //tv.setTextSize(12); // try 10–14 range
            iv.setOnClickListener(v -> {

                if (lastSelectedTileView != null) {
                    lastSelectedTileView.setBackgroundColor(Color.TRANSPARENT);
                    findViewById(R.id.leftButton).setVisibility(View.GONE);
                    findViewById(R.id.rightButton).setVisibility(View.GONE);
                    lastSelectedTileView.setPadding(0, 0, 0, 0);
                    //refreshUI();
                    //lastSelectedTileView.setTextColor(Color.BLACK);
                }

                // STEP 2: Highlight the CURRENTLY clicked tile
                iv.setBackgroundColor(Color.BLUE);

                int p = (int)(4 * getResources().getDisplayMetrics().density);
                iv.setPadding(p, p, p, p);

                // STEP 3: Update the Controller's memory
                lastSelectedTileView = iv;

                currentMove.chosenTile = tile;

                findViewById(R.id.leftButton).setVisibility(View.VISIBLE);
                findViewById(R.id.rightButton).setVisibility(View.VISIBLE);

                // STEP 3: Optional - highlight the selected tile so you know it's active
                //iv.setBackgroundColor(android.graphics.Color.YELLOW);
            });
            humHand.addView(iv);
        }

        for (String tile : currentRound.getComputerPlayer().getHandTiles()) {
            View iv = createTileView(tile);
            compHand.addView(iv); // Or tile if you want to see them
        }



        // Inside refreshUI() - Find the "Board" drawing loop
        List<String> chain = currentRound.getLayout().getChain();
        int connectingPip = -1; // -1 means this is the first tile in the chain

        /*for (String tile : chain) {
            // 1. Parse the current tile (e.g., "5-2")
            String trimmed = tile.trim();
            int a = trimmed.charAt(0) - '0';
            int b = trimmed.charAt(2) - '0';


            // 2. Create the view and pass the neighbor's pip
            View iv = createLayoutView(tile, connectingPip);
            board.addView(iv);

            // 3. Update connectingPip for the NEXT tile in the loop
            // Logic: If the left side (a) matched the previous tile,
            // the NEXT tile must match our right side (b).
            if (connectingPip == -1 || a == connectingPip) {
                connectingPip = b;
            } else {
                // If we had to flip to match (b was the matcher),
                // the NEXT tile must match our new "right" side (a).
                connectingPip = a;
            }
        }*/

        System.out.println(chain.size());
        for (int i = 0; i < chain.size(); i++) {
            String current = chain.get(i).replace("[", "").replace("]", "").trim();
            int a = current.charAt(0) - '0';
            int b = current.charAt(2) - '0';

            int neighborPip = -1;
            String next = "";
            // Look at the tile BEFORE this one to see what we are touching
            //if (i > 1) {
              //  next = chain.get(i + 1).replace("[", "").replace("]", "").trim();


            //}
            View iv = createLayoutView(current);
            board.addView(iv);
        }


        for (String tile : currentRound.getGameStock().getBoneyard()) {
            View iv = createTileView(tile);
            boneyard.addView(iv);
        }


    }

    private void setTexts()
    {

        TextView scoreView = findViewById(R.id.scoreText);
        TextView statusView = findViewById(R.id.statusText);
        TextView tournScoreView = findViewById(R.id.tournScoreText);
        int hScore = currentRound.getHumanPlayer().getScore();
        int cScore = currentRound.getComputerPlayer().getScore();
        TextView roundText = findViewById(R.id.roundNumber);
        TextView nextPlayerText = findViewById(R.id.nextPlayer);

        tournScoreView.setText("Required Tournament Score: " + currentTournament.getTournScore());
        scoreView.setText("Human score: " + hScore + " | Computer score: " + cScore);
        roundText.setText("Round No.:" + currentRound.getRoundNum());
        nextPlayerText.setText("Next Player:" + currentRound.getPlayerByIndex(currentRound.getNextPlayer()).returnID());


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
    private void refreshUI() {

        LinearLayout humHand = findViewById(R.id.humanHand);
        LinearLayout compHand = findViewById(R.id.computerHand);
        LinearLayout boneyard = findViewById(R.id.boneyard);
        LinearLayout board = findViewById(R.id.layout);

        removeViews(humHand, compHand, boneyard, board);
        addViews(humHand, compHand, boneyard, board);
        setTexts();


   }


    // Helper to keep code clean


    private View createLayoutView(String tile) {
        ImageView iv = new ImageView(this);
        String trimmed = tile.trim();
        int a = trimmed.charAt(0) - '0';
        int b = trimmed.charAt(2) - '0';

        // Find Resource
        String nameA = "tile_" + a + "_" + b;
        String nameB = "tile_" + b + "_" + a;
        int resID = getResources().getIdentifier(nameA, "drawable", getPackageName());
        if (resID == 0)
        {
            int secResID = getResources().getIdentifier(nameB, "drawable", getPackageName());
            if(secResID != 0)
            {
                float density = getResources().getDisplayMetrics().density;
                iv.setImageResource(secResID);
                //iv.setLayoutParams(new LinearLayout.LayoutParams((int)(44 * density), (int)(22 * density)));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(22, 44);
                int marginSize = (int)(5 * density);
                params.setMargins(marginSize, marginSize, marginSize, marginSize);

                iv.setLayoutParams(params);

                iv.setRotation(90);
                return iv;
            }
        }


        if (resID != 0) {
            float density = getResources().getDisplayMetrics().density;
            iv.setImageResource(resID);
            //iv.setLayoutParams(new LinearLayout.LayoutParams((int)(44 * density), (int)(22 * density)));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(22, 44);

            if (a == b) {
                // DOUBLE: Tall Box, No Rotation

                int marginSize = (int)(1 * density);
                params.setMargins(marginSize, marginSize, marginSize, marginSize);

                iv.setLayoutParams(params);
            } else {
                // NORMAL: Wide Box, Rotate 90

                int marginSize = (int)(5 * density);
                params.setMargins(marginSize, marginSize, marginSize, marginSize);

                iv.setLayoutParams(params);

                iv.setRotation(270);

            }



        }
        return iv;
    }


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

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(22, 44);

            if (a == b) {
                // DOUBLE: Tall Box, No Rotation

                int marginSize = (int)(1 * density);
                params.setMargins(marginSize, marginSize, marginSize, marginSize);

                iv.setLayoutParams(params);
            } else {
                // NORMAL: Wide Box, Rotate 90

                int marginSize = (int)(5 * density);
                params.setMargins(marginSize, marginSize, marginSize, marginSize);

                iv.setLayoutParams(params);

                iv.setRotation(270);

            }


        }

        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

        return iv;
    }




    /*
    private TextView createTileView(String tile) {
        TextView tv = new TextView(this);
        tv.setText(tile);
        tv.setPadding(2, 2, 2, 2);
        return tv;
    } */


    private boolean checkForRoundWinner() {
        String winText = "";
        // 1. Check the flags from your Round class
        if (currentRound.getHumanPlayer().getHandTiles().isEmpty()) {
            Toast.makeText(this, "Human wins the round!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Computer loses!", Toast.LENGTH_LONG).show();
            winText = currentRound.winPoints(currentRound.getHumanPlayer(), currentRound.getComputerPlayer());
            Toast.makeText(this, winText, Toast.LENGTH_LONG).show();
        } else if (currentRound.getComputerPlayer().getHandTiles().isEmpty()) {
            Toast.makeText(this, "Computer wins the round!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Human loses!", Toast.LENGTH_LONG).show();
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
            return false;
        }

        addScores();
        refreshUI();
        checkTournamentWinner();
        endRound();
        return true;
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
            intent.putExtra("WINNER", winner);
            intent.putExtra("LOSER", "computer");
            intent.putExtra("TOURNAMENT_SCORE", winnerScore);
            startActivity(intent);

        }
        else if(winner.equals("computer"))
        {

            winnerScore = currentRound.getComputerPlayer().getScore();
            intent.putExtra("WINNER", winner);
            intent.putExtra("LOSER", "human");
            intent.putExtra("TOURNAMENT_SCORE", winnerScore);
            startActivity(intent);

        }




    }

    private void enableButtons()
    {
        findViewById(R.id.leftButton).setEnabled(true);
        findViewById(R.id.rightButton).setEnabled(true);
        findViewById(R.id.drawButton).setEnabled(true);
        findViewById(R.id.passButton).setEnabled(true);
        findViewById(R.id.helpButton).setEnabled(true);

    }

    private void disableButtons()
    {
        findViewById(R.id.leftButton).setEnabled(false);
        findViewById(R.id.rightButton).setEnabled(false);
        findViewById(R.id.drawButton).setEnabled(false);
        findViewById(R.id.passButton).setEnabled(false);
        findViewById(R.id.helpButton).setEnabled(false);

    }
    private void startNextRound()
    {

        enableButtons();

        currentRound.resetStats();
        currentRound.nextRound();
        refreshUI();


        String emptyText = currentRound.startRound();
        refreshUI();
        checkPlayerTurn();

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