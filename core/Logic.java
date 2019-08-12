package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Logic {
    private static final String possibleWordsPath;
    private static final String alphabet;

    private HumanMachineInteraction humanMachineInteraction;
    private int guessesLeft;

    private ArrayList<String> possibleWords;
    private ArrayList<String> possibleLetters;
    private String[] word;

    static {
        possibleWordsPath = "src\\assets\\words-20000.txt";

        alphabet = "abcdefghijklmnopqrstuvwxyz";
    }

    public Logic () {
        this.humanMachineInteraction = new HumanMachineInteraction();
        int wordLength = this.humanMachineInteraction.getWordLength();

        this.word = new String[wordLength];
        setPossibleWords(wordLength);
        setPossibleLetters();
        setMaximumGuesses();

        guessLoop();
    }

    private void setPossibleWords(int wordLength) {
        try {
            FileReader fileReader = new FileReader(Logic.possibleWordsPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            this.possibleWords = new ArrayList<>();
            String word = bufferedReader.readLine();
            while (word != null) {
                if (word.length() == wordLength) {
                    this.possibleWords.add(word);
                }

                word = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException err) {
            System.out.println("\nOops, couldn't access assets.\nAbility to guess is compromised.");
            this.possibleWords = new ArrayList<>();
        }
    }

    private void setPossibleLetters() {
        this.possibleLetters = new ArrayList<>(Arrays.asList(alphabet.split("")));
    }

    private void setMaximumGuesses() {
        int wordLength = this.word.length;
        if (wordLength <= 5) {
            this.guessesLeft = 12;
        } else if (wordLength <= 10) {
            this.guessesLeft = 16;
        } else if (wordLength <= 15) {
            this.guessesLeft = 20;
        }
    }

    private void guessLoop() {
        boolean isGuessed;
        do {
            if (this.possibleWords.size() == 1) {
                isGuessed = true;
                this.word = this.possibleWords.get(0).split("");
                break;
            }

            int nextLetterIndex = getNextLetterIndex();
            ArrayList<Integer> positions = humanMachineInteraction.handleGuessInteraction(
                    this.possibleLetters.get(nextLetterIndex), this.word);

            if (!positions.isEmpty()) {
                for (int position: positions) {
                    this.word[position] = this.possibleLetters.get(nextLetterIndex);
                }

                removeWordsNotContainingRightGuess(this.possibleLetters.get(nextLetterIndex));
            } else {
                removeWordsContainingWrongGuess(this.possibleLetters.get(nextLetterIndex));
                this.guessesLeft--;
            }

            this.possibleLetters.remove(nextLetterIndex);

            isGuessed = true;
            for (String letter: this.word) {
                if (letter == null) {
                    isGuessed = false;
                }
            }

        } while (!isGuessed && this.guessesLeft > 0);

        this.humanMachineInteraction.result(isGuessed, this.word);
    }

    private int getNextLetterIndex() {
        int nextLetterIndex;

        if (!this.possibleWords.isEmpty()) {
            int[] numOfLetters = new int[this.possibleLetters.size()];
            int mostCommonLetterIndex = 0;

            ArrayList<String> moreCommonPossibleWords = new ArrayList<>(
                    this.possibleWords.subList(0, (this.possibleWords.size() / 2 + this.possibleWords.size() / 4)));

            for (String possibleWord: moreCommonPossibleWords) {
                for (String letter: possibleWord.split("")) {
                    if (this.possibleLetters.contains(letter)) {
                        numOfLetters[this.possibleLetters.indexOf(letter)]++;
                    }
                }
            }

            for (int i = 1; i < numOfLetters.length; i++) {
                if (numOfLetters[i] > numOfLetters[mostCommonLetterIndex]) {
                    mostCommonLetterIndex = i;
                }
            }

            nextLetterIndex = mostCommonLetterIndex;
        } else {
            nextLetterIndex = new Random().nextInt(this.possibleLetters.size());
        }

        return nextLetterIndex;
    }

    private void removeWordsContainingWrongGuess(String letter) {
        for (int i = 0; i < this.possibleWords.size(); i++) {
            if (this.possibleWords.get(i).contains(letter)) {
                this.possibleWords.remove(i);
                i--;
            }
        }
    }

    private void removeWordsNotContainingRightGuess(String letter) {
        for (int i = 0; i < this.word.length; i++) {
            if (this.word[i] != null && this.word[i].equals(letter)) {
                for (int j = 0; j < this.possibleWords.size(); j++) {
                    if (this.possibleWords.get(j).charAt(i) != letter.charAt(0)) {
                        this.possibleWords.remove(j);
                        j--;
                    }
                }
            }
        }
    }
}
