package com.injent.miscalls.ui.patientcard;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.databinding.PatientCardItemBinding;
import com.injent.miscalls.data.patientlist.Patient;

import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {

    private List<String> fieldTypes, patientData;

    public InfoAdapter(Patient patient, List<String> fieldTypes) {
        this.patientData = patient.getData();
        this.fieldTypes = fieldTypes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        PatientCardItemBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.patient_card_item, parent, false);
        return new InfoAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.fieldType.setText(fieldTypes.get(position));
        holder.binding.fieldValue.setText(patientData.get(position));
    }

    @Override
    public int getItemCount() {
        return fieldTypes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private PatientCardItemBinding binding;

        public ViewHolder(@NonNull PatientCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
