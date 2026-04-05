package com.example.primaryjavalongaga.Controller.models;

import java.util.List;

public abstract class Player {

    protected int score;
    protected String ID;
    protected Hand hand;

    // Default constructor
    public Player() {
        score = 0;
        ID = "Player";
        hand = new Hand();
    }

    // Parameterized constructor
    public Player(Hand initialHand) {
        score = 0;
        ID = "Player";
        hand = initialHand;
    }

    // Get the player's hand
    public Hand getHand() {
        return hand;
    }

    // Set points for the tournament
    public abstract void addPoints(int points);

    // Get points for the tournament
    public int getAddedPoints() {
        return score;
    }

    // Inner class representing a move
    public static class Move {
        public String chosenTile;
        public char side;   // 'L' or 'R'
        public boolean draw;
        public boolean passed;
        public boolean help;
        public boolean hasPlayableTiles;
        public String reasoning;

        public Move() {
            chosenTile = "";
            reasoning = "";
            side = ' ';
            draw = false;
            passed = false;
            help = false;
            hasPlayableTiles = false;
        }
    }

    public String help(Player player, Player.Move playerMove, Stock gameStock, Round gameRound, int leftEnd, int rightEnd){return "-";}



    // Inner class for playable options
    public static class PlayableOption {
        public int index;
        public char side;

        public PlayableOption(int index, char side) {
            this.index = index;
            this.side = side;
        }
    }

    // Inner class for tile pips
    public static class Pips {
        int left;
        int right;

        public Pips(int l, int r) {
            left = l;
            right = r;
        }
    }

    // Abstract methods to implement in subclasses
    public abstract String returnID();

    public abstract Move takeTurn(Stock gameStock, Round gameRound, int leftEnd, int rightEnd);

    public abstract List<PlayableOption> findPlayableTiles(Hand hand, Round gameRound, int leftEnd, int rightEnd);

    public abstract Pips parseTile(String tile);

    public abstract int getScore();

    // Hand operations
    public void emptyHand() {
        hand.emptyHand();
    }

    public String getTileByIndex(int index) {
        return hand.getTileByIndex(index);
    }

    public int getIndexByTile(String tile) {
        return hand.getIndexByTile(tile);
    }

    public void removeTile(int index) {
        hand.removeTile(index);
    }

    public boolean hasTile(String targetTile) {
        return hand.hasTile(targetTile);
    }

    public int checkTileFit(String tile, int leftEnd, int rightEnd) {
        return 0;
    }



    public String checkValidity(Move move, Round gameRound, int leftEnd, int rightEnd)
        {return " ";}

    public String handleDraw(Player.Move move, Stock gameStock, Round round, int l, int r) {return " ";}

    public String handlePass(Player.Move move, Stock gameStock){return " ";}

    public void addTile(String tile) {
        hand.addTile(tile);
    }

    public void setTiles(List<String> deal) {
        hand.setTiles(deal);
    }

    public List<String> getHandTiles() {
        return hand.getHandTiles();
    }
}