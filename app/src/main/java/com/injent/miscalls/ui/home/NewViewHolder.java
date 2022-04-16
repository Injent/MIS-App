package com.injent.miscalls.ui.home;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.data.patientlist.Patient;

public class NewViewHolder extends RecyclerView.ViewHolder {

    private final NewPatientAdapter.OnItemClickListener  listener;

    public NewViewHolder(@NonNull View itemView, NewPatientAdapter.OnItemClickListener listener) {
        super(itemView);
        this.listener = listener;
    }

    public void setData(Patient patient){

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(patient.id);
            }
        });
    }
}