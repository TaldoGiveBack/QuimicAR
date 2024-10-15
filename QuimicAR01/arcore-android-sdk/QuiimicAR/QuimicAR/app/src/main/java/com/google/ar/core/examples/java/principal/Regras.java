package com.google.ar.core.examples.java.principal;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.ar.core.examples.java.principal.databinding.RulesActivityBinding;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Regras extends AppCompatActivity {
    private RulesActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RulesActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String filename = getIntent().getStringExtra("FILENAME");
        String fileContent = readTextFileFromAssets(filename);
        binding.textView.setText(fileContent);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private String readTextFileFromAssets(String filename) {
        if (filename != null) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(getAssets().open(filename));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                bufferedReader.close();
                return content.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "Erro ao ler o arquivo";
            }
        } else {
            return "Arquivo não encontrado";
        }
    }
}
