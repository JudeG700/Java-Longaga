package com.example.primaryjavalongaga.Controller.models;

import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Tournament {

    private static final String TAG = "Tournament";

    private int humanScore;
    private int computerScore;
    private int targetScore;

    //private Round currentRound = new Round();

    public static final int STARTING_SCORE = 0;

    public Tournament() {
        humanScore = STARTING_SCORE;
        computerScore = STARTING_SCORE;
        targetScore = STARTING_SCORE;
    }

    // Add points to human player
    public void setHumanScore(int points) {
        humanScore = points;
    }

    // Add points to computer player
    public void setComputerScore(int points) {
        computerScore = points;
    }

    public int getHumanScore() { return humanScore; }
    public int getComputerScore() { return computerScore; }

    public int getTournScore() { return targetScore; }
    public void setTournScore(int targetScore) {
        this.targetScore = targetScore;
    }


    public String determineTournamentWinner() {

        // add round scores


        // check tournament winner
        if (getHumanScore() >= targetScore) {
            return "human";
        }
        else if(getComputerScore() >= targetScore)
        {
            return "computer";
        }

        return "";
    }

    /**
     * startTournament will now **not block** for input.
     * Instead, call this method after UI collects tournament settings.
     * Pass choice (1=new game, 2=load) and target score directly.
     */
    public void startTournament(int choice, int selectedTargetScore) {
        if (choice == 1) {
            setTournScore(selectedTargetScore);
        } else {
            // load previously saved game from UI-provided file path
            Log.i(TAG, "Load game called - implement file selection via Android UI");
        }

        // start a round
        /*int saveChoice = currentRound.startRound(); // rounds themselves should also be refactored for Android
        if (saveChoice == 1) {
            Log.i(TAG, "Save game called - use Context file I/O on Android");
        }*/


    }


    public void saveGameState(String filename, Hand humanHand, Hand computerHand, Stock gameStock, Layout layout, Round currentRound) {

        try (BufferedWriter outFile = new BufferedWriter(new FileWriter(filename))) {

            outFile.write("Tournament Score: " + getTournScore() + "\n");
            outFile.write("Round No.: " + currentRound.getRoundNum() + "\n\n");

            outFile.write("Computer:\n");
            outFile.write("   Hand: ");
            for (String tile : computerHand.getHandTiles())
                outFile.write(tile + " ");
            outFile.write("\n   Score: " + getComputerScore() + "\n\n");

            outFile.write("Human:\n");
            outFile.write("   Hand: ");
            for (String tile : humanHand.getHandTiles())
                outFile.write(tile + " ");
            outFile.write("\n   Score: " + getHumanScore() + "\n\n");

            outFile.write("Layout:\n");
            outFile.write("  L ");
            for (String tile : layout.getChain())
                outFile.write(tile + " ");
            outFile.write("R\n\n");

            outFile.write("Boneyard:\n");
            for (String tile : gameStock.getBoneyard())
                outFile.write(tile + " ");
            outFile.write("\n\n");

            outFile.write("Previous Player Passed: " +
                    (currentRound.isPassed(currentRound.getCurrentPlayer()) ? "Yes" : "No") + "\n\n");

            outFile.write("Next Player: " +
                    (currentRound.getNextPlayer() == 1 ? "Computer" : "Human") + "\n");

            System.out.println("Game saved to " + filename);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }


    public boolean loadGameState(BufferedReader reader, Round currentRound) {

        try{

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.contains("Tournament Score:")) {
                    String value = line.split(":")[1].trim();
                    setTournScore(Integer.parseInt(value));
                }

                // Everything else → let Round handle it
                else {
                    currentRound.processLine(line, reader);
                }
            }

        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public void showLoadMenu() {
        Scanner input = new Scanner(System.in);

        // Get all .txt files in current directory
        File dir = new File("."); // current directory
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("No save files found.");
            return;
        }

        // List all files
        ArrayList<String> saveFiles = new ArrayList<>();
        System.out.println("--- AVAILABLE SAVES ---");
        int count = 1;
        for (File f : files) {
            System.out.println(count + ") " + f.getName());
            saveFiles.add(f.getName());
            count++;
        }

        // Prompt user to select a file
        int choice = -1;
        do {
            System.out.print("Select a file number to load: ");

            if (input.hasNextInt()) {
                choice = input.nextInt();
            } else {
                System.out.println("Invalid input!");
                input.next(); // consume invalid token
                continue;
            }

        } while (choice < 1 || choice > saveFiles.size());

        String selectedFile = saveFiles.get(choice - 1);
        System.out.println("Loading: " + selectedFile + "...\n");

    }




    public void initSave(Hand humanHand, Hand computerHand, Stock gameStock, Layout layout, Round currentRound, String fileName) {


        saveGameState(fileName, humanHand, computerHand, gameStock, layout, currentRound);
    }

}