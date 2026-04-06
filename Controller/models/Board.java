package com.example.primaryjavalongaga.Controller.models;

import android.util.Log;
import java.util.LinkedList;
import java.util.List;

public class Board {

    private static final String TAG = "Layout";
    private LinkedList<String> dominoChain;

    // Constructor
    public Board() {
        dominoChain = new LinkedList<>();
    }

    // Check if the layout is empty
    public boolean isEmpty() {
        return dominoChain.isEmpty();
    }

    // Return the integer value on the left end
    public int returnLeft() {
        return dominoChain.getFirst().charAt(0) - '0';
    }

    // Return the integer value on the right end
    public int returnRight() {
        return dominoChain.getLast().charAt(2) - '0';
    }

    // Return the leftmost tile as a string
    public String returnLeftTile() {
        return dominoChain.getFirst();
    }

    // Return the rightmost tile as a string
    public String returnRightTile() {
        return dominoChain.getLast();
    }

    // Add a tile to the right
    public void addRight(String tile) {
        dominoChain.addLast(tile);
        Log.i(TAG, "Added " + tile + " to the right");
    }

    // Add a tile to the left
    public void addLeft(String tile) {
        dominoChain.addFirst(tile);
        Log.i(TAG, "Added " + tile + " to the left");
    }

    // Clear the board
    public void clearChain() {
        dominoChain.clear();
        Log.i(TAG, "Layout cleared");
    }

    // Get a copy of the domino chain
    public List<String> getChain() {
        return new LinkedList<>(dominoChain); // return a copy for safety
    }
}