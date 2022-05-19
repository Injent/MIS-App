package com.injent.miscalls.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.injent.miscalls.App;
import com.injent.miscalls.R;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {

    private final Context context;
    private final List<String> modes;

    public SpinnerAdapter(Context context, List<String> modes) {
        this.context = context;
        this.modes = modes;
    }

    @Override
    public int getCount() {
        return modes.size();
    }

    @Override
    public String getItem(int position) {
        return modes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") View rootView = LayoutInflater.from(context).inflate(R.layout.mode_spinner_item,
                viewGroup, false);

        TextView textView = rootView.findViewById(R.id.modeText);
        textView.setText(modes.get(position));

        return rootView;
    }
}
