package com.example.primaryjavalongaga;
import com.example.primaryjavalongaga.Controller.models.Round;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class GameView {

    private Activity activity;

    public interface OnTileClickListener {
        void onTileClicked(String tile);
    }

    private OnTileClickListener listener;

    public void setOnTileClickListener(OnTileClickListener listener) {
        this.listener = listener;
    }


    public GameView(Activity activity) {
        this.activity = activity;
    }

    private TextView createTileView(String tile) {
        TextView tv = new TextView(this.activity);
        tv.setText(tile);
        tv.setPadding(20, 20, 20, 20);
        return tv;
    }
    public void refreshUI(Round currentRound) {

        LinearLayout humHand = activity.findViewById(R.id.humanHand);
        LinearLayout compHand = activity.findViewById(R.id.computerHand);
        LinearLayout boneyard = activity.findViewById(R.id.boneyard);
        LinearLayout board = activity.findViewById(R.id.layout);

        humHand.removeAllViews();
        compHand.removeAllViews();
        board.removeAllViews();
        boneyard.removeAllViews();

        TextView scoreView = activity.findViewById(R.id.scoreText);
        TextView statusView = activity.findViewById(R.id.statusText);

        // Score
        int hScore = currentRound.getHumanScore();
        int cScore = currentRound.getComputerScore();
        scoreView.setText("Score - Human: " + hScore + " | Computer: " + cScore);

        // Status
        StringBuilder status = new StringBuilder();

        if (currentRound.isPassed(0)) {
            status.append("Human PASSED. ");
        }
        if (currentRound.isPassed(1)) {
            status.append("Computer PASSED. ");
        }

        if (status.length() == 0) {
            status.append("Game in progress...");
        }

        statusView.setText(status.toString());

        // Draw hands + board
        drawTiles(humHand, currentRound.getHumanPlayer().getHandTiles());
        drawTiles(compHand, currentRound.getComputerPlayer().getHandTiles());
        drawTiles(board, currentRound.getLayout().getChain());
        drawTiles(boneyard, currentRound.getGameStock().getBoneyard());

        for (String tile : currentRound.getHumanPlayer().getHandTiles()) {
            TextView tv = createTileView(tile);
            tv.setOnClickListener(v -> {
                // Controller logic
                listener.onTileClicked(tile);
                //currentMove.chosenTile = tile;

                // STEP 2: Show the L/R buttons. NO AUTO-PLAY.
                activity.findViewById(R.id.sideButtonsContainer).setVisibility(View.VISIBLE);

                // STEP 3: Optional - highlight the selected tile so you know it's active
                //iv.setBackgroundColor(android.graphics.Color.YELLOW);
            });
            humHand.addView(tv);
        }

        // Draw Computer Hand (NOT Clickable - use a placeholder or the tile string)
        for (String tile : currentRound.getComputerPlayer().getHandTiles()) {
            TextView tv = createTileView(tile);
            compHand.addView(tv); // Or tile if you want to see them
        }

        // Draw Board (Layout)
        for (String tile : currentRound.getLayout().getChain()) {
            TextView tv = createTileView(tile);
            board.addView(tv);
        }

        for (String tile : currentRound.getGameStock().getBoneyard()) {
            TextView tv = createTileView(tile);
            boneyard.addView(tv);
        }
    }

    private void drawTiles(LinearLayout container, java.util.List<String> tiles) {
        for (String tile : tiles) {
            TextView tv = new TextView(activity);
            tv.setText(tile);
            tv.setPadding(20, 20, 20, 20);
            container.addView(tv);
        }
    }







}