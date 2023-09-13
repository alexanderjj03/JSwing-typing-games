package TypeGameModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.List;

/*
 * Represents an instance of a falling words game (specification provided below).
 */
public class FallingWordsGame {
    private static final Random rand = new Random();

    private boolean running; // to ensure that charTyped executes to completion before it is called again
    private int correctChars;
    private int charsTyped;
    private int accuracy;
    private int livesLeft; // starts at 3
    private int difficulty;
    private int wordsLeft; // Number of words that need to be typed for the user to win.
    private boolean gameWon;
    private boolean gameLost;

    private boolean wordSelected; // true if a word on screen is in the process of being typed.
    private TypePassage curPassage; // the word that is being typed

    private List<TypePassage> wordPassages; // list of single-word typePassages on screen
    private List<Integer> xPosList; // X coordinates of the words on screen
    private List<Integer> yPosList; // Y coordinates of the words on screen
    private List<String> selectedWords; // the actual words that are on screen
    private List<String> allWords; // every word in wordlist.10000

    // Constructs a falling words game. The idea is for random words to show up at the top of the screen and for them
    // to gradually fall to the bottom. The user must finish typing them before this happens. If a word touches the
    // bottom, the user loses a life. Once lives run out, the user loses. If they type enough words without losing all
    // their lives, the user wins.
    // REQUIRES: Difficulty can be 1, 2, or 3.
    public FallingWordsGame (int diff) {
        wordPassages = new ArrayList<>();
        xPosList = new ArrayList<>();
        yPosList = new ArrayList<>();
        selectedWords = new ArrayList<>();
        allWords = new ArrayList<>();
        correctChars = 0;
        charsTyped = 0;
        accuracy = 100;
        difficulty = diff;
        wordsLeft = 10 + 15 * diff; // Starts at 25 for easy, 40 for medium. 55 for hard.
        running = false;
        livesLeft = 3;
        gameWon = false;
        gameLost = false;

        try {
            BufferedReader bf = new BufferedReader(new FileReader("wordlist.10000.txt"));
            String line = bf.readLine();
            while (line != null) {
                allWords.add(line);
                line = bf.readLine();
            }
            bf.close();
        } catch (Exception e) {
            System.out.println("Error");
        } // reading the list of 10,000 words and storing all of them onto allWords

        String chosenWord = allWords.get(rand.nextInt(allWords.size()));
        wordPassages.add(new TypePassage(chosenWord)); // add a random word from allWords to start the game off.
        selectedWords.add(chosenWord);
        xPosList.add(rand.nextInt(600) + 50); // randomly select the word's x coordinate from 50 to 650.
        yPosList.add(80);
    }

    public int getCorrectChars() {
        return correctChars;
    }

    public int getCharsTyped() {
        return charsTyped;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getLivesLeft() {
        return livesLeft;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getWordsLeft() {
        return wordsLeft;
    }

    public boolean getGameWon() {
        return gameWon;
    }

    public boolean getGameLost() {
        return gameLost;
    }

    public List<TypePassage> getWordPassages() {
        return wordPassages;
    }

    public List<Integer> getXPosList() {
        return xPosList;
    }

    public List<Integer> getYPosList() {
        return yPosList;
    }

    public List<String> getSelectedWords() {
        return selectedWords;
    }

    // Calculates the current typing game accuracy (correct chars/chars typed)
    public void calcAccuracy() {
        if (charsTyped == 0) {
            accuracy = 100;
        } else {
            accuracy = (int) Math.round((100.0 * correctChars) / charsTyped);
        }
    }

    // Called on every tick. Shifts the words down the screen by a certain number of pixels (determined by the game's
    // difficulty. If there is a word at the bottom, remove it and deduct a life. Game is intentionally designed so
    // at most one word will be at the bottom of the screen at any given time
    public void shiftWordsDown() {
        int indexToRemove = -1;
        for (int i = 0; i<yPosList.size(); i++) {
            yPosList.set(i, yPosList.get(i) + 5); // shift words down by 1+2*difficulty pixels
            if (yPosList.get(i) >= 800) {
                indexToRemove = i; // index (within wordPassages) of the word to remove
            }
        }

        if (indexToRemove > -1) { // if there is a word to remove, remove it and deduct a life.
            if (wordPassages.get(indexToRemove).equals(curPassage)) { // if a word is currently being typed as it
                // hits the bottom of the screen...
                wordSelected = false;
            }
            wordPassages.remove(indexToRemove);
            selectedWords.remove(indexToRemove);
            xPosList.remove(indexToRemove);
            yPosList.remove(indexToRemove);
            livesLeft--;
            if (livesLeft == 0) {
                gameLost = true;
            }
        }
    }

    // Called every fixed number of ticks (and every 20 correct characters). Adds a random word from allWords whose
    // first letter is unique (no 2 words on screen will have the same first letter). There can be a maximum of
    // 10 words on screen.
    public void addNewWord() {
        if (selectedWords.size() > 10) {
            return;
        } else {
            String chosenWord = " "; // placeholder
            boolean wordFound = false; // true when a suitable radom word has been found.
            while (!wordFound) { // continually find random words until there's one with a unique first letter.
                boolean dupeFirstLetter = false;
                chosenWord = allWords.get(rand.nextInt(allWords.size()));

                for (String s : selectedWords) {
                    if (s.substring(0, 1).equalsIgnoreCase(chosenWord.substring(0, 1))) {
                        dupeFirstLetter = true;
                    }
                }
                if (!dupeFirstLetter && chosenWord.length() > 5 && chosenWord.length() < 15) {
                    // Only words that are 6-14 characters long can be selected
                    wordFound = true;
                }
            }

            wordPassages.add(new TypePassage(chosenWord)); // add the selected word on screen.
            selectedWords.add(chosenWord);
            xPosList.add(rand.nextInt(600) + 50);
            yPosList.add(80);
        }
    }

    // Called whenever a character is typed (keyhandler calls this). Updates all instance variables accordingly
    // REQUIRES: gameOver is false
    // MODIFIES: this
    public void charTyped(char c) {
        while (running) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        } // to prevent synchrony issues.
        running = true;

        if (wordSelected) {
            // If a word is currently being typed, the user is only able to continue typing that word.
            if (c == curPassage.getCharList().get(curPassage.getCursorPos()).getChar()) {
                curPassage.correctInput();
                correctChars++;
                if (correctChars % 20 == 0) {
                    addNewWord();
                }
            } else {
                curPassage.incorrectInput();
            }

            // If the current word is done being typed, remove it from the screen.
            if (curPassage.checkComplete()) {
                int indexToRemove = wordPassages.indexOf(curPassage);
                if (indexToRemove > -1) {
                    wordPassages.remove(indexToRemove);
                    selectedWords.remove(indexToRemove);
                    xPosList.remove(indexToRemove);
                    yPosList.remove(indexToRemove);

                    wordsLeft--;
                }
                wordSelected = false;
            }
        } else { // If no word is currently being typed, the user can start typing any of the words on screen
            for (int i=0; i<selectedWords.size(); i++) {
                if (c == selectedWords.get(i).charAt(0)) {
                    curPassage = wordPassages.get(i);
                    wordSelected = true;
                    curPassage.correctInput();
                    correctChars++;
                }
            }
        }
        charsTyped++;
        calcAccuracy();
        if (wordsLeft == 0) {
            gameWon = true;
        }
        running = false;
    }
}
