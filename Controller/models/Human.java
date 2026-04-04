package com.example.primaryjavalongaga.Controller.models;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class Human extends Player {


    private static final String ID = "Human";

    // Constructors
    public Human() { super(); }
    public Human(Hand humanHand) { super(humanHand); }

    // returnID
    @Override
    public String returnID() { return ID; }

    @Override
    public void addPoints(int points) { score += points; }

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


    public int checkTileFit(String tile, int leftEnd, int rightEnd) {


        int a = tile.charAt(0) - '0';
        int b = tile.charAt(2) - '0';

        boolean fitsLeft = (a == leftEnd || b == leftEnd);
        boolean fitsRight = (a == rightEnd || b == rightEnd);

        if (fitsLeft && fitsRight) return 3; // BOTH
        if (fitsLeft) return 1;             // LEFT ONLY
        if (fitsRight) return 2;            // RIGHT ONLY
        return 0;                           // NONE
    }

    public String handlePass(Move move, Stock gameStock)
    {
        /*
        if(move.hasPlayableTiles || !gameStock.getBoneyard().isEmpty())
        {
            return "You can still play or draw tiles";
        } */

        move.passed = true;
        return " ";
    }


    public String handleDraw(Move move, Stock gameStock, Round gameRound, int leftEnd, int rightEnd) {

        if(gameStock.getBoneyard().isEmpty())
        {

            return "You have no more tiles to draw ";
        }
        List<PlayableOption> playableList = findPlayableTiles(getHand(), gameRound, leftEnd, rightEnd);
        if (!playableList.isEmpty())
        {
            return "You still have tiles you can play ";
        }


        String drawnTile = gameStock.drawTile();
        addTile(drawnTile);
        //System.out.println(returnID() + " drew " + drawnTile);

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
            move.draw = false;
            move.passed = true;
            return "unplayable";
        }

        return " ";
    }

    public boolean checkValidity(Move move, Round gameRound, int leftEnd, int rightEnd)
    {

        Pips p = parseTile(move.chosenTile);

        boolean choiceValid = false;

        if (move.side == 'L') {
            if (matchesLeft(p, leftEnd)) {
                choiceValid = true;
            } else {
                System.out.println("Invalid left move.");
            }
        } else {
            if (canPlayRight(p, rightEnd, gameRound)) {
                choiceValid = true;
            } else {
                System.out.println("Invalid right move.");
            }
        }
        return choiceValid;

    }
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