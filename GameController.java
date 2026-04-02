package com.example.primaryjavalongaga;

import com.example.primaryjavalongaga.Controller.models.Player;
import com.example.primaryjavalongaga.Controller.models.Round;
import com.example.primaryjavalongaga.Controller.models.Tournament;

public class GameController {


    private Round currentRound;
    private Tournament currentTourn;

    public GameController() {
        currentTourn = new Tournament();
        currentRound = new Round();
    }

    public void startRound() {
        currentRound.startRound();
    }

    public Round getRound() {
        return currentRound;
    }

    public void onDominoClicked(String tile) {
        Player.Move move = new Player.Move();
        move.chosenTile = tile;

        /*
        if (currentRound.validate(move)) {
            currentRound.updateMove(move);
            //updateUI();
        } */
    }


    /*public void refreshUI()
    {
        gameView.refreshUI(currentRound);
    } */



}
