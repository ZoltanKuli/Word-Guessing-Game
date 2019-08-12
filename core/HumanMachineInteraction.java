package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

class HumanMachineInteraction {
    private static final String resultsPath;
    private static final String guessedWordsPath;

    private Scanner scanner;
    private final int wordLength;

    static {
        resultsPath = "src\\statistics\\results.txt";
        guessedWordsPath = "src\\statistics\\guessed_words.txt";
    }

    HumanMachineInteraction() {
        this.scanner = new Scanner(System.in);
        System.out.println("Welcome to the Word Guessing Game!\nThink of a word, and I'll attempt to guess it.");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        while (true) {
            int input;
            System.out.print("Between 2 and 15 characters, how long is your word? ");
            try {
                input = this.scanner.nextInt();
            } catch (InputMismatchException err) {
                this.scanner.next();
                System.out.println("Invalid input.\n");
                continue;
            }

            if (input < 2) {
               System.out.println("Your chosen word is too short.\n");
               continue;
            } else if (input > 15) {
                System.out.println("Your chosen word is too long.\n");
                continue;
            }

            this.wordLength = input;
            System.out.println(String.format("Your word is %d characters long.", this.wordLength));
            break;
        }
    }

    int getWordLength() {
        return this.wordLength;
    }

    ArrayList<Integer> handleGuessInteraction(String letter, String[] word) {
        System.out.println("\n-----------------------------------------------");
        String doesContain;
        while (true) {
            System.out.print(String.format("\nDoes your word contain the letter '%s'? Y/N: ", letter));
            try {
                doesContain = this.scanner.next();
            } catch (InputMismatchException err) {
                this.scanner.next();
                System.out.println("Invalid input.");
                continue;
            }

            break;
        }

        ArrayList<Integer> positions = new ArrayList<>();
        if (doesContain.toLowerCase().contains("y")) {
            while (true) {
                System.out.print(String.format("\nBetween 1 and %d,", this.wordLength));
                System.out.print(String.format(" at which position is the letter '%s'? ", letter));
                try {
                    int position = this.scanner.nextInt() - 1;

                    if (position < 0) {
                        System.out.println("The position you specified is too small.");
                        continue;
                    } else if (position >= this.wordLength) {
                        System.out.println("The position you specified is too large.");
                        continue;
                    }

                    if (word[position] != null | positions.contains(position)) {
                        System.out.println("You've already specified this position.");
                        continue;
                    }

                    positions.add(position);
                    word[position] = letter;
                } catch (InputMismatchException err) {
                    this.scanner.next();
                    System.out.println("Invalid input.");
                    continue;
                }

                boolean isGuessed = true;
                for (String ch: word) {
                    if (ch == null) {
                        isGuessed = false;
                    }
                }

                if (!isGuessed && Arrays.asList(word).contains(null)) {
                    System.out.print(String.format("\nDoes your word contain the letter '%s' at another position? Y/N: ", letter));
                    try {
                        String doesStillContain = this.scanner.next();
                        if (!doesStillContain.toLowerCase().contains("y")) {
                            break;
                        }
                    } catch (InputMismatchException err) {
                        this.scanner.next();
                        System.out.println("Invalid input.");
                    }
                } else {
                    break;
                }
            }
        }

        return positions;
    }

    void result(boolean isGuessed, String[]  word) {
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        if (isGuessed) {
            System.out.print("Is this your word? '");
            for (String letter: word) {
                System.out.print(letter);
            }
            System.out.print("' Y/N: ");

            String input;
            while (true) {
                try {
                    input = this.scanner.next();
                    break;
                } catch (InputMismatchException err) {
                    this.scanner.next();
                    System.out.println("Invalid input.\n");
                }
            }

            if (input.toLowerCase().contains("y")) {
                try {
                    updateResult(ResultType.RIGHT);

                    saveGuessedWord(word);
                } catch (IOException err) {
                    System.out.println("Could't update statistics.\n");
                }

                System.out.println("Great, I guessed your word!");
            } else {
                try {
                    updateResult(ResultType.WRONG);
                } catch (IOException err) {
                    System.out.println("Could't update statistics.\n");
                }

                System.out.println("I couldn't guess your word, but maybe next time.");
            }
        } else {
            try {
                updateResult(ResultType.WRONG);
            } catch (IOException err) {
                System.out.println("Could't update statistics.\n");
            }

            System.out.print("I couldn't guess your word.");
        }
    }

    private enum ResultType {
        RIGHT(1),
        WRONG(3);

        final int position;

        ResultType (int position) {
            this.position = position;
        }

       int getPosition() {
            return position;
       }
    }

    private void updateResult(ResultType position) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(HumanMachineInteraction.resultsPath));
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

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(HumanMachineInteraction.resultsPath));
        for (String newLine: results) {
            bufferedWriter.write(newLine + "\n");
        }
        bufferedWriter.close();
    }

    private void saveGuessedWord(String[] word) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new FileReader(HumanMachineInteraction.guessedWordsPath));
        ArrayList<String> guessedWords = new ArrayList<>();
        String guessedWord = bufferedReader.readLine();
        while (guessedWord != null) {
            guessedWords.add(guessedWord);

            guessedWord = bufferedReader.readLine();
        }
        bufferedReader.close();

        guessedWords.add(String.join("", word));

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(HumanMachineInteraction.guessedWordsPath));
        for (String newGuessedWord: guessedWords) {
            bufferedWriter.write(newGuessedWord + "\n");
        }
        bufferedWriter.close();
    }
}
