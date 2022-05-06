package com.injent.miscalls.ui.diagnosis;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.data.diagnosis.Diagnosis;
import com.injent.miscalls.databinding.DiagnosisItemBinding;

import java.security.SecureRandom;

public class DiagnosisUsedAdapter extends ListAdapter<Diagnosis, DiagnosisUsedAdapter.ViewHolder> {

    private final OnItemLongClickListener listener;

    protected DiagnosisUsedAdapter(OnItemLongClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    static DiffUtil.ItemCallback<Diagnosis> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Diagnosis oldItem, @NonNull Diagnosis newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Diagnosis oldItem, @NonNull Diagnosis newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        DiagnosisItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.diagnosis_item,parent,false);
        return new ViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Diagnosis diagnosis = getItem(position);
        holder.setData(diagnosis);
    }

    public interface OnItemLongClickListener {
        void onClick(int diagnosisId);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final DiagnosisItemBinding binding;
        private final OnItemLongClickListener listener;
        private final SecureRandom random;

        public ViewHolder(@NonNull DiagnosisItemBinding binding, OnItemLongClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
            random = new SecureRandom();
        }

        public void setData(Diagnosis diagnosis) {
            binding.usedDiagnosisText.setText(diagnosis.getContent());
            diagnosis.setId(random.nextInt());
            binding.usedDiagnosisText.setOnLongClickListener(view -> {
                listener.onClick(diagnosis.getId());
                return false;
            });
        }
    }
}
