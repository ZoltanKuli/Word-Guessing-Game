package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class AssetLoader {
    private ArrayList<String> possibleWords;

    AssetLoader (String path, int wordLength) {
        try {
            setPossibleWords(path, wordLength);
        } catch (IOException err) {
            System.out.println("\nOops, couldn't access assets.\nAbility to guess is compromised.");
            this.possibleWords = new ArrayList<>();
        }
    }

    private void setPossibleWords(String path, int length) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        this.possibleWords = new ArrayList<>();

        String word = bufferedReader.readLine();
        while (word != null) {
            if (word.length() == length) {
                possibleWords.add(word);
            }

            word = bufferedReader.readLine();
        }

        bufferedReader.close();
    }

    ArrayList<String> getPossibleWords() {
        return this.possibleWords;
    }
}
