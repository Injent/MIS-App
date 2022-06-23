package com.injent.miscalls.ui.callinfo;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.databinding.ItemCallInfoBinding;
import com.injent.miscalls.ui.adapters.Field;

public class InfoAdapter extends ListAdapter<Field, InfoAdapter.ViewHolder> {

    public InfoAdapter() {
        super(diffCallback);
    }

    static DiffUtil.ItemCallback<Field> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Field oldItem, @NonNull Field newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Field oldItem, @NonNull Field newItem) {
            return false;
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCallInfoBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_call_info, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemCallInfoBinding binding;

        public ViewHolder(@NonNull ItemCallInfoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(Field field) {
            binding.fieldType.setText(field.getNameStringResId());
            binding.fieldValue.setText(field.getValue());
        }
    }
}
