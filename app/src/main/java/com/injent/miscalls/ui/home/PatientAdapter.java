package com.injent.miscalls.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.databinding.PatientListItemBinding;

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {

    private SortedList<Patient> sortedList;
    private OnPatientClickListener listener;
    private boolean moved;

    public PatientAdapter(List<Patient> list) {
        sortedList = new SortedList<>(Patient.class, new SortedList.Callback<Patient>() {
            @Override
            public int compare(Patient o1, Patient o2) {
                if (!o2.inspected && o1.inspected) {
                    return 1;
                }
                if (o2.inspected && !o1.inspected) {
                    return -1;
                }
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Patient oldItem, Patient newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Patient item1, Patient item2) {
                return item1.id == item2.id;
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });

        for (Patient p : list) {
            sortedList.add(p);
        }
    }

    public interface OnPatientClickListener {
        void onClick(View view, int position);
    }

    public void setListener(OnPatientClickListener listener) {
        this.listener = listener;
    }

    public Patient getPatient(int position) {
        return sortedList.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        PatientListItemBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.patient_list_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.listId.setText(String.valueOf(position + 1));
        holder.binding.listHeader.setText(sortedList.get(position).getShortInfo());
        holder.binding.addressField.setText(sortedList.get(position).regAddress);
        if (sortedList.get(position).inspected)
            holder.binding.statusBar.setBackgroundResource(R.drawable.status_bar_done);

        //First time solution
        holder.binding.patientCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientAdapter.this.onClick(view, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private PatientListItemBinding binding;

        public ViewHolder(@NonNull PatientListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    private void onClick(View view, PatientAdapter.ViewHolder holder) {
        if (moved) return;
        moved = true;
        Bundle bundle = new Bundle();
        bundle.putParcelable("patient",sortedList.get(holder.getAdapterPosition()));
        Navigation.findNavController(view)
                .navigate(R.id.action_homeFragment_to_patientCardFragment, bundle);
    }
}
