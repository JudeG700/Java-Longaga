package com.example.test2java;

import java.util.List;

//what is public abstract
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

    // getHand
    public Hand getHand() {
        return hand;
    }

    // the points are added to the player's score in the tournament class, not the round class, so this method is used to set the points for the player in the tournament class
    public void setAddedPoints(int points) {
        score = points;
    }

    // getScore
    public int getAddedPoints() {
        return score;
    }


    public static class Move
    {
        public String chosenTile;
        public char side;   // 'L' or 'R'
        public boolean draw;
        public boolean passed;
        public boolean help;
        public boolean hasPlayableTiles;
        public boolean choseSave;

        public Move() {
            chosenTile = "";
            side = ' ';
            draw = false;
            passed = false;
            help = false;
            hasPlayableTiles = false;
            choseSave = false;
        }
    }

    //declared as static
    public static class PlayableOption
    {
        public int index;
        public char side;

        public PlayableOption(int index, char side) {
            this.index = index;
            this.side = side;
        }
    }

    // Helper class to replace pair<int,int>
    public static class Pips {
        int left;
        int right;

        public Pips(int l, int r) {
            left = l;
            right = r;
        }
    }


    // returnID
    public abstract String returnID();

    public abstract Move takeTurn(Stock gameStock, Round gameRound, int leftEnd, int rightEnd);

    public abstract List<PlayableOption> findPlayableTiles(Hand hand, Round gameRound, int leftEnd, int rightEnd);

    public abstract Pips parseTile(String tile);

    public abstract void addScore(int points);

    public abstract int getScore();


    // emptyHand
    public void emptyHand() {
        hand.emptyHand();
    }

    // getTileByIndex
    public String getTileByIndex(int index) {
        return hand.getTileByIndex(index);
    }

    // getIndexByTile
    public int getIndexByTile(String tile) {
        return hand.getIndexByTile(tile);
    }

    // removeTile
    public void removeTile(int index) {
        hand.removeTile(index);
    }

    // hasTile
    public boolean hasTile(String targetTile) {
        return hand.hasTile(targetTile);
    }

    // addTile
    public void addTile(String tile) {
        hand.addTile(tile);
    }

    // setTiles
    public void setTiles(List<String> deal) {
        hand.setTiles(deal);
    }

    // getHandTiles
    public List<String> getHandTiles() {
        return hand.getHandTiles();
    }
}