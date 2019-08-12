package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Logic {
    private HumanMachineInteraction humanMachineInteraction;

    private ArrayList<String> possibleWords;
    private ArrayList<String> possibleLetters;
    private int guessesLeft;
    private String[] word;

    public Logic () {
        this.humanMachineInteraction = new HumanMachineInteraction();
        this.humanMachineInteraction.scanWordLength();
        int wordLength = this.humanMachineInteraction.getWordLength();

        this.word = new String[wordLength];

        AssetLoader assetLoader = new AssetLoader("assets\\words-20000.txt", wordLength);
        this.possibleWords = assetLoader.getPossibleWords();

        setPossibleLetters();

        setMaximumGuesses();
    }

    private void setPossibleLetters() {
        this.possibleLetters = new ArrayList<>(Arrays.asList("abcdefghijklmnopqrstuvwxyz".split("")));
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

    public void run() {
        boolean isRunning;

        do {
            String possibleLetter = getPossibleLetter();
            ArrayList<Integer> letterIndexes = humanMachineInteraction.checkLetter(possibleLetter, this.word);

            updateGuessAssets(possibleLetter, letterIndexes);

            GuessOnOnePossibleWord();
            isRunning = !trueOnWordFull();
        } while (isRunning && this.guessesLeft > 0);

        this.humanMachineInteraction.handleResult(this.guessesLeft > 0, this.word);
    }

    private void GuessOnOnePossibleWord() {
        if (this.possibleWords.size() == 1) {
            this.word = this.possibleWords.get(0).split("");
        }
    }

    private String getPossibleLetter() {
        if (!this.possibleWords.isEmpty()) {
            return this.possibleLetters.get(getMostCommonLetterIndex());
        } else {
            return this.possibleLetters.get(ThreadLocalRandom.current().nextInt(this.possibleLetters.size()));
        }
    }

    private int getMostCommonLetterIndex() {
        int[] numOfLetters = getNumOfLetters();
        int mostCommonLetterIndex = 0;

        for (int i = 1; i < numOfLetters.length; i++) {
            if (numOfLetters[i] > numOfLetters[mostCommonLetterIndex]) {
                mostCommonLetterIndex = i;
            }
        }

        return mostCommonLetterIndex;
    }

    private int[] getNumOfLetters() {
        int[] numOfLetters = new int[this.possibleLetters.size()];

        ArrayList<String> moreCommonPossibleWords = new ArrayList<>(
                this.possibleWords.subList(0, (this.possibleWords.size() / 2 + this.possibleWords.size() / 4)));

        for (String possibleWord: moreCommonPossibleWords) {
            for (String letter: possibleWord.split("")) {
                if (this.possibleLetters.contains(letter)) {
                    numOfLetters[this.possibleLetters.indexOf(letter)]++;
                }
            }
        }

        return numOfLetters;
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

    private boolean trueOnWordFull() {
        boolean isWordFull = true;

        for (String letter: this.word) {
            if (letter == null) {
                isWordFull = false;
            }
        }

        return isWordFull;
    }

    private void updateGuessAssets(String possibleLetter, ArrayList<Integer> letterPositions) {
        if (!letterPositions.isEmpty()) {
            for (int position: letterPositions) {
                this.word[position] = possibleLetter;
            }

            removeWordsNotContainingRightGuess(possibleLetter);
        } else {
            removeWordsContainingWrongGuess(possibleLetter);

            this.guessesLeft--;
        }

        this.possibleLetters.remove(possibleLetter);
    }
}
