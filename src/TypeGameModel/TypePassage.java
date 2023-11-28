package TypeGameModel;

import java.util.*;
import java.util.List;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

/*
 * Represents a passage of word(s) that is to be typed.
 *
 * BOSS FIGHT: have a BossFightGame and a BossFightPanel. First have a dialogue screen (USE TIMER to go between lines)
 * Then have the boss at the top and the player at the bottom. Player uses mouse clicks to move around.
 * Boss will spew TypePassages at the player and the player is to type them before they hit the player. Reflected words
 * damage the boss.
 *
 * At half HP, introduce another factor that makes the player need to move around actively while typing. Maybe
 * include some trash talk too. Player should have limited HP but there should be some mechanic to naturally
 * regenerate.
 */
public class TypePassage {
    private List<TypeChar> passageChars;
    private int cursorPos; // indicates the next character in the passage that is to be typed
    private int xPos; // used in fallingwordsgame and bossFight, represents location of passage's top left corner
    private int yPos;

    private int xVel; // used in bossFight only. Represents the velocity vector of the passage.
    private int yVel;

    private int width; // used in bossFight only. Represents the pixel width and height of the passage
    private int height;

    // Constructors (one with text input, one with single word input, one without)
    public TypePassage() {
        passageChars = new ArrayList<>();
        cursorPos = 0;
    }

    // Creates a TypePassage for a single word input with initial x and y position (for falling words)
    public TypePassage(String word, int posX, int posY) {
        passageChars = new ArrayList<>();
        xPos = posX;
        yPos = posY;
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
        Font font = new Font("Helvetica", 20, 20);
        width = (int)(font.getStringBounds(word, frc).getWidth());
        height = (int)(font.getStringBounds(word, frc).getHeight());
        cursorPos = 0;
        char [] chars = word.toCharArray();
        for (char c : chars) {
            passageChars.add(new TypeChar(c));
        }
    }

    // Creates a TypePassage for a single word input with initial x and y position and velocty (for boss fight)
    public TypePassage(String word, int posX, int posY, int velX, int velY) {
        passageChars = new ArrayList<>();
        xPos = posX;
        yPos = posY;
        xVel = velX;
        yVel = velY;
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
        Font font = new Font("Helvetica", 20, 20);
        width = (int)(font.getStringBounds(word, frc).getWidth());
        height = (int)(font.getStringBounds(word, frc).getHeight());
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

    public void setXPos(int pos) {
        xPos = pos;
    }

    public int getXPos() {
        return xPos;
    }

    public void setYPos(int pos) {
        yPos = pos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setXVel(int vel) {
        xVel = vel;
    }

    public int getXVel() {
        return xVel;
    }

    public void setYVel(int vel) {
        yVel = vel;
    }

    public int getYVel() {
        return yVel;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getPassageWords() {
        String output = "";
        for (TypeChar t : passageChars) {
            output = output + t.getChar();
        }
        return output;
    }

    // Adds a word to the passage
    public void addWord(String str) {
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
