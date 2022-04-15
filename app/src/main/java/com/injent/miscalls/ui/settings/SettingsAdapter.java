package com.injent.miscalls.ui.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.injent.miscalls.R;

public class SettingsAdapter extends ArrayAdapter {

    private String[] modes;

    @Nullable
    @Override
    public String getItem(int position) {
        return modes[position];
    }

    public SettingsAdapter(@NonNull Context context, @NonNull String... modes) {
        super(context, 0, modes);
        this.modes = modes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mode_spinner_item,
                    parent, false);
        }

        TextView textView = convertView.findViewById(R.id.modeText);
        textView.setText(getItem(position));
        return convertView;
    }
}
