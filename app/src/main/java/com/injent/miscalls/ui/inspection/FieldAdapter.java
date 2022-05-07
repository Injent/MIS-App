package com.injent.miscalls.ui.inspection;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FieldItemBinding;

public class FieldAdapter extends ListAdapter<Field, FieldAdapter.ViewHolder> {

    protected FieldAdapter() {
        super(diffCallBack);
    }

    static DiffUtil.ItemCallback<Field> diffCallBack = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Field oldItem, @NonNull Field newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Field oldItem, @NonNull Field newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FieldItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.field_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final FieldItemBinding binding;

        public ViewHolder(@NonNull FieldItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(Field field) {
            binding.fieldName.setText(field.getFieldName());
            binding.fieldText.setHint(field.getFieldHintText());
            binding.fieldText.setText(field.getFieldText());
        }
    }
}
