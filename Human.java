package com.example.test2java;

import java.util.*;

public class Human extends Player {

    private Scanner input = new Scanner(System.in);

    // Constructors
    public Human() {
        super();
    }

    public Human(Hand humanHand) {
        super(humanHand);
    }

    // returnID
    @Override
    public String returnID() {
        return ID;
    }

    @Override
    public void addScore(int points)
    {
        score += points;
    }

    @Override
    public int getScore()
    {
        return score;
    }

    // parseTile
    @Override
    public Pips parseTile(String tile) {
        int dash = tile.indexOf('-');

        if (dash == -1)
            return new Pips(-1, -1);

        //the reason this always works is because you can only input tiles in the form of "a-b";
        //it simply won't allow you to input anything other than the tile
        int left = Integer.parseInt(tile.substring(0, dash));
        int right = Integer.parseInt(tile.substring(dash + 1));

        return new Pips(left, right);
    }

    // findPlayableTiles
    @Override
    public List<Player.PlayableOption> findPlayableTiles(Hand hand, Round gameRound, int leftEnd, int rightEnd) {

        List<Player.PlayableOption> playableTiles = new ArrayList<>();
        List<String> tiles = hand.getHandTiles();

        final int COMPUTER_INDEX = 1;
        boolean oppPassed = gameRound.isPassed(COMPUTER_INDEX);

        for (int i = 0; i < tiles.size(); i++) {
            Pips p = parseTile(tiles.get(i));
            boolean isDouble = (p.left == p.right);

            boolean matchesLeft = (p.left == leftEnd || p.right == leftEnd);
            boolean matchesRight = (p.left == rightEnd || p.right == rightEnd);

            if (matchesLeft) {
                playableTiles.add(new PlayableOption(i, 'L'));
            }

            if (matchesRight && (isDouble || oppPassed)) {
                playableTiles.add(new PlayableOption(i, 'R'));
            }
        }

        return playableTiles;
    }

