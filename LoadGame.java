package com.example.primaryjavalongaga;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

        spinner = findViewById(R.id.selectFile);
        loadButton = findViewById(R.id.loadButton);


        // Only look in SavedGames folder
        File folder = new File(getExternalFilesDir(null), "SavedGames");
        if (!folder.exists()) folder.mkdirs();


        AssetManager am = getAssets();
        try {
            // list all files in assets (or assets/cases if you made a subfolder)
            String[] assetFiles = am.list("");
            if (assetFiles != null) {
                for (String f : assetFiles) {
                    if (f.endsWith(".txt")) { // only text files
                        File outFile = new File(folder, f);
                        if (!outFile.exists()) { // copy only if not already copied
                            try (InputStream in = am.open(f);
                                 OutputStream out = new FileOutputStream(outFile)) {
                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = in.read(buffer)) != -1) {
                                    out.write(buffer, 0, read);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        File[] files = folder.listFiles();
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(".txt")) {
                    fileNames.add(f.getName());
                }
            }
        }

        // Spinner adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, fileNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        loadButton.setOnClickListener(v -> {
            if (spinner.getSelectedItem() == null) return;

            String selected = spinner.getSelectedItem().toString();
            File file = new File(folder, selected);

            // Pass everything to MainActivity as a string
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line).append("\n");

                Intent intent = new Intent(LoadGame.this, MainActivity.class);
                intent.putExtra("LOAD_OPTION", 2);
                intent.putExtra("FILE_CONTENT", sb.toString());
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load file", Toast.LENGTH_SHORT).show();
            }
        });
    }
}