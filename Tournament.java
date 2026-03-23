package com.example.test2java;


import java.io.*;
import java.util.*;




public class Tournament {

    private static int humanScore;
    private static int computerScore;
    private int targetScore;

    private Scanner input = new Scanner(System.in);

    //Everything that’s an object lives on the heap.
    //You can only have a reference (pointer-like) variable on the stack:
    private Round currentRound = new Round();

    //static meaning belonging to the class itself
    //final meaning the variable's value can't change once assigned
    public static final int STARTING_SCORE = 0;

    // Constructor
    public Tournament() {
        humanScore = STARTING_SCORE;
        computerScore = STARTING_SCORE;
        targetScore = STARTING_SCORE;
    }


    public void saveGameState(String filename, Hand humanHand, Hand computerHand, Stock gameStock, Layout layout, Round currentRound)
    {

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

        System.exit(0);
    }


    public boolean loadGameState(String filename) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.contains("Tournament Score:")) {
                    String value = line.split(":")[1].trim();
                    this.setTournScore(Integer.parseInt(value));
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

        loadGameState(selectedFile);
    }




    public void initSave(Hand humanHand, Hand computerHand, Stock gameStock, Tournament tournament, Layout layout, Round currentRound, Scanner input) {


        String fileName;
        boolean isValidFileName;

        do {
            System.out.print("Enter name for save file (e.g., game1.txt): ");
            fileName = input.nextLine();

            isValidFileName = fileName.endsWith(".txt");

            if (!isValidFileName) {
                System.out.println("Error: File must end in .txt");
            }

        } while (!isValidFileName);

        saveGameState(fileName, humanHand, computerHand, gameStock, layout, currentRound);
    }




    // Add points to human player
    public void addHumanScore(int points) {
        //output points added for debugging
        System.out.println("Adding " + points + " points to Human score.");
        humanScore += points;

        //output new score for debugging
        System.out.println("New Human score: " + humanScore);

    }

    // Add points to computer player
    public void addComputerScore(int points) {
        //output points added for debugging
        System.out.println("Adding " + points + " points to Computer score.");
        computerScore += points;
        //output new score for debugging
        System.out.println("New Computer score: " + computerScore);
    }

    // Get human score
    public static int getHumanScore() {
        return humanScore;
    }

    // Get computer score
    public static int getComputerScore() {
        return computerScore;
    }

    // Get tournament target score
    public int getTournScore() {
        return targetScore;
    }

    // Set tournament target score
    public void setTournScore(int targetScore) {
        this.targetScore = targetScore;
        System.out.println("Tournament target score set to: " + targetScore);
    }

    // Set human score directly
    public void setHumanScore(int newScore) {
        humanScore = newScore;
    }

    // Set computer score directly
    public void setComputerScore(int newScore) {
        computerScore = newScore;
    }

    public String determineTournamentWinner()
    {
        // Check if the tournament's overall point threshold has been reached by any participant
        if (humanScore >= targetScore || computerScore >= targetScore)
        {
            // Compare final cumulative totals to declare the overall tournament champion
            return (humanScore > computerScore) ? "Human" : "Computer";
        }

        //in case anything goes wrong, but it shouldn't since this method is only called at the end of the tournament, but just in case, it will return an empty string if the tournament is still in progress or if there's an error
        return "";
    }

    public void startTournament(int choice)
    {
        boolean tournOver = false;
        int saveChoice = 0;

        while (!tournOver)
        {

            if(choice == 2)
            {
                showLoadMenu();
            }
            else
            {
                int tournScore = 0;
                do {
                    System.out.println("Enter the tournament score (50 - 250):");
                    tournScore = Integer.parseInt(input.nextLine());
                    setTournScore(tournScore);
                } while(tournScore < 50 || tournScore > 250);

            }
            //activate game round
            saveChoice = currentRound.startRound();

            if(saveChoice == 1) {
                initSave(currentRound.getHumanPlayer().getHand(), currentRound.getComputerPlayer().getHand(), currentRound.getGameStock(), this, currentRound.getLayout(), currentRound, input);
            }

            //total scores
            addHumanScore(currentRound.getHumanScore());
            addComputerScore(currentRound.getComputerScore());

            //will be at the bottom
            //outputting both for debug purposes
            System.out.println("Current Human Score: " + humanScore);
            System.out.println("Current Computer Score: " + computerScore);
            if (computerScore >= targetScore || humanScore >= targetScore)
            {
                tournOver = true;
            }
        }
        System.out.println("THE TOURNAMENT HAS OFICIALLUY ENDEEEDEDE!!!!!!!!Q:");

        determineTournamentWinner();
        System.out.println("The tournament winner is: " + determineTournamentWinner());
        System.out.println("With a score of: " + ((humanScore > computerScore) ? humanScore : computerScore));

    }



}