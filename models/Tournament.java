package com.example.primaryjavalongaga.models;

import android.util.Log;

public class Tournament {

    private static final String TAG = "Tournament";

    private static int humanScore;
    private static int computerScore;
    private int targetScore;

    private Round currentRound = new Round();

    public static final int STARTING_SCORE = 0;

    public Tournament() {
        humanScore = STARTING_SCORE;
        computerScore = STARTING_SCORE;
        targetScore = STARTING_SCORE;
    }

    // Add points to human player
    public void addHumanScore(int points) {
        humanScore += points;
        Log.i(TAG, "Human score: " + humanScore);
    }

    // Add points to computer player
    public void addComputerScore(int points) {
        computerScore += points;
        Log.i(TAG, "Computer score: " + computerScore);
    }

    public static int getHumanScore() { return humanScore; }
    public static int getComputerScore() { return computerScore; }

    public int getTournScore() { return targetScore; }
    public void setTournScore(int targetScore) {
        this.targetScore = targetScore;
        Log.i(TAG, "Tournament target score: " + targetScore);
    }

    public String determineTournamentWinner() {
        if (humanScore >= targetScore || computerScore >= targetScore) {
            return (humanScore > computerScore) ? "Human" : "Computer";
        }
        return "";
    }

    /**
     * startTournament will now **not block** for input.
     * Instead, call this method after UI collects tournament settings.
     * Pass choice (1=new game, 2=load) and target score directly.
     */
    public void startTournament(int choice, int selectedTargetScore) {
        if (choice == 1) {
            setTournScore(selectedTargetScore);
        } else {
            // load previously saved game from UI-provided file path
            Log.i(TAG, "Load game called - implement file selection via Android UI");
        }

        // start a round
        /*int saveChoice = currentRound.startRound(); // rounds themselves should also be refactored for Android
        if (saveChoice == 1) {
            Log.i(TAG, "Save game called - use Context file I/O on Android");
        }*/

        // add round scores
        addHumanScore(currentRound.getHumanScore());
        addComputerScore(currentRound.getComputerScore());

        // check tournament winner
        if (humanScore >= targetScore || computerScore >= targetScore) {
            String winner = determineTournamentWinner();
            Log.i(TAG, "Tournament winner: " + winner);
        }
    }
}