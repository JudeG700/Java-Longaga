package com.example.test2java;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private List<String> tiles;

    // Constructor
    public Hand() {
        tiles = new ArrayList<>();
    }

    // displayHand
    public void displayHand() {
        System.out.print("Hand: ");
        for (int i = 0; i < tiles.size(); i++) {
            System.out.print(tiles.get(i) + " ");
        }
        System.out.println();
    }

    // isEmptyHand
    public boolean isEmptyHand() {
        return tiles.isEmpty();
    }

    // hasTile
    public boolean hasTile(String targetTile) {
        // flip tile (e.g., "5-6" -> "6-5")
        String flipped = "" + targetTile.charAt(2) + "-" + targetTile.charAt(0);

        for (String tile : tiles) {
            if (tile.equals(targetTile) || tile.equals(flipped)) {
                return true;
            }
        }
        return false;
    }

    // getTileByIndex
    public String getTileByIndex(int index) {
        return tiles.get(index);
    }

    // getIndexByTile
    public int getIndexByTile(String tile) {
        for (int i = 0; i < tiles.size(); i++) {
            if (tiles.get(i).equals(tile)) {
                return i;
            }
        }

        System.out.println("Tile not found:");
        return -1;
    }

    // removeTile
    public void removeTile(int tileDex) {
        tiles.remove(tileDex);
    }

    // emptyHand
    public void emptyHand() {
        tiles.clear();
    }

    // addTile
    public void addTile(String tile) {
        tiles.add(tile);
    }

    // setTiles
    public void setTiles(List<String> deal) {
        tiles = new ArrayList<>(deal); // safer copy
    }

    // getHandTiles
    public List<String> getHandTiles() {
        return tiles;
    }
}