    // getValidatedChoice
    public int getValidatedChoice(int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(input.nextLine());

                if (value < min || value > max) {
                    System.out.println("Enter between " + min + " and " + max);
                    continue;
                }

                return value;

            } catch (Exception e) {
                System.out.println("Invalid input!");
            }
        }
    }

    // getValidatedSide
    public char getValidatedSide(Move move, Stock gameStock, Round gameRound, int leftEnd, int rightEnd) {

        Computer helper = new Computer();
        while (true) {
            System.out.print("Side L or R: (Press H for help)");
            String line = input.nextLine().toUpperCase();

            if (line.equals("H")) {
                helper.help(this, move, gameStock, gameRound, leftEnd, rightEnd);
                continue;
            }

            if (line.equals("L") || line.equals("R")) {
                return line.charAt(0);
            }

            System.out.println("Invalid side.");
        }
    }

    // getValidatedTileFromHand
    public String getValidatedTileFromHand() {
        List<String> tiles = getHandTiles();

        while (true) {
            System.out.print("Enter tile: ");
            String chosen = input.nextLine();

            if (tiles.contains(chosen)) {
                return chosen;
            }

            System.out.println(chosen + " isn't in your hand.");
        }
    }

    // matchesLeft
    public boolean matchesLeft(Pips p, int leftEnd) {
        return (p.left == leftEnd || p.right == leftEnd);
    }

    // matchesRight
    public boolean matchesRight(Pips p, int rightEnd) {
        return (p.left == rightEnd || p.right == rightEnd);
    }

    // canPlayRight
    public boolean canPlayRight(Pips p, int rightEnd, Round gameRound) {

        final int COMPUTER_INDEX = 1;
        boolean oppPassed = gameRound.isPassed(COMPUTER_INDEX);
        boolean isDouble = (p.left == p.right);

        if (!matchesRight(p, rightEnd)) {
            System.out.println("Doesn't match right.");
        }

        if (!oppPassed && !isDouble) {
            System.out.println("Opponent hasn't passed.");
        }

        return matchesRight(p, rightEnd) && (isDouble || oppPassed);
    }

    // handleDraw
    public void handleDraw(Move move, Stock gameStock, Round gameRound, int leftEnd, int rightEnd) {

        String drawnTile = gameStock.drawTile();
        addTile(drawnTile);

        System.out.println(returnID() + " drew " + drawnTile);

        Pips p = parseTile(drawnTile);

        boolean canLeft = matchesLeft(p, leftEnd);
        boolean canRight = canPlayRight(p, rightEnd, gameRound);

        move.draw = true;

        if (canLeft && canRight) {
            move.side = getValidatedSide(move, gameStock, gameRound, leftEnd, rightEnd);
            move.chosenTile = drawnTile;
        } else if (canLeft) {
            move.side = 'L';
            move.chosenTile = drawnTile;
        } else if (canRight) {
            move.side = 'R';
            move.chosenTile = drawnTile;
        } else {
            System.out.println("Sorry. You can't play " + drawnTile);
            move.draw = false;
            move.passed = true;
        }
    }

    // takeTurn
    @Override
    public Move takeTurn(Stock gameStock, Round gameRound, int leftEnd, int rightEnd) {

        Move move = new Move();

        move.draw = false;
        move.passed = false;
        move.help = false;
        move.chosenTile = "";
        move.side = ' ';
        move.hasPlayableTiles = false;
        move.choseSave = false;

        List<Player.PlayableOption> playableList = findPlayableTiles(getHand(), gameRound, leftEnd, rightEnd);

        if (!playableList.isEmpty()) {
            move.hasPlayableTiles = true;
        }

        boolean choiceValid = false;

        while (!choiceValid) {

            System.out.println("1=Play 2=Draw 3=Pass 4=Help 5=Save");
            int choice = getValidatedChoice(1, 5);

            if (choice == 1) {

                //get the inputted tile and side, validate them, and update the move object accordingly
                move.chosenTile = getValidatedTileFromHand();
                Pips p = parseTile(move.chosenTile);

                move.side = getValidatedSide(move, gameStock, gameRound, leftEnd, rightEnd);

                if (move.side == 'L') {
                    if (matchesLeft(p, leftEnd)) {
                        choiceValid = true;
                    } else {
                        System.out.println("Invalid left move.");
                    }
                } else {
                    if (canPlayRight(p, rightEnd, gameRound)) {
                        choiceValid = true;
                    } else {
                        System.out.println("Invalid right move.");
                    }
                }

            } else if (choice == 2) {

                if (gameStock.getBoneyard().isEmpty()) {
                    System.out.println("Can't draw.");
                    continue;
                }

                if (move.hasPlayableTiles) {
                    System.out.println("You have playable tiles.");
                    continue;
                }

                handleDraw(move, gameStock, gameRound, leftEnd, rightEnd);
                choiceValid = true;

            } else if (choice == 3) {

                /*
                boolean normalPass =
                        gameStock.getBoneyard().isEmpty() &&
                        !move.hasPlayableTiles;

                if (normalPass) {
                    move.passed = true;
                    choiceValid = true;
                } else {
                    System.out.println("Cannot pass.");
                } */

                //debug only
                move.passed = true;
                choiceValid = true;

            } else if (choice == 4) {

                Computer helper = new Computer();
                helper.help(this, move, gameStock, gameRound, leftEnd, rightEnd);

                choiceValid = false;
            }
            else if (choice == 5) {

                int inp;

                do {
                    System.out.println("Would you like to save?");
                    System.out.println("1. Yes");
                    System.out.println("2. No");
                    System.out.println("Note: Saving here will exit the game");

                    while (!input.hasNextInt()) {
                        System.out.println("Invalid input!");
                        input.next();
                    }

                    inp = input.nextInt();

                } while (inp < 1 || inp > 2);

                if(inp == 1)
                {

                    move.choseSave = true;
                    choiceValid = true;
                }
                else
                {
                    choiceValid = false;
                }
            }
        }

        return move;
    }
}