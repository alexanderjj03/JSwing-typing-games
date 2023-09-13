package TypeGameModel;

import java.awt.*;

/*
 * Represents a single character within a TypePassage
 */
public class TypeChar {
    public static final Color COLOR0 = new Color(255, 255, 255);
    public static final Color COLOR1 = new Color(32, 255, 255);
    public static final Color COLOR2 = new Color(245, 32, 32);

    private Color status; // COLOR0 for untyped, COLOR1 for correctly typed, COLOR2 for incorrectly typed
    private char character;

    // Constructs a character
    public TypeChar(char chara) {
        character = chara;
        status = COLOR0;
    }

    public char getChar() {
        return character;
    }

    public void setChar(char c) {
        character = c;
    }

    public Color getStatus() {
        return status;
    }

    // If the character is typed correctly, change the color to blue
    public void typeCorrect() {
        status = COLOR1;
    }

    // If the character is typed incorrectly, change the color to red
    public void typeIncorrect() {
        status = COLOR2;
    }
}
