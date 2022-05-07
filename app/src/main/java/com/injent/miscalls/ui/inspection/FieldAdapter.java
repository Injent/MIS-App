package com.injent.miscalls.ui.inspection;

import android.text.Editable;
import android.text.TextWatcher;
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

    private final OnEditTextListener listener;

    public FieldAdapter(OnEditTextListener listener) {
        super(diffCallBack);
        this.listener = listener;
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
        return new ViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getItem(position), position);
    }

    public interface OnEditTextListener {
        void onChange(String s, int type);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final FieldItemBinding binding;
        private final OnEditTextListener listener;

        public ViewHolder(@NonNull FieldItemBinding binding, OnEditTextListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        public void setData(Field field, int type) {
            binding.fieldName.setText(field.getFieldName());
            binding.fieldText.setHint(field.getFieldHintText());
            binding.fieldText.setText(field.getFieldText());
            if (type == 0) {
                binding.fieldText.setText(R.string.inspectionPreset);
            }
            binding.fieldText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Nothing to do
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listener.onChange(s.toString(), type);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Nothing to do
                }
            });
        }
    }
}
