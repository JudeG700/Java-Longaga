package com.example.primaryjavalongaga.Controller.models;

//import java.util.LinkedList;
import java.util.List;

public class LayoutView {

    public void display(List<String> tileChain) {
        String top = "";
        String mid = "";
        String bot = "";

        for (String tile : tileChain) {

            // Check if it's a double (e.g., "6-6")
            if (tile.charAt(0) == tile.charAt(2)) {
                top += " " + tile.charAt(0) + " ";
                mid += " | ";
                bot += " " + tile.charAt(2) + " ";
            } else {
                top += "    ";           // 4 spaces for non-double
                mid += tile + " ";       // tile string + space
                bot += "    ";           // 4 spaces
            }
        }

        System.out.println("  " + top);
        System.out.println("L " + mid + " R");
        System.out.println("  " + bot);
    }
}
