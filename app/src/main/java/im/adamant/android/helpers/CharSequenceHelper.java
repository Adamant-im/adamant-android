package im.adamant.android.helpers;

import java.util.stream.IntStream;

public class CharSequenceHelper {

    //TODO: Need create protect and unprotect functions

    public static CharSequence trim(CharSequence charSequence) {
        int lastCharacterPosition = charSequence.length() - 1;
        int leftPosition = 0;
        int rightPosition = lastCharacterPosition;

        for (int i = 0; i < charSequence.length(); i++) {
            char currentChar = charSequence.charAt(i);

            if (isLetterCharacter(currentChar)) {
                leftPosition = i;
                break;
            }
        }

        for (int i = lastCharacterPosition; i > 0; i--) {
            char currentChar = charSequence.charAt(i);

            if (isLetterCharacter(currentChar)) {
                rightPosition = i;
                break;
            }
        }

        return charSequence.subSequence(leftPosition, rightPosition + 1);
    }

    //This method implements the sequence integration functionality for the API starting with version 19.
    public static CharSequence concat(CharSequence sequence1, CharSequence sequence2) {
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < sequence1.length(); i++) {
            buffer.append(sequence1.charAt(i));
        }

        for(int i = 0; i < sequence2.length(); i++) {
            buffer.append(sequence2.charAt(i));
        }

        return buffer;
    }

    public static boolean equalsCaseSensitive(CharSequence sequence1, CharSequence sequence2) {
        boolean equals = true;
        if (sequence1.length() != sequence2.length()) { return false; }

        for (int i = 0; i < sequence1.length(); i++) {
            equals &= (sequence1.charAt(i) == sequence2.charAt(i));
        }

        return equals;
    }

    private static boolean isLetterCharacter(char character) {
        boolean isSpace = (character == ' ');
        boolean isTab = (character == '\t');
        boolean isNewLine = (character == '\n');
        boolean isReturn = (character == '\r');

        return !(isSpace || isTab || isNewLine || isReturn);
    }
}
