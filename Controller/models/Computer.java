package com.example.primaryjavalongaga.Controller.models;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class Computer extends Player {

    private static final String ID = "Computer";

    // Constructors
    public Computer() {
        super();
    }

    public Computer(Hand computerHand) {
        super(computerHand);
    }

    // returnID
    @Override
    public String returnID() {
        return ID;
    }

    @Override
    public void addPoints(int points) {
        score += points;
    }

    @Override
    public int getScore() {
        return score;
    }

    // Helper: check if a tile is double
    public boolean isDouble(int a, int b) {
        return a == b;
    }

    // Helper: calculate tile weight
    public int tileWeight(int a, int b) {
        return a + b;
    }

    // Score a tile based on game state
    public int scoreTile(int leftPip, int rightPip, int leftEnd, int rightEnd, char pickedSide, String player) {
        int score = tileWeight(leftPip, rightPip);

        if (isDouble(leftPip, rightPip)) {
            score += 10;
        }

        if (pickedSide == 'L' && player.equals("Computer")) {
            score += 10;
        } else if (pickedSide == 'R' && player.equals("Human")) {
            score += 10;
        }

        return score;
    }

    // Parse tile string into Pips
    @Override
    public Pips parseTile(String tile) {
        int dash = tile.indexOf('-');
        if (dash == -1) return new Pips(-1, -1);

        int left = Integer.parseInt(tile.substring(0, dash));
        int right = Integer.parseInt(tile.substring(dash + 1));
        return new Pips(left, right);
    }

    // Find playable tiles
    @Override
    public List<PlayableOption> findPlayableTiles(Hand hand, Round gameRound, int leftEnd, int rightEnd) {
        List<PlayableOption> playable = new ArrayList<>();
        List<String> tiles = hand.getHandTiles();

        final int HUMAN_INDEX = 0;
        boolean humanPassed = gameRound.isPassed(HUMAN_INDEX);

        for (int i = 0; i < tiles.size(); i++) {
            Pips p = parseTile(tiles.get(i));
            boolean isDoubleTile = isDouble(p.left, p.right);

            boolean matchesLeft = (p.left == leftEnd || p.right == leftEnd);
            boolean matchesRight = (p.left == rightEnd || p.right == rightEnd);

            if (matchesRight) {
                playable.add(new PlayableOption(i, 'R'));
            }
            if (matchesLeft && (isDoubleTile || humanPassed)) {
                playable.add(new PlayableOption(i, 'L'));
            }
        }
        return playable;
    }

    // Optional: provide recommendations via Android logs
    // Change 'void' to 'String'
    public String help(Player player, Player.Move playerMove, Stock gameStock, Round gameRound, int leftEnd, int rightEnd) {
        List<PlayableOption> playableTiles = player.findPlayableTiles(player.getHand(), gameRound, leftEnd, rightEnd);

        if (playableTiles.isEmpty()) {
            if (gameStock.getBoneyard().isEmpty()) {
                return "The boneyard is empty. You have to pass.";
            } else {
                if (playerMove.draw) {
                    return "No place for your drawn tile. You have to pass.";
                } else {
                    return "No playable tiles. You should draw.";
                }
            }
        }

        int bestIndex = -1;
        int bestScore = -1;
        char bestSide = 'L';

        if (playableTiles.size() == 1) {

            bestIndex = playableTiles.get(0).index;

            bestSide = playableTiles.get(0).side;

        } else {

            for (PlayableOption option : playableTiles) {

                String tile = player.getTileByIndex(option.index);

                Pips pips = parseTile(tile);



                int currentScore = scoreTile(

                        pips.left,

                        pips.right,

                        leftEnd,

                        rightEnd,

                        option.side,

                        player.returnID()

                );



                if (currentScore > bestScore) {

                    bestScore = currentScore;

                    bestIndex = option.index;

                    bestSide = option.side;

                }

            }

        }




        if (bestIndex != -1) {
            String recommendedTile = player.getHand().getTileByIndex(bestIndex);
            return "Recommendation: " + recommendedTile +
                    " on the " + (bestSide == 'L' ? "left" : "right") + " side";
        }

        return "No recommendation available.";
    }

    // Take a turn
    @Override
    public Move takeTurn(Stock gameStock, Round gameRound, int leftEnd, int rightEnd) {
        Move move = new Move();
        move.draw = false;
        move.passed = false;
        move.help = false;
        move.chosenTile = "";
        move.side = ' ';
        move.hasPlayableTiles = false;

        List<PlayableOption> playableTiles = findPlayableTiles(hand, gameRound, leftEnd, rightEnd);

        if (playableTiles.isEmpty()) {
            if (gameStock.getBoneyard().isEmpty()) {
                System.out.println("There are no playable tiles and the boneyard's empty. I will pass.");
                move.passed = true;
                return move;
            } else {
                move.draw = true;
                String drawn = gameStock.drawTile();
                hand.addTile(drawn);
                System.out.println("Drew tile " + drawn);

                playableTiles = findPlayableTiles(hand, gameRound, leftEnd, rightEnd);
                if (playableTiles.isEmpty()) {
                    System.out.println("Can't play " + drawn + ". Passing.");
                    move.passed = true;
                    return move;
                }
            }
        }

        int bestIndex = -1;
        int bestScore = -1;
        char bestSide = 'R';

        for (PlayableOption option : playableTiles) {
            String tile = hand.getTileByIndex(option.index);
            Pips p = parseTile(tile);

            int currentScore = scoreTile(
                    p.left,
                    p.right,
                    leftEnd,
                    rightEnd,
                    option.side,
                    returnID()
            );

            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestIndex = option.index;
                bestSide = option.side;
            }
        }

        move.chosenTile = hand.getTileByIndex(bestIndex);
        move.side = bestSide;

        return move;
    }
}