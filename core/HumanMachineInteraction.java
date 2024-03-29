package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

class HumanMachineInteraction {
    private Scanner scanner;
    private int wordLength;

    private StatisticsHandler statisticsHandler;

    HumanMachineInteraction() {
        this.scanner = new Scanner(System.in);
        this.statisticsHandler = new StatisticsHandler();
        System.out.println("Welcome to the Word Guessing Game!\nThink of a word, and I'll attempt to guess it.");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
    }

    void scanWordLength() {
        while (true) {
            System.out.print("Between 2 and 15 characters, how long is your word? ");
            int length = scanIntInput();

            if (!trueOnGoodWordLength(length)) {
                continue;
            }

            this.wordLength = length;
            System.out.println(String.format("Your word is %d characters long.", this.wordLength));
            break;
        }
    }

    private boolean trueOnGoodWordLength(int length) {
        if (length < 2) {
            System.out.println("Your chosen word is too short.\n");
            return false;
        } else if (length > 15) {
            System.out.println("Your chosen word is too long.\n");
            return false;
        } else {
            return true;
        }
    }

    private int scanIntInput() {
        int input;

        while (true) {
            try {
                input = this.scanner.nextInt();
                break;
            } catch (InputMismatchException err) {
                this.scanner.next();
                System.out.println("Invalid input.\n");
            }
        }

        return input;
    }

    int getWordLength() {
        return this.wordLength;
    }

    ArrayList<Integer> checkLetter(String letter, String[] word) {
        System.out.println("\n-----------------------------------------------");

        printWordWithUnderscores(word);

        ArrayList<Integer> positions = new ArrayList<>();
        if (trueOnCorrectLetter(letter)) {
            positions = getLetterPositions(letter, word);
        }

        return positions;
    }

    private boolean trueOnCorrectLetter(String letter) {
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

        return doesContain.toLowerCase().contains("y");
    }

    private ArrayList<Integer> getLetterPositions(String letter, String[] word) {
        ArrayList<Integer> positions = new ArrayList<>();

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

            if (!trueOnFullWord(word) && Arrays.asList(word).contains(null)) {
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

        return positions;
    }

    private boolean trueOnFullWord(String[] word) {
        boolean isFull = true;

        for (String character: word) {
            if (character == null) {
                isFull = false;
            }
        }

        return isFull;
    }

    private void printWordWithUnderscores(String[] word) {
        System.out.print("\nYour word:");
        for(String letter: word) {
            if (letter == null) {
                System.out.print(" _");
            } else {
                System.out.print(" " + letter);
            }
        }

        System.out.println();
    }

    void handleResult(boolean areGuessesLeft, String[]  word) {
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        if (areGuessesLeft) {
            checkIfGuessedRight(word);
        } else {
            try {
                System.out.println("I couldn't guess your word.");

                this.statisticsHandler.updateResult(StatisticsHandler.ResultType.WRONG);
            } catch (IOException err) {
                System.out.println("\nCould't update statistics.");
            }
        }

        System.out.println("\nClosing the game.");
    }

    private void checkIfGuessedRight(String[] word) {
        System.out.print("Is this your word? '");
        printWord(word);
        System.out.print("' Y/N: ");

        String input = scanStringInput();

        if (input.toLowerCase().contains("y")) {
            try {
                System.out.println("Great, I guessed your word!");

                this.statisticsHandler.updateResult(StatisticsHandler.ResultType.RIGHT);

                this.statisticsHandler.saveGuessedWord(word);
            } catch (IOException err) {
                System.out.println("\nCould't update statistics.");
            }
        } else {
            try {
                System.out.println("I couldn't guess your word, but maybe next time.");

                this.statisticsHandler.updateResult(StatisticsHandler.ResultType.WRONG);
            } catch (IOException err) {
                System.out.println("\nCould't update statistics.");
            }
        }
    }
    
    private void printWord(String[] word) {
        for (String letter: word) {
            System.out.print(letter);
        }
    }

    private String scanStringInput() {
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

        return input;
    }
}
