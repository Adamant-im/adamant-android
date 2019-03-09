package im.adamant.android.helpers;

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

    private static boolean isLetterCharacter(char character) {
        boolean isSpace = (character == ' ');
        boolean isTab = (character == '\t');
        boolean isNewLine = (character == '\n');
        boolean isReturn = (character == '\r');

        return !(isSpace || isTab || isNewLine || isReturn);
    }
}
