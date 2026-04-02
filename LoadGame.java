package com.example.primaryjavalongaga;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LoadGame extends AppCompatActivity {

    // 1. Declare at the CLASS LEVEL so the button can "see" them
    private Spinner spinner;
    private Button loadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_file);

        // 2. Initialize the views
        spinner = findViewById(R.id.selectFile);
        loadButton = findViewById(R.id.loadButton);

        // 3. Get the raw files
        Field[] fields = R.raw.class.getFields();
        List<String> fileNames = new ArrayList<>();
        for (Field field : fields) {
            fileNames.add(field.getName());
        }

        // 4. Setup the Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, fileNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 5. The Click Listener (The actual "Go" button)
        loadButton.setOnClickListener(v -> {
            if (spinner.getSelectedItem() != null) {
                // IMPORTANT: Use capital "S" in String
                String selectedFileName = spinner.getSelectedItem().toString();

                //basically, this code here is meant to transform the data files into a single concatenated structure to be parsed
                try {
                    // Turn the name (string) into the actual ID (number)
                    int resID = getResources().getIdentifier(selectedFileName, "raw", getPackageName());
                    InputStream is = getResources().openRawResource(resID);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }

                    // Package and send to MainActivity
                    Intent intent = new Intent(LoadGame.this, MainActivity.class);
                    intent.putExtra("FILE_CONTENT", sb.toString());
                    intent.putExtra("LOAD_OPTION", 2);
                    intent.putExtra("FILE_NAME", resID);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}