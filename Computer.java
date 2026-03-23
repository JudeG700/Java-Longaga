package com.example.test2java;

import java.util.*;

public class Computer extends Player {

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
    public void addScore(int points)
    {
        score += points;
    }

    @Override
    public int getScore()
    {
        return score;
    }

    // isDouble
    public boolean isDouble(int a, int b) {
        return a == b;
    }

    // tileWeight
    public int tileWeight(int a, int b) {
        return a + b;
    }

    // scoreTile
    public int scoreTile(int leftPip, int rightPip, int leftEnd, int rightEnd, char pickedSide, String player) {
        int score = 0;

        score += tileWeight(leftPip, rightPip);

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

    // parseTile
    public Pips parseTile(String tile) {
        int dash = tile.indexOf('-');

        if (dash == -1) return new Pips(-1, -1);

        int left = Integer.parseInt(tile.substring(0, dash));
        int right = Integer.parseInt(tile.substring(dash + 1));

        return new Pips(left, right);
    }

    // findPlayableTiles
    public List<Player.PlayableOption> findPlayableTiles(Hand hand, Round gameRound, int leftEnd, int rightEnd) {

        List<Player.PlayableOption> playable = new ArrayList<>();
        List<String> tiles = hand.getHandTiles();

        final int HUMAN_INDEX = 0;
        boolean humanPassed = gameRound.isPassed(HUMAN_INDEX);

        for (int i = 0; i < tiles.size(); i++) {

            Pips p = parseTile(tiles.get(i));
            boolean isDoubleTile = isDouble(p.left ,p.right);

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

    // help
    public void help(Player player, Move playerMove, Stock gamestock, Round gameRound, int leftEnd, int rightEnd) {

        List<Player.PlayableOption> playableTiles = player.findPlayableTiles(player.getHand(), gameRound, leftEnd, rightEnd);

        if (playableTiles.isEmpty()) {
            if (gamestock.getBoneyard().isEmpty()) {
                System.out.println("The boneyard is empty and there's no playable tiles. You have to pass.");
            } else {
                if (playerMove.draw) {
                    System.out.println("There's nowhere to place your drawn tile. You have to pass");
                } else {
                    System.out.println("No playable tiles. It's best to draw.");
                }
            }
            return;
        }

        int bestIndex = -1;
        int bestScore = -1;
        char bestSide = 'L';

        if (playableTiles.size() == 1) {
            bestIndex = playableTiles.get(0).index;
            bestSide = playableTiles.get(0).side;
        } else {
            for (Player.PlayableOption option : playableTiles) {
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

            System.out.println("Recommendation: " + recommendedTile +
                    " on the " + (bestSide == 'L' ? "left" : "right") + " side");

            final int COMPUTER_INDEX = 1;

            if (bestSide == 'R' && gameRound.isPassed(COMPUTER_INDEX)) {
                System.out.println("Your opponent has passed! Place it on the right to disrupt their streak!");
            }

            if (recommendedTile.charAt(0) == recommendedTile.charAt(2)) {
                System.out.println("Doubles can be placed on any side as long as they match.");
                System.out.println("Placing it on the " + (bestSide == 'L' ? "left" : "right") + " side will soften the blow of your opponent's win");
            }
        }
    }

    // takeTurn
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
                move.passed = true;
                return move;
            } else {
                move.draw = true;
                String drawn = gameStock.drawTile();
                hand.addTile(drawn);

                System.out.println(returnID() + " drew " + drawn);

                playableTiles = findPlayableTiles(hand, gameRound, leftEnd, rightEnd);

                if (playableTiles.isEmpty()) {
                    System.out.println("No tiles can be played, will have to pass");
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