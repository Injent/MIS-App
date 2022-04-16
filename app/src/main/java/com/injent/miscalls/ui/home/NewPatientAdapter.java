package com.injent.miscalls.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.Patient;

public class NewPatientAdapter extends ListAdapter<Patient, NewViewHolder> {

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
    private final OnItemClickListener listener;

    protected NewPatientAdapter(OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_item, parent, false);
        return new NewViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NewViewHolder holder, int position) {
        Patient patient = getItem(position);
        holder.setData(patient);
    }

    public interface OnItemClickListener {
        void onClick(int pacientId);
    }
}
