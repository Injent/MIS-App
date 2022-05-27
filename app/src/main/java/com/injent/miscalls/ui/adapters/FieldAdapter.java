package com.injent.miscalls.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;

import java.util.List;

public class FieldAdapter extends ListAdapter<ViewType, FieldAdapter.ViewHolder> {

    public FieldAdapter() {
        super(diffCallback);
    }

    private static final DiffUtil.ItemCallback<ViewType> diffCallback = new DiffUtil.ItemCallback<>() {

        @Override
        public boolean areItemsTheSame(@NonNull ViewType oldItem, @NonNull ViewType newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ViewType oldItem, @NonNull ViewType newItem) {
            return oldItem.getViewType() == newItem.getViewType();
        }
    };

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public void submitList(@Nullable List<ViewType> list) {
        super.submitList(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewType layout = getItem(position);
        switch (layout.getViewType()) {
            case ViewType.FIELD_EDIT: holder.setDefaultFieldData(layout);
            break;
            case ViewType.FIELD_ADDITIONAL: holder.setAdditionalFieldData(layout);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View view) {
            super(view);
        }

        public void setDefaultFieldData(ViewType layout) {
            TextView textView = itemView.findViewById(R.id.fieldEditSubtitle);
            EditText editText = itemView.findViewById(R.id.fieldEditText);

            Field field = (Field) layout;
            textView.setText(field.getNameStringResId());
            editText.setHint(field.getHintStringResId());
        }

        public void setAdditionalFieldData(ViewType layout) {
            TextView textView = itemView.findViewById(R.id.fieldAddName);

            AdditionalField additionalField = (AdditionalField) layout;

            textView.setText(additionalField.getName());
        }
    }
}
