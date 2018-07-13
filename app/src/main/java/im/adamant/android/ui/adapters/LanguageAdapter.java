package im.adamant.android.ui.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import java.util.List;
import java.util.Locale;

public class LanguageAdapter extends ArrayAdapter<Locale> {

    public LanguageAdapter(@NonNull Context context, int resource, @NonNull List<Locale> objects) {
        super(context, resource, objects);
    }


}
