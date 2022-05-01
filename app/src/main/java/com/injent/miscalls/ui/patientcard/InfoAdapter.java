package com.injent.miscalls.ui.patientcard;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.databinding.PatientCardItemBinding;

public class InfoAdapter extends ListAdapter<String, InfoAdapter.ViewHolder> {

    private final String[] fieldName;

    protected InfoAdapter(String... fieldName) {
        super(diffCallback);
        this.fieldName = fieldName;
    }

    static DiffUtil.ItemCallback<String> diffCallback = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return false;
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        PatientCardItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.patient_card_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getItem(position), fieldName[position]);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final PatientCardItemBinding binding;

        public ViewHolder(@NonNull PatientCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(String infoField, String fieldName) {
            binding.fieldType.setText(fieldName);
            binding.fieldValue.setText(infoField);
        }
    }
}
