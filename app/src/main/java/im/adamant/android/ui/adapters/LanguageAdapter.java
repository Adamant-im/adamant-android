package im.adamant.android.ui.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageAdapter extends BaseAdapter {
    private Context context;
    private List<Locale> locales = new ArrayList<>();
    private LayoutInflater inflater;

    public LanguageAdapter(Context context, @NonNull List<Locale> locales) {
        this.context = context;
        this.locales = locales;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return locales.size();
    }

    @Override
    public Locale getItem(int i) {
        return locales.get(i);
    }

    @Override
    public long getItemId(int i) {
        return locales.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_spinner_item, viewGroup, false);
        }
        ((TextView) view).setText(locales.get(i).getDisplayName());
        return view;
    }
}
