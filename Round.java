package com.example.test2java;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Round {

    private boolean roundOverFlag;
    private int engineIndex;
    private int roundNum;
    private String engine;
    private String requiredEngine;
    private List<String> requiredEngines;
    private int currentPlayer;
    private int nextPlayer;
    private boolean[] passed = new boolean[2];


    // These objects represent the tiles currently held by each participant.
    Hand humanHand = new Hand();
    Hand computerHand = new Hand();

    //polymorphism with player class
    Player[] players = {
            new Human(humanHand),
            new Computer(computerHand)
    };

    // Represents the sequence of domino tiles currently placed on the board.
    Layout layout = new Layout();

    // These variables store the numeric values at the left and right
    // ends of the layout so the program can determine legal tile placements.
    int leftEnd;
    int rightEnd;

    // Represents the collection of remaining tiles that players
    // may draw from when they cannot make a legal move.
    Stock gameStock = new Stock();

    // Responsible for displaying the layout visually to the player
    // without modifying the underlying layout data.
    LayoutView gameView = new LayoutView();

    //Indicates whether a round has already been set up.
    // This prevents reinitializing the round when loading from a save.
    boolean roundInitialized = false;

    // Used to ensure the player selects a valid starting option
    // before continuing to the main game loop.
    boolean isValidChoice = false;

    public Round() {
        roundNum = 1;
        engineIndex = 0;
        currentPlayer = 0;
        nextPlayer = 0;
        roundOverFlag = false;

        // Defines the mandatory opening doubles for the 7-round tournament structure
        requiredEngines = new ArrayList<>(Arrays.asList("6-6", "5-5", "4-4", "3-3", "2-2", "1-1", "0-0"));

        // Ensures all board sides start in a locked state
        // In Java, boolean arrays default to false, so passed[] is already initialized to false
        passed[0] = false;
        passed[1] = false;
    }


    public String obtainEngine()
    {
        String engine = "";

        determineRequiredEngine();


        engine = determineEngine(players[0].getHandTiles());

        int eIndex = 0;

        if(engine == "")
        {
            System.out.println("Human doesn't have the engine "+ requiredEngine);
            engine = determineEngine(players[1].getHandTiles());

            //if computer has the engine
            if (engine != "")
            {
                System.out.println("Computer has the engine");
                System.out.println("Computer takes first turn");
                eIndex = players[1].getHand().getHandTiles().indexOf(engine);
                players[1].removeTile(eIndex);

            }
            //if computer doesn't have the engine
            else
            {
                System.out.println("Computer doesn't have engine either");
                System.out.println("Proceeding with drawing... ");
                System.out.println();

            }

        }
        else
        {
            System.out.println("Human has engine");
            System.out.println("Human takes first turn");
            eIndex = players[0].getHand().getHandTiles().indexOf(engine);
            players[0].removeTile(eIndex);
        }

        // If neither player initially holds the required engine tile,
        // both players draw tiles until one obtains it, which determines
        // who starts the round.
        while (engine == "") {

            String humanDraw, compDraw;
            // Human tries to retrieve engine
            humanDraw = gameStock.drawTile();
            players[0].addTile(humanDraw);
            System.out.println("Human draws: " + humanDraw);

            if (humanDraw.trim().equals(this.getRequiredEngine())) {
                engine = humanDraw;
                System.out.println("Human obtained engine!");
                System.out.println("Human goes first!");
                eIndex = players[0].getHand().getHandTiles().indexOf(engine);
                players[0].removeTile(eIndex);
                currentPlayer = 0; // Human starts

                break;
            }

            // Computer tries to retrieve engine
            compDraw = gameStock.drawTile();
            players[1].addTile(compDraw);
            System.out.println("Computer draws: " + compDraw);

            //.equals() checks whether the strings look the same, have the same length and consist of the same characters in the same order.
            if (compDraw.trim().equals(this.getRequiredEngine())) {
                engine = compDraw;
                System.out.println("Computer obtained engine!");
                System.out.println("Computer goes first!");
                eIndex = players[1].getHand().getHandTiles().indexOf(engine);
                if (eIndex >= 0) {
                    players[1].removeTile(eIndex);
                } else {
                    System.out.println("Warning: computed engine index not found in computer hand: " + engine);
                }
                currentPlayer = 1; // Computer starts

                break;


            }

            //this program is designed to ensure it won't hit this case
            if (gameStock.getBoneyard().isEmpty())
            {
                System.out.println("Error. Something went wrong when finding the engine");
                System.exit(1);
            }
        }
        return engine;
    }

    public void showGameDetails() {
        //UI layout
        System.out.println("_______________________________________");
        //System.out.println("Tournament Score: " + gameTournament.getTournScore());
        System.out.println("Round no.: " + getRoundNum());
        System.out.println();

        System.out.println("Computer: ");
        System.out.println("	");
        players[1].getHand().displayHand();
        System.out.println();
        System.out.println("	" + "Score: " + players[1].getScore());
        System.out.println();

        System.out.println("Human: ");
        System.out.println("	");
        players[0].getHand().displayHand();
        System.out.println();
        System.out.println("	" + "Score: " + players[0].getScore());
        System.out.println();

        System.out.println("Layout: ");
        System.out.println("	");
        gameView.display(layout.getChain());
        System.out.println();

        System.out.println("Boneyard: ");
        gameStock.display();
        System.out.println();

        System.out.println("Previous player passed: " + labelStateValue(isPassed(nextPlayer)));
        System.out.println("_______________________________________" );
        System.out.println();

    }

    public Player getHumanPlayer()
    {
        return players[0];
    }

    public Player getComputerPlayer()
    {
        return players[1];
    }

    public Stock getGameStock()
    {
        return gameStock;
    }

    public Layout getLayout()
    {
        return layout;
    }

    void tiePoints(Player human, Player computer) {
// sum of points for human
        int sumHuman = 0;
        for (String tile : human.getHandTiles()) {
            if (tile == null || tile.length() < 3) continue;
            int a = Character.getNumericValue(tile.charAt(0));
            int b = Character.getNumericValue(tile.charAt(2));
            sumHuman += a + b;
        }

// sum of points for computer
        int sumComputer = 0;
        for (String tile : computer.getHandTiles()) {
            if (tile == null || tile.length() < 3) continue;
            int a = Character.getNumericValue(tile.charAt(0));
            int b = Character.getNumericValue(tile.charAt(2));
            sumComputer += a + b;
        }

        // announce result; actual score bookkeeping should be handled by caller or Tournament
        if (sumHuman < sumComputer) {
            System.out.println("Human wins the tied round! +" + sumComputer + " points");
            human.setAddedPoints(sumComputer);
        } else if (sumComputer < sumHuman) {
            System.out.println("Computer wins the tied round! +" + sumHuman + " points");
            human.setAddedPoints(sumHuman);
        } else {
            System.out.println("Tied round is a draw. No points awarded.");
        }
    }

    public void applyMove(
            Player player,
            Layout layout,
            Stock gameStock,
            Round gameRound,
            Player.Move move
    ) {


        if (move.passed) {
            System.out.println(player.returnID() + " passed");
            return;
        }

        //List<String> tiles = player.getHandTiles();

        int a = move.chosenTile.charAt(0) - '0';
        int b = move.chosenTile.charAt(2) - '0';

        int leftEnd = layout.returnLeft();
        int rightEnd = layout.returnRight();

        boolean isDouble = (a == b);

        boolean prevLeft = layout.returnLeftTile().equals(gameRound.getEngine());
        boolean prevRight = layout.returnRightTile().equals(gameRound.getEngine());

        // ----- LEFT SIDE -----
        if (move.side == 'L') {

            if (isDouble) {
                layout.addLeft(move.chosenTile);
            }
            else if (b == leftEnd) {
                layout.addLeft(move.chosenTile);
            }
            else if (a == leftEnd) {
                String flipped = "" + move.chosenTile.charAt(2) + "-" + move.chosenTile.charAt(0);

                System.out.println(player.returnID() + " flipped "
                        + move.chosenTile + " left to " + flipped);

                layout.addLeft(flipped);
            }

            if (player.returnID().equals("Human")) {
                System.out.println(player.returnID()
                        + " played " + move.chosenTile + " on left side of layout");
            }
        }

        // ----- RIGHT SIDE -----
        else if (move.side == 'R') {

            if (isDouble) {
                layout.addRight(move.chosenTile);
            }
            else if (a == rightEnd) {
                layout.addRight(move.chosenTile);
            }
            else if (b == rightEnd) {
                String flipped = "" + move.chosenTile.charAt(2) + "-" + move.chosenTile.charAt(0);

                System.out.println(player.returnID() + " flipped "
                        + move.chosenTile + " right to " + flipped);

                layout.addRight(flipped);
            }

            if (player.returnID().equals("Human")) {
                System.out.println(player.returnID()
                        + " played " + move.chosenTile + " on right side of layout");
            }
        }

        // ----- COMPUTER OUTPUT -----
        if (player.returnID().equals("Computer")) {

            String sideName = (move.side == 'L') ? "left" : "right";
            String referencePoint;

            if (sideName.equals("left")) {
                referencePoint = prevLeft ? "engine" : "layout";
            } else {
                referencePoint = prevRight ? "engine" : "layout";
            }

            if (move.passed) {
                System.out.println(player.returnID() + " passed.");
            }
            else {
                System.out.println("The " + player.returnID() + " placed "
                        + move.chosenTile + " to the " + sideName
                        + " of the " + referencePoint + ".");

                if (move.chosenTile.charAt(0) == move.chosenTile.charAt(2)) {
                    System.out.println("Trying to get rid of doubles as soon as possible");
                    System.out.println("Doubles placed left on player's side for purpose of messing their tile streak up");
                }
                else {
                    int totalPipValue = a + b;
                    System.out.println("The pips on my current tile, "
                            + move.chosenTile.charAt(0) + " and "
                            + move.chosenTile.charAt(2)
                            + ", add up to " + totalPipValue
                            + ", which is a higher sum value than the other tiles I can play");

                    System.out.println("Continuing to hold tiles with lots of pips would soften the blow if I were to lose; the player gets less points");
                }
            }
        }

        System.out.println();

        int tileIndex = player.getIndexByTile(move.chosenTile);
        player.removeTile(tileIndex);
    }

    public void processLine(String line, BufferedReader reader) throws IOException {

        boolean passVal = false;

        if (line == null) {
            return;
        }

        if (line.contains("Round No.:")) {
            String value = line.split(":")[1].trim();
            setRoundNum(Integer.parseInt(value));
            return;
        }

        if (line.contains("Computer:")) {
            // Next line expected: "   Hand: <tiles>"
            String handLine = reader.readLine();
            if (handLine != null && handLine.contains("Hand:")) {
                String tilesStr = handLine.split(":")[1].trim();
                if (!tilesStr.isEmpty()) {
                    String[] tiles = tilesStr.split("\\s+");
                    ArrayList<String> loadedTiles = new ArrayList<>();
                    for (String t : tiles) {
                        if (!t.isEmpty()) loadedTiles.add(t);
                    }
                    players[1].setTiles(loadedTiles);
                } else {
                    players[1].setTiles(new ArrayList<String>());
                }
            }
            return;
        }

        if (line.contains("Human:")) {
            // Next line expected: "   Hand: <tiles>"
            String handLine = reader.readLine();
            if (handLine != null && handLine.contains("Hand:")) {
                String tilesStr = handLine.split(":")[1].trim();
                if (!tilesStr.isEmpty()) {
                    String[] tiles = tilesStr.split("\\s+");
                    ArrayList<String> loadedTiles = new ArrayList<>();
                    for (String t : tiles) {
                        if (!t.isEmpty()) loadedTiles.add(t);
                    }
                    players[0].setTiles(loadedTiles);
                } else {
                    players[0].setTiles(new ArrayList<String>());
                }
            }
            return;
        }

        if (line.contains("Layout:")) {
            // Next line expected: "  L <tiles> R"
            String layoutLine = reader.readLine();
            if (layoutLine != null && layoutLine.contains("L") && layoutLine.contains("R")) {
                int start = layoutLine.indexOf("L") + 1;
                int end = layoutLine.indexOf("R");
                if (start < end && end != -1) {
                    String tilesStr = layoutLine.substring(start, end).trim();
                    if (!tilesStr.isEmpty()) {
                        String[] tiles = tilesStr.split("\\s+");
                        for (String t : tiles) {
                            if (!t.isEmpty()) layout.addRight(t);
                        }
                    }
                }
            }
            return;
        }

        if (line.contains("Boneyard:")) {
            // Next line expected to contain boneyard tiles
            String boneyardLine = reader.readLine();
            ArrayList<String> boneyardTiles = new ArrayList<>();
            if (boneyardLine != null && !boneyardLine.trim().isEmpty()) {
                String[] tiles = boneyardLine.trim().split("\\s+");
                for (String t : tiles) {
                    if (!t.isEmpty()) boneyardTiles.add(t);
                }
            }
            gameStock.setBoneyard(boneyardTiles);
            return;
        }

        if (line.contains("Previous Player Passed:")) {
            String val = line.split(":")[1].trim();
            passVal = val.equalsIgnoreCase("Yes");
            return;
        }

        if (line.contains("Next Player:")) {
            String nextName = line.split(":")[1].trim();
            if (nextName.contains("Computer")) {
                setCurrentPlayer(1);
                setNextPlayer(0);
            } else {
                setCurrentPlayer(0);
                setNextPlayer(1);
            }

            if (!passVal) {
                resetPass(getNextPlayer());
            } else {
                setPassed(getNextPlayer());
            }
            return;
        }

        System.out.println("Unrecognized line in save file: " + line);
    }







    public int startRound()
    {

        if(players[0].getHandTiles().isEmpty() && players[1].getHandTiles().isEmpty())
        {
            //deal first
            players[0].setTiles(gameStock.dealTiles());
            players[1].setTiles(gameStock.dealTiles());
        }


        if(layout.getChain().isEmpty())
        {
            //obtain engine and set left and right ends
            String engine = obtainEngine();
            layout.addRight(engine);
            leftEnd = engine.charAt(0) - '0';
            rightEnd = engine.charAt(2) - '0';
        }

        System.out.println("Starting round " + roundNum + " with engine " + engine);
        //loop

        while(!isRoundOver())
        {
            showGameDetails();
            Player.Move move = players[currentPlayer].takeTurn(gameStock, this, leftEnd, rightEnd);


            if(move.choseSave) {
                return 1;
            }

            applyMove(players[currentPlayer], layout, gameStock, this, move);


            //current player takes turn

            //check if move is pass
            if (move.passed) {
                System.out.println(players[currentPlayer].returnID() + " passes.");
                setPassed(currentPlayer);
            } else {
                //update layout and ends based on move
                Player.Pips p = players[currentPlayer].parseTile(move.chosenTile);
                if (move.side == 'L') {
                    leftEnd = (p.left == leftEnd) ? p.right : p.left;
                } else {
                    rightEnd = (p.left == rightEnd) ? p.right : p.left;
                }
                System.out.println(players[currentPlayer].returnID() + " plays " + move.chosenTile + " on the " + (move.side == 'L' ? "left" : "right") + " side.");
                resetPass(currentPlayer); // reset pass status after a successful play
            }

            // Check for round end conditions
            if (players[currentPlayer].getHandTiles().isEmpty()) {
                System.out.println(players[currentPlayer].returnID() + " has emptied their hand and wins the round!");
                setRoundOver();
            } else if (bothPassed()) {
                System.out.println("Both players have passed. The round is blocked.");
                setRoundOver();
            }

            // Switch to next player
            if(!isRoundOver())
            {
                currentPlayer = (currentPlayer + 1) % 2;

            }
        }

        determineRoundWinner(players[currentPlayer], players[nextPlayer]);

        resetStats();
        //reset for save/load and next round

        System.out.println("The round has ended ");
        return 0;


    }

    public void resetStats()
    {
        gameStock.resetBoneyard();
        players[0].emptyHand();
        players[1].emptyHand();

        layout.clearChain();
        incEIndex();
        nextRound();

    }
    void winPoints(Player winner, Player loser) {
        int total = 0;

        // sum the pips in loser hand
        List<String> loserHand = loser.getHand().getHandTiles();

        // convert left and right end to ints to add up total pips on each side
        for (String tile : loserHand) {
            if (tile == null || tile.length() < 3) continue;
            int a = Character.getNumericValue(tile.charAt(0));
            int b = Character.getNumericValue(tile.charAt(2));
            total += a + b;
        }

        // announce the result; actual score bookkeeping should be handled by caller or Tournament
        if (winner.returnID().equals("Human"))
        {
            System.out.println("Human wins the round! +" + total + " points");
        }
        // if computer wins the round
        else if (winner.returnID().equals("Computer"))
        {
            System.out.println("Computer wins the round! +" + total + " points");
        }
        winner.setAddedPoints(total);
    }

    public int getHumanScore()
    {
        return players[0].getAddedPoints();
    }

    public int getComputerScore()
    {
        return players[1].getAddedPoints();
    }




    public Player determineRoundWinner(Player human, Player computer) {

        if (bothPassed()) {
            tiePoints(human, computer);
            return null;
        } else {
            // Checks for the victory condition: a completely empty hand
            if (human.getHandTiles().isEmpty()) {
                winPoints(human, computer);
                return human;
            }
            if (computer.getHandTiles().isEmpty()) {
                winPoints(computer, human);
                return computer;
            }
        }

        return null;
    }

    public boolean isRoundOver() {
        return roundOverFlag;
    }

    public void setRoundOver() {
        roundOverFlag = true;
    }

    public void incEIndex() {
        engineIndex = (engineIndex + 1) % requiredEngines.size();
    }

    public void nextRound() {
        roundOverFlag = false;
        roundNum++;
        resetPasses();
    }

    public void setRoundNum(int roundNumber) {
        this.roundNum = roundNumber;
    }

    public int getRoundNum() {
        return roundNum;
    }

    public String getEngine() {
        return engine;
    }

    public String getRequiredEngine() {
        return requiredEngine;
    }

    public String determineEngine(List<String> playerHand) {
        System.out.println("Determining engine for hand: " + playerHand);
        for (String tile : playerHand) {
            if (tile.trim().equals(requiredEngine.trim()))
            {
                return tile;
            }
        }
        return "";
    }

    public void determineRequiredEngine() {
        int index = (roundNum - 1) % requiredEngines.size();
        requiredEngine = requiredEngines.get(index);
        setRequiredEngine(requiredEngine);
    }

    public void setCurrentPlayer(int playerIndex) {
        this.currentPlayer = playerIndex;
    }

    public void setNextPlayer(int playerIndex) {
        this.nextPlayer = playerIndex;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public void setRequiredEngine(String requiredEngine) {
        this.requiredEngine = requiredEngine;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getNextPlayer() {
        return nextPlayer;
    }

    public void resetPasses() {
        passed[0] = false;
        passed[1] = false;
    }

    public void setPassed(int playerIndex) {
        passed[playerIndex] = true;
    }

    public void resetPass(int playerIndex) {
        passed[playerIndex] = false;
    }

    public boolean bothPassed() {
        return passed[0] && passed[1];
    }

    public boolean isPassed(int playerIndex) {
        return passed[playerIndex];
    }

    public String labelStateValue(boolean stateValue) {
        return stateValue ? "Yes" : "No";
    }
}