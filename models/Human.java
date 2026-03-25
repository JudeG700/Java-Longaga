package com.example.primaryjavalongaga.models;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class Human extends Player {

    private static final String TAG = "Human";

    // Constructors
    public Human() { super(); }
    public Human(Hand humanHand) { super(humanHand); }

    // returnID
    @Override
    public String returnID() { return ID; }

    @Override
    public void addScore(int points) { score += points; }

    @Override
    public int getScore() { return score; }

    // parseTile
    @Override
    public Pips parseTile(String tile) {
        int dash = tile.indexOf('-');
        if (dash == -1) return new Pips(-1, -1);
        int left = Integer.parseInt(tile.substring(0, dash));
        int right = Integer.parseInt(tile.substring(dash + 1));
        return new Pips(left, right);
    }

    // findPlayableTiles
    @Override
    public List<PlayableOption> findPlayableTiles(Hand hand, Round gameRound, int leftEnd, int rightEnd) {
        List<PlayableOption> playableTiles = new ArrayList<>();
        List<String> tiles = hand.getHandTiles();

        final int COMPUTER_INDEX = 1;
        boolean oppPassed = gameRound.isPassed(COMPUTER_INDEX);

        for (int i = 0; i < tiles.size(); i++) {
            Pips p = parseTile(tiles.get(i));
            boolean isDouble = (p.left == p.right);

            boolean matchesLeft = (p.left == leftEnd || p.right == leftEnd);
            boolean matchesRight = (p.left == rightEnd || p.right == rightEnd);

            if (matchesLeft) playableTiles.add(new PlayableOption(i, 'L'));
            if (matchesRight && (isDouble || oppPassed)) playableTiles.add(new PlayableOption(i, 'R'));
        }
        return playableTiles;
    }

    // Validation helpers
    public boolean matchesLeft(Pips p, int leftEnd) { return p.left == leftEnd || p.right == leftEnd; }
    public boolean matchesRight(Pips p, int rightEnd) { return p.left == rightEnd || p.right == rightEnd; }
    public boolean canPlayRight(Pips p, int rightEnd, Round gameRound) {
        final int COMPUTER_INDEX = 1;
        boolean oppPassed = gameRound.isPassed(COMPUTER_INDEX);
        boolean isDouble = (p.left == p.right);
        return matchesRight(p, rightEnd) && (isDouble || oppPassed);
    }

    // handleDraw
    public void handleDraw(Move move, Stock gameStock, Round gameRound, int leftEnd, int rightEnd) {
        String drawnTile = gameStock.drawTile();
        addTile(drawnTile);
        Log.i(TAG, returnID() + " drew " + drawnTile);

        Pips p = parseTile(drawnTile);
        boolean canLeft = matchesLeft(p, leftEnd);
        boolean canRight = canPlayRight(p, rightEnd, gameRound);

        move.draw = true;

        // On Android, you'd prompt user to pick side via UI
        if (canLeft && canRight) {
            // Return a signal to UI to ask for side choice
            move.side = ' '; // placeholder
        } else if (canLeft) {
            move.side = 'L';
            move.chosenTile = drawnTile;
        } else if (canRight) {
            move.side = 'R';
            move.chosenTile = drawnTile;
        } else {
            Log.i(TAG, "Cannot play " + drawnTile);
            move.draw = false;
            move.passed = true;
        }
    }

    // takeTurn - simplified for Android
    @Override
    public Move takeTurn(Stock gameStock, Round gameRound, int leftEnd, int rightEnd) {
        Move move = new Move();
        move.draw = false;
        move.passed = false;
        move.help = false;
        move.chosenTile = "";
        move.side = ' ';
        move.hasPlayableTiles = false;
        move.choseSave = false;

        List<PlayableOption> playableList = findPlayableTiles(getHand(), gameRound, leftEnd, rightEnd);
        if (!playableList.isEmpty()) move.hasPlayableTiles = true;

        // On Android, actual choices come from buttons or dialogs
        // Here we just return the move object with flags, UI will update move
        return move;
    }
}