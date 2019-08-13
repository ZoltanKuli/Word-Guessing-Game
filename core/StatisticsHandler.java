package core;

import java.io.*;
import java.util.ArrayList;

class StatisticsHandler {
    enum ResultType {
        RIGHT(1),
        WRONG(3);

        private final int position;

        ResultType (int position) {
            this.position = position;
        }

        int getPosition() {
            return position;
        }
    }

    void updateResult(ResultType position) throws IOException {
        String path = "statistics\\results.txt";

        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        ArrayList<String> results = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (line != null) {
            results.add(line);

            line = bufferedReader.readLine();
        }
        bufferedReader.close();

        int guesses = Integer.parseInt(results.get(position.getPosition()));
        guesses++;
        results.set(position.getPosition(), Integer.toString(guesses));

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path));
        for (String newLine: results) {
            bufferedWriter.write(newLine + "\n");
        }
        bufferedWriter.close();
    }

    void saveGuessedWord(String[] word) throws IOException{
        String path = "statistics\\guessed_words.txt";

        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        ArrayList<String> guessedWords = new ArrayList<>();
        String guessedWord = bufferedReader.readLine();
        while (guessedWord != null) {
            guessedWords.add(guessedWord);

            guessedWord = bufferedReader.readLine();
        }
        bufferedReader.close();

        guessedWords.add(String.join("", word));

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path));
        for (String newGuessedWord: guessedWords) {
            bufferedWriter.write(newGuessedWord + "\n");
        }
        bufferedWriter.close();
    }
}
