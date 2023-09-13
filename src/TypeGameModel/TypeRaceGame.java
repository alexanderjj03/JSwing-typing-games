package TypeGameModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Clock;
import java.util.*;
import java.util.List;

/*
 * Represents an instance of a typeracer-style typing race game.
 */
public class TypeRaceGame {
    private static final Random rand = new Random();

    private boolean running; // to ensure that charTyped executes to completion before it is called again.
    // This is needed as a precaution since charTyped is liked to a keyhandler.

    private TypePassage passage; // words to be typed
    private int correctChars;
    private int charsTyped;
    private int accuracy;
    private boolean gameOver;

    private Clock clock;
    private double initialTime; // time when first character is typed
    private List<Double> wpmList; // list of "current" wpm's, calculated per 50 correct characters
    private List<Double> timeList; // times at which each measurement in wpmList was taken
    private double cumulativeWpm; // cumulative wpm

    // Constructs the typing game. Initializes all instance variables and creates a passage of randomly selected words.
    // REQUIRES: numWords cannot be below 20.
    public TypeRaceGame(int numWords) {
        List<String> chosenWords = new ArrayList<>();
        List<String> allWords = new ArrayList<>();
        clock = Clock.systemDefaultZone();
        correctChars = 0;
        charsTyped = 0;
        accuracy = 100;
        initialTime = 0; // placeholder until first character is typed
        wpmList = new ArrayList<>();
        timeList = new ArrayList<>();
        cumulativeWpm = 0;
        gameOver = false;
        running = false;

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

        for (int i=0; i<numWords; i++) {
            chosenWords.add(allWords.get(rand.nextInt(allWords.size())));
        } // randomly selecting numWords words from allWords to be added to the passage
        passage = new TypePassage(chosenWords);
    }

    public TypePassage getPassage() {
        return passage;
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

    public boolean isGameOver() {
        return gameOver;
    }

    public double getInitialTime() {
        return initialTime;
    }

    public List<Double> getWpmList() {
        return wpmList;
    }

    public List<Double> getTimeList() {
        return timeList;
    }

    public double getCumulativeWpm() {
        return cumulativeWpm;
    }

    // Calculates the current typing game accuracy (correct chars/chars typed). Rounds to the nearest whole number.
    public void calcAccuracy() {
        if (charsTyped == 0) {
            accuracy = 100;
        } else {
            accuracy = (int) Math.round((100.0 * correctChars) / charsTyped);
        }
    }

    // Called whenever a character is typed (a keyhandler calls this). Updates all instance variables accordingly
    // REQUIRES: gameOver is false
    // MODIFIES: this
    public void charTyped(char c) {
        while (running) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        } // to prevent synchrony issues.
        running = true;
        double curTime = clock.millis(); // timestamp for when the character was typed.
        if (charsTyped == 0) {
            initialTime = clock.millis();
        }
        if (c == passage.getCharList().get(correctChars).getChar()) {
            // determining if the typed character matches the one that needs to be typed.
            passage.correctInput();
            correctChars++;
        } else {
            passage.incorrectInput();
        }
        charsTyped++;
        calcAccuracy();

        if ((correctChars % 10 == 0) && (correctChars != 0)) {
            cumulativeWpm = Math.round(10*(correctChars * 13500.0)/(curTime - initialTime))/10.0;
            // calculating wpm up to this point. This calculation is made every 10 correct characters to minimize lag.
            if (correctChars % 50 == 0) {
                if (correctChars == 50) {
                    wpmList.add(cumulativeWpm);
                } else {
                    wpmList.add(Math.round(10*(50.0 * 13500.0)/(curTime - timeList.get(timeList.size() - 1)))/10.0);
                    // calculating "current" wpm (over last 50 characters)
                }
                timeList.add(curTime);
            }
        }

        if (passage.checkComplete()) {
            cumulativeWpm = Math.round(10*(correctChars * 13500.0)/(curTime - initialTime))/10.0;
            gameOver = true;
        }
        running = false;
    }
}
