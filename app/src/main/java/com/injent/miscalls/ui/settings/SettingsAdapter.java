package com.injent.miscalls.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.injent.miscalls.R;

import java.util.List;

public class SettingsAdapter extends BaseAdapter {

    private Context context;
    private List<String> modes;

    public SettingsAdapter(Context context, List<String> modes) {
        this.context = context;
        this.modes = modes;
    }

    @Override
    public int getCount() {
        return modes.size();
    }

    @Override
    public String getItem(int i) {
        return modes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") View rootView = LayoutInflater.from(context).inflate(R.layout.mode_spinner_item,
                viewGroup, false);

        TextView textView = rootView.findViewById(R.id.modeText);
        textView.setText(modes.get(i));
        return rootView;
    }
}
