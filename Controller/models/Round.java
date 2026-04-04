package com.example.primaryjavalongaga.Controller.models;
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

    private int leftEnd;
    private int rightEnd;

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

    // Represents the collection of remaining tiles that players
    // may draw from when they cannot make a legal move.
    Stock gameStock = new Stock();

    // Responsible for displaying the layout visually to the player
    // without modifying the underlying layout data.

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



    public String obtainEngine() {

        determineRequiredEngine();

        String engine = "";

        // Check hands first
        engine = determineEngine(players[0].getHandTiles());
        if (!engine.isEmpty()) {
            currentPlayer = 0;
            System.out.println("Human has the engine! They take first turn!");


        } else {
            engine = determineEngine(players[1].getHandTiles());
            if (!engine.isEmpty()) {
                currentPlayer = 1;
                System.out.println("Computer has the engine! They take first turn!");
            }
        }


        System.out.println("Neither have engine. Drawing...");

        // If neither has it → draw loop
        while (engine.isEmpty()) {

            String humanDraw = gameStock.drawTile();
            players[0].addTile(humanDraw);

            if (humanDraw.equals(requiredEngine)) {
                engine = humanDraw;
                System.out.println("Human draws the engine! They take first turn!");
                currentPlayer = 0;
                break;
            }

            String compDraw = gameStock.drawTile();
            players[1].addTile(compDraw);

            if (compDraw.equals(requiredEngine)) {
                engine = compDraw;
                System.out.println("Computer draws the engine! They take first turn!");
                currentPlayer = 1;
                break;
            }
        }

        // Remove engine from whoever got it
        int index = players[currentPlayer].getHandTiles().indexOf(engine);
        players[currentPlayer].removeTile(index);


        return engine;
    }

    /*public String obtainEngine() {
        String engine = "";

        determineRequiredEngine();

        engine = determineEngine(players[0].getHandTiles());

        int eIndex;

        if (engine.isEmpty()) {
            //log("Human doesn't have the engine " + requiredEngine);

            engine = determineEngine(players[1].getHandTiles());

            if (!engine.isEmpty()) {
                //log("Computer has the engine");
                //log("Computer takes first turn");

                eIndex = players[1].getHand().getHandTiles().indexOf(engine);
                players[1].removeTile(eIndex);
                //currentPlayer = 0;
                setCurrentPlayer(0);

            } else {
                log("Computer doesn't have engine either");
                log("Proceeding with drawing...");
            }

        } else {
            //log("Human has engine");
            //log("Human takes first turn");

            eIndex = players[0].getHand().getHandTiles().indexOf(engine);
            players[0].removeTile(eIndex);
            setCurrentPlayer(1);
            //currentPlayer = 1;
        }

        while (engine.isEmpty()) {

            String humanDraw = gameStock.drawTile();
            players[0].addTile(humanDraw);
            //log("Human draws: " + humanDraw);

            if (humanDraw.trim().equals(getRequiredEngine())) {
                engine = humanDraw;
                //log("Human obtained engine!");
                setCurrentPlayer(1);


                eIndex = players[0].getHand().getHandTiles().indexOf(engine);
                players[0].removeTile(eIndex);
                break;
            }

            String compDraw = gameStock.drawTile();
            players[1].addTile(compDraw);
            //log("Computer draws: " + compDraw);

            if (compDraw.trim().equals(getRequiredEngine())) {
                engine = compDraw;
                //log("Computer obtained engine!");
                setCurrentPlayer(0);

                eIndex = players[1].getHand().getHandTiles().indexOf(engine);
                if (eIndex >= 0) {
                    players[1].removeTile(eIndex);
                }
                break;
            }

            if (gameStock.getBoneyard().isEmpty()) {
                //log("Error: No engine found.");
                break;
            }
        }

        return engine;
    }
*/

    public Player getPlayerByIndex(int index)
    {
        return players[index];
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

    public String tiePoints(Player human, Player computer) {
// sum of points for human
        int sumHuman = 0;
        for (String tile : human.getHandTiles()) {
            if (tile == null || tile.length() < 3) continue;
            int a = Character.getNumericValue(tile.charAt(0));
            int b = Character.getNumericValue(tile.charAt(2));
            sumHuman += a + b;
        }

        int sumComputer = 0;
        for (String tile : computer.getHandTiles()) {
            if (tile == null || tile.length() < 3) continue;
            int a = Character.getNumericValue(tile.charAt(0));
            int b = Character.getNumericValue(tile.charAt(2));
            sumComputer += a + b;
        }

        // announce result; actual score bookkeeping should be handled by caller or Tournament
        if (sumHuman < sumComputer) {
            human.addPoints(sumComputer);
            return "Human wins the tied round! +" + sumComputer + " points";
        } else if (sumComputer < sumHuman) {
            computer.addPoints(sumHuman);
            return "Computer wins the tied round! +" + sumComputer + " points";
        } else {
            return "Tied round is a draw. No points awarded.";
        }

    }


    public void applyMove(
            Player player,
            Layout layout,
            Stock gameStock,
            Round gameRound,
            Player.Move move
    ) {


        if (move == null || move.chosenTile == null) {
            return;
        }

        if(move.passed)
        {
            setPassed(currentPlayer);
            return;
        }

        // 2. Parse the tile (e.g., "4-5" becomes a=4, b=5)
        int a = move.chosenTile.charAt(0) - '0';
        int b = move.chosenTile.charAt(2) - '0';
        String tileToPlace = move.chosenTile;

        int leftEnd = layout.returnLeft();
        int rightEnd = layout.returnRight();

        // 3. Placement Logic with Auto-Flipping
        if (move.side == 'L') {
            // If the right side of our tile matches the left end of the board, we're good.
            // If the left side matches, we MUST flip it.
            if (a == leftEnd && a != b) {
                tileToPlace = b + "-" + a; // Flip it

            }
            layout.addLeft(tileToPlace);
        } else {
            // If the left side of our tile matches the right end of the board, we're good.
            // If the right side matches, we MUST flip it.
            if (b == rightEnd && a != b) {
                tileToPlace = b + "-" + a; // Flip it
            }

            this.leftEnd = layout.returnLeft();
            this.rightEnd = layout.returnRight();


            layout.addRight(tileToPlace);
        }

        // 4. Update the internal "Ends" so the next move knows what to match
        this.leftEnd = layout.returnLeft();
        this.rightEnd = layout.returnRight();

        // 5. Remove from hand
        int tileIndex = player.getIndexByTile(move.chosenTile);
        if (tileIndex != -1) {
            player.removeTile(tileIndex);
        }
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
            String scoreLine = reader.readLine();
                if(scoreLine != null && scoreLine.contains("Score:"))
                {
                    String numStr = scoreLine.split(":")[1].trim();
                    if(!numStr.isEmpty())
                    {
                        players[1].addPoints(Integer.parseInt(numStr));
                    }
                    else
                    {
                        players[1].addPoints(0);
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
            String scoreLine = reader.readLine();
            if(scoreLine != null && scoreLine.contains("Score:"))
            {
                String numStr = scoreLine.split(":")[1].trim();
                if(!numStr.isEmpty())
                {
                    players[0].addPoints(Integer.parseInt(numStr));
                }
                else
                {
                    players[0].addPoints(0);
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

        //System.out.println("Unrecognized line in save file: " + line);
    }






    public void dealTiles()
    {
        players[0].setTiles(gameStock.dealTiles());
        players[1].setTiles(gameStock.dealTiles());
    }


    public void applyMove(Player.Move move)
    {
        applyMove(players[currentPlayer], layout, gameStock, this, move);
    }

    public Player.Move takeTurn() {
        //List<String> logs = new ArrayList<>();

        Player.Move move = players[currentPlayer].takeTurn(gameStock, this, leftEnd, rightEnd);

        return move;

    }

    /*public String startRound()
    {
        if (players[0].getHandTiles().isEmpty() && players[1].getHandTiles().isEmpty()) {
            dealTiles();
        }

        if (layout.getChain().isEmpty()) {
            String engineFound = obtainEngine(); // This sets currentPlayer internally
            layout.addRight(engineFound);

            // Update the ends so the next move knows what to match
            leftEnd = engineFound.charAt(0) - '0';
            rightEnd = engineFound.charAt(2) - '0';

            // CRITICAL: Switch to the NEXT player after the engine is placed
            //nextPlayer = currentPlayer;
            //currentPlayer = (currentPlayer + 1) % 2;
            //setCurrentPlayer(currentPlayer);

            return "Engine " + engineFound + " placed. Next: " + players[currentPlayer].returnID();
        }
        return "Round resumed";
    }*/

    public String startRound() {

        if (players[0].getHandTiles().isEmpty() &&
                players[1].getHandTiles().isEmpty()) {
            dealTiles();
        }

        if (layout.getChain().isEmpty()) {

            setEIndex();

            String engine = obtainEngine();

            setEngine(engine);

            int tempPlayer = currentPlayer;
            currentPlayer = (currentPlayer + 1) % 2;  // will cycle 0 → 1 → 0
            setCurrentPlayer(currentPlayer);

            layout.addRight(engine);

            leftEnd = engine.charAt(0) - '0';
            rightEnd = engine.charAt(2) - '0';

            return players[tempPlayer].returnID() + " has the engine + ";
        }
        else
        {
            leftEnd = layout.returnLeft();
            rightEnd = layout.returnRight();
        }
        return "Round initiated";
    }
    public void resetStats()
    {
        layout.clearChain();

        setEIndex();

        players[0].emptyHand();
        players[1].emptyHand();

        gameStock.resetBoneyard();


        dealTiles();

        //nextRound();

    }

    public String draw(Player.Move move)
    {
        return players[0].handleDraw(move, gameStock, this, leftEnd, rightEnd);

    }

    public String pass(Player.Move move)
    {
        String passText = players[0].handlePass(move, gameStock);

        final int HUMAN_INDEX = 0;

        if(passText.equals(" "))
        {
            setPassed(HUMAN_INDEX);
        }

        return passText;
    }

    public void nextTurn()
    {
        currentPlayer = getCurrentPlayer();
        setNextPlayer(currentPlayer);

        currentPlayer = (currentPlayer + 1) % 2;  // will cycle 0 → 1 → 0
        resetPass(currentPlayer);
        setCurrentPlayer(currentPlayer);
    }

    public String winPoints(Player winner, Player loser) {
        int total = 0;

        // sum the pips in loser hand
        List<String> loserHand = loser.getHandTiles();

        // convert left and right end to ints to add up total pips on each side
        for (String tile : loserHand) {
            if (tile == null || tile.length() < 3) continue;
            int a = Character.getNumericValue(tile.charAt(0));
            int b = Character.getNumericValue(tile.charAt(2));
            total += a + b;
        }

        winner.addPoints(total);

        // announce the result; actual score bookkeeping should be handled by caller or Tournament
        if (winner.returnID().equals("Human"))
        {
            return "+" + total + " points for human";
        }
        // if computer wins the round
        else if (winner.returnID().equals("Computer"))
        {
            return "+" + total + " points for computer";
        }
        return "";

    }


    public int getHumanScore()
    {
        return players[0].getAddedPoints();
    }

    public int getComputerScore()
    {
        return players[1].getAddedPoints();
    }


    public String getHelp(Player.Move move)
    {
        String advice = players[1].help(players[0], move, gameStock, this, leftEnd, rightEnd);
        return advice;
    }
    public boolean validate(Player.Move move)
    {
        return players[0].checkValidity(move, this, getLayout().returnLeft(), getLayout().returnRight());
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

    public void setEIndex() {
        engineIndex = 0;
        engineIndex = (engineIndex + (roundNum - 1)) % requiredEngines.size();
    }

    public void nextRound() {
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
        for (String tile : playerHand) {
            if (tile.trim().equals(requiredEngine.trim()))
            {
                return tile;
            }
        }
        return "";
    }

    public void determineRequiredEngine() {
        int index = engineIndex;
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
}