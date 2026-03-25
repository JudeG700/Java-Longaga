package com.example.primaryjavalongaga.models;

import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stock {

    private static final String TAG = "Stock";

    public static final int MAX_PIP_VALUE = 6;
    public static final int INITIAL_DEAL_COUNT = 8;

    private List<String> boneyard;

    // Constructor: generates 28 domino tiles
    public Stock() {
        boneyard = new ArrayList<>();
        for (int i = 0; i <= MAX_PIP_VALUE; i++) {
            for (int j = i; j <= MAX_PIP_VALUE; j++) {
                boneyard.add(i + "-" + j);
            }
        }
        shuffleBoneyard();
    }

    // Display all tiles in boneyard (for debugging)
    public void display() {
        Log.i(TAG, String.join(" ", boneyard));
    }

    // Shuffle tiles in boneyard
    public void shuffleBoneyard() {
        Collections.shuffle(boneyard);
        Log.i(TAG, "Boneyard shuffled");
    }

    // Draw a tile from the front
    public String drawTile() {
        if (boneyard.isEmpty()) return "";
        String drawn = boneyard.remove(0);
        Log.i(TAG, "Drew tile: " + drawn);
        return drawn;
    }

    // Deal initial hand to a player
    public List<String> dealTiles() {
        List<String> hand = new ArrayList<>();
        for (int i = 0; i < INITIAL_DEAL_COUNT && !boneyard.isEmpty(); i++) {
            hand.add(boneyard.remove(boneyard.size() - 1));
        }
        return hand;
    }

    // Get a copy of the boneyard
    public List<String> getBoneyard() {
        return new ArrayList<>(boneyard);
    }

    // Replace the boneyard with a given list
    public void setBoneyard(List<String> tiles) {
        boneyard = new ArrayList<>(tiles);
    }

    // Reset boneyard to full 28 tiles and shuffle
    public void resetBoneyard() {
        emptyBoneyard();
        for (int i = 0; i <= MAX_PIP_VALUE; i++) {
            for (int j = i; j <= MAX_PIP_VALUE; j++) {
                boneyard.add(i + "-" + j);
            }
        }
        shuffleBoneyard();
    }

    // Empty the boneyard
    public void emptyBoneyard() {
        boneyard.clear();
        Log.i(TAG, "Boneyard emptied");
    }
}