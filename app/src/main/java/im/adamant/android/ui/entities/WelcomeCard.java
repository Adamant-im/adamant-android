package im.adamant.android.ui.entities;

import android.text.Spannable;
import android.text.Spanned;

public class WelcomeCard {
    private int imageResource;
    private Spanned text;

    public WelcomeCard(int imageResource, Spanned text) {
        this.imageResource = imageResource;
        this.text = text;
    }

    public int getImageResource() {
        return imageResource;
    }

    public Spanned getText() {
        return text;
    }

}
