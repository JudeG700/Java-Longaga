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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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

        // 1. Get the folder
        File folder = new File(getExternalFilesDir(null), "SavedGames");
        if (!folder.exists()) folder.mkdirs();

// 2. Define your 3 cases
        String[] filenames = {"case1.txt", "case2.txt", "case3.txt"};
        String[] contents = {
                "Tournament Score: 100\n" +
                        "Round No.: 2\n" +
                        "\n" +
                        "Computer:\n" +
                        "   Hand: 0-2 0-6 3-4 1-4 0-0 0-1 2-3 3-3 \n" +
                        "   Score: 27\n" +
                        "\n" +
                        "Human:\n" +
                        "   Hand: 6-6 4-4 0-3 2-4 1-1 0-4 1-3 2-2 \n" +
                        "   Score: 0\n" +
                        "\n" +
                        "Layout:\n" +
                        "  L R\n" +
                        "\n" +
                        "Boneyard:\n" +
                        "1-2 3-6 2-6 5-5 1-6 4-6 4-5 2-5 3-5 0-5 1-5 5-6 \n" +
                        "\n" +
                        "Previous Player Passed: \n" +
                        "\n" +
                        "Next Player: \n", // Case 1 data
                "Tournament Score: 120\n" +
                        "Round No.: 4\n" +
                        "\n" +
                        "Computer:\n" +
                        "   Hand: 3-5 5-6 2-5 0-5 1-5  \n" +
                        "   Score: 119\n" +
                        "\n" +
                        "Human:\n" +
                        "   Hand: 2-4 0-4 3-4 0-2  \n" +
                        "   Score: 119\n" +
                        "\n" +
                        "Layout:\n" +
                        "L 5-4 4-6 6-6 6-0 0-0 0-3 3-3 3-6 6-1 1-2 2-3 3-1 1-4  R\n" +
                        "\n" +
                        "Boneyard:\n" +
                        "5-5 4-4 0-1 1-1 2-2 2-6  \n" +
                        "\n" +
                        "Previous Player Passed: No\n" +
                        "\n" +
                        "Next Player: Computer\n",       // Case 2 data
                "Tournament Score: 150\n" +
                        "Round No.: 7\n" +
                        "\n" +
                        "Computer:\n" +
                        "   Hand: 5-6 0-2   \n" +
                        "   Score: 100\n" +
                        "\n" +
                        "Human:\n" +
                        "   Hand: 2-4 1-1 2-2 2-6 \n" +
                        "   Score: 120\n" +
                        "\n" +
                        "Layout:\n" +
                        "L 5-4 4-6 6-6 6-0 0-0 0-3 3-3 3-6 6-1 1-2 2-3 3-1 1-4  R\n" +
                        "\n" +
                        "Boneyard:\n" +
                        "0-1 4-4 0-4 5-5 3-4 2-5 0-5 1-5 3-5 \n" +
                        "\n" +
                        "Previous Player Passed: No\n" +
                        "\n" +
                        "Next Player: Computer\n"    // Case 3 data
        };

        for (int i = 0; i < filenames.length; i++) {
            File file = new File(folder, filenames[i]);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(contents[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        spinner = findViewById(R.id.selectFile);
        loadButton = findViewById(R.id.loadButton);


        // Only look in SavedGames folder
        /*File folder = new File(getExternalFilesDir(null), "SavedGames");
        if (!folder.exists())
        {
            folder.mkdirs();
        } */



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
                System.out.println("File: " + f);
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