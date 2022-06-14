package com.injent.miscalls.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.ui.adapters.ViewType;

import java.util.Arrays;
import java.util.List;

public class SettingAdapter extends ListAdapter<ViewType, SettingAdapter.ViewHolder> {

    public SettingAdapter() {
        super(diffCallback);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    static DiffUtil.ItemCallback<ViewType> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ViewType oldItem, @NonNull ViewType newItem) {
            return oldItem.equals(newItem);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull ViewType oldItem, @NonNull ViewType newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewType layout = getItem(position);
        switch (holder.getItemViewType()) {
            case ViewType.SETTING_SPINNER: holder.setSpinnerData(layout);
            break;
            case ViewType.SETTING_SWITCH: holder.setSwitchData(layout);
            break;
            case ViewType.SETTINGS_BUTTON: holder.setButtonData(layout);
            break;
            default: holder.setSectionData(layout);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.context = itemView.getContext();
        }

        public void setButtonData(ViewType layout) {
            ButtonLayout buttonLayout = (ButtonLayout) layout;
            TextView name = itemView.findViewById(R.id.settingButtonText);
            name.setText(buttonLayout.getNameResId());
            Button button = itemView.findViewById(R.id.settingButton);
            button.setOnClickListener(buttonLayout.getListener());
            button.setText(buttonLayout.getButtonTextResId());

            Drawable icon = ResourcesCompat.getDrawable(context.getResources(), buttonLayout.getDrawableResId(),context.getTheme());
            if (icon == null) return;
            icon.mutate();
            DrawableCompat.setTint(icon, ContextCompat.getColor(context, buttonLayout.getDrawableColorResId()));
            name.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            name.setCompoundDrawablePadding(context.getResources().getDimensionPixelSize(R.dimen.icPadding));
        }

        public void setSpinnerData(ViewType layout) {
            SpinnerLayout spinnerLayout = (SpinnerLayout) layout;
            TextView title = view.findViewById(R.id.settingSpinnerTitle);
            title.setText(spinnerLayout.getStringResId());
            Drawable icon = ResourcesCompat.getDrawable(context.getResources(), spinnerLayout.getDrawableResId(),context.getTheme());
            if (icon == null) return;
            icon.mutate();
            DrawableCompat.setTint(icon, ContextCompat.getColor(context,spinnerLayout.getDrawableColorResId()));
            title.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            title.setCompoundDrawablePadding(context.getResources().getDimensionPixelSize(R.dimen.icPadding));

            Spinner spinner  = view.findViewById(R.id.spinner);
            String[] modes = context.getResources().getStringArray(spinnerLayout.getStringArrayResId());
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(context, modes);
            spinner.setAdapter(spinnerAdapter);
            spinner.setSelection(spinnerLayout.getSelectedItemPosition());
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    spinnerLayout.getListener().onSelect(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Nothing to do
                }
            });
        }

        public void setSwitchData(ViewType layout) {
            SwitchLayout switchLayout = (SwitchLayout) layout;
            TextView title = view.findViewById(R.id.settingsSwitchTitle);
            title.setText(switchLayout.getStringResId());
            Drawable icon = ContextCompat.getDrawable(context, switchLayout.getDrawableResId());
            if (icon == null) return;
            icon.mutate();
            DrawableCompat.setTint(icon, ContextCompat.getColor(context,switchLayout.getDrawableColorResId()));
            title.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            title.setCompoundDrawablePadding(context.getResources().getDimensionPixelSize(R.dimen.icPadding));
            if (switchLayout.getStringHintResId() != 0) {
                TextView hint = view.findViewById(R.id.settingsSwitchHint);
                hint.setVisibility(View.VISIBLE);
                hint.setText(switchLayout.getStringHintResId());
            }

            SwitchCompat switchCompat = view.findViewById(R.id.settingsSwitch);
            switchCompat.setChecked(switchLayout.getState());
            switchCompat.setTrackResource(switchLayout.getTrackResId());
            switchCompat.setThumbResource(switchLayout.getThumbResId());
            switchCompat.setOnCheckedChangeListener((compoundButton, state) -> switchLayout.getListener().onFlick(state));
            ConstraintLayout switchItemLayout = view.findViewById(R.id.switchItemLayout);
            switchItemLayout.setOnClickListener(view -> {
                switchLayout.getListener().onFlick(!switchCompat.isChecked());
                switchCompat.setChecked(!switchCompat.isChecked());
            });
        }

        public void setSectionData(ViewType layout) {
            SectionLayout sectionLayout = (SectionLayout) layout;
            TextView sectionName = view.findViewById(R.id.settingSectionName);
            sectionName.setText(sectionLayout.getStringResId());
        }
    }
}
