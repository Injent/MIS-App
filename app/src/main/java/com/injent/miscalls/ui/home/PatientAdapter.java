package com.injent.miscalls.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.databinding.PatientListItemBinding;

public class PatientAdapter extends ListAdapter<Patient, PatientAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    protected PatientAdapter(OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    static DiffUtil.ItemCallback<Patient> diffCallback = new DiffUtil.ItemCallback<Patient>() {
        @Override
        public boolean areItemsTheSame(@NonNull Patient oldItem, @NonNull Patient newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Patient oldItem, @NonNull Patient newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        PatientListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.patient_list_item,parent, false);
        return new ViewHolder(listener, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Patient patient = getItem(position);
        holder.setData(patient, position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final PatientAdapter.OnItemClickListener listener;
        private final PatientListItemBinding binding;

        public ViewHolder(PatientAdapter.OnItemClickListener listener, PatientListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        public void setData(Patient patient, int position){
            binding.listId.setText(String.valueOf(position + 1));
            binding.listHeader.setText(patient.getShortInfo());
            binding.listAddress.setText(patient.getRegAddress());
            if (patient.isInspected()) {
                binding.statusBar.setBackgroundResource(R.drawable.status_bar_done);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(patient.getId());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(int patientId);
    }
}
