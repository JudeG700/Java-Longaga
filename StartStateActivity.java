package com.example.test2java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

// 1. "extends" makes this a real Android Screen
public class StartStateActivity extends AppCompatActivity {

    // 2. This is the method the Phone calls automatically
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // 3. This links your Java to your XML layout file
        // Make sure you have a file named activity_start.xml in res/layout!
        setContentView(R.layout.activity_start);

        //initialize classes
        //currentRound = new Round();

        // 4. Find the button you made in XML (we'll assume the ID is "start_button")
        Button newGButton = findViewById(R.id.new_game);
        Button loadGButton = findViewById(R.id.load_game);

        // 5. This is your "Event Listener" - what happens when you click?
        newGButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is the "Bridge" to the next screen
                Intent intent = new Intent(StartStateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        loadGButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is the "Bridge" to the next screen
                Intent intent = new Intent(StartStateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}