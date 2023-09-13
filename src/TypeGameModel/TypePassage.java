package TypeGameModel;

import java.util.*;
import java.util.List;

/*
 * Represents a passage of word(s) that is to be typed.
 */
public class TypePassage {
    private List<TypeChar> passageChars;
    private int cursorPos;

    // Constructors (one with text input, one with single word input, one without)
    public TypePassage() {
        passageChars = new ArrayList<>();
        cursorPos = 0;
    }

    // Creates a TypePassage for a single word input (for falling words mode)
    public TypePassage(String word) {
        passageChars = new ArrayList<>();
        cursorPos = 0;
        char [] chars = word.toCharArray();
        for (char c : chars) {
            passageChars.add(new TypeChar(c));
        }
    }

    // Deconstruct every word into its characters, make a TypeChar object for each character,
    // and add it to passageChars
    public TypePassage(List<String> words) {
        passageChars = new ArrayList<>();
        cursorPos = 0;
        for (String s : words) {
            char [] chars = s.toCharArray();
            for (char c : chars) {
                passageChars.add(new TypeChar(c));
            }
            passageChars.add(new TypeChar(' '));
        }
        passageChars.remove(passageChars.size() - 1); // removing the last space
    }

    public int getCursorPos() {
        return cursorPos;
    }

    public List<TypeChar> getCharList() {
        return passageChars;
    }

    public String getPassageWords() {
        String output = "";
        for (TypeChar t : passageChars) {
            output = output + t.getChar();
        }
        return output;
    }

    // Adds a word to the passage
    public void addWord (String str) {
        passageChars.add(new TypeChar(' '));
        char [] chars = str.toCharArray();
        for (char c : chars) {
            passageChars.add(new TypeChar(c));
        }
    }

    // Checks to see if every character has been typed correctly. Returns true if so, false if not
    public boolean checkComplete() {
        return cursorPos == passageChars.size();
    }

    // called if the correct character is typed
    public void correctInput() {
        passageChars.get(cursorPos).typeCorrect();
        cursorPos++;
    }

    // called if the incorrect character is typed
    public void incorrectInput() {
        passageChars.get(cursorPos).typeIncorrect();
    }
}
