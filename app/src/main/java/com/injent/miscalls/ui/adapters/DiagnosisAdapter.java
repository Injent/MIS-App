package com.injent.miscalls.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.diagnosis.Diagnosis;
import com.injent.miscalls.databinding.ItemHandbookDiagnosisBinding;

public class DiagnosisAdapter extends ListAdapter<Diagnosis, DiagnosisAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    public DiagnosisAdapter(OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Diagnosis> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Diagnosis oldItem, @NonNull Diagnosis newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Diagnosis oldItem, @NonNull Diagnosis newItem) {
            return false;
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemHandbookDiagnosisBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_handbook_diagnosis, parent, false);
        return new ViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getItem(position));
    }

    public interface OnItemClickListener {
        void onClick(Diagnosis diagnosis);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final OnItemClickListener listener;
        private final ItemHandbookDiagnosisBinding binding;

        public ViewHolder(@NonNull ItemHandbookDiagnosisBinding binding, OnItemClickListener listener) {
            super(binding.getRoot());
            this.listener = listener;
            this.binding = binding;
        }

        public void setData(@NonNull Diagnosis diagnosis) {
            binding.diagnosisCode.setText(diagnosis.getCode());
            binding.diagnosisCategory.setText(diagnosis.getName());
            if (diagnosis.isNotParent()) {
                binding.handbookArrow.setVisibility(View.GONE);
            } else {
                binding.handbookArrow.setVisibility(View.VISIBLE);
            }
            binding.handbookItemLayout.setOnClickListener(view -> listener.onClick(diagnosis));
        }
    }
}
