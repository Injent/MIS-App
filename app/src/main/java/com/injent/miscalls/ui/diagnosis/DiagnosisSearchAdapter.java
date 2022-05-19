package com.injent.miscalls.ui.diagnosis;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.databinding.DiagnosisItemSelectBinding;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisSearchAdapter extends ListAdapter<Diagnosis, DiagnosisSearchAdapter.ViewHolder> implements Filterable {

    private final OnItemClickListener listener;
    private List<Diagnosis> searchList;

    protected DiagnosisSearchAdapter(OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    public void submitList(@Nullable List<Diagnosis> list, boolean searchList) {
        super.submitList(list);
        if (searchList && list != null)
            this.searchList = new ArrayList<>(list);
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
        DiagnosisItemSelectBinding binding = DataBindingUtil.inflate(inflater, R.layout.diagnosis_item_select, parent, false);
        return new ViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getItem(position));
    }

    @Override
    public void submitList(@Nullable List<Diagnosis> list) {
        if (list == null) return;
        List<Diagnosis> sortedList = new ArrayList<>();
        for (Diagnosis d : list) {
            if (!d.getCode().isEmpty()) {
                sortedList.add(d);
            }
        }
        searchList = sortedList;
        super.submitList(sortedList);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final DiagnosisItemSelectBinding binding;
        private final OnItemClickListener listener;

        public ViewHolder(DiagnosisItemSelectBinding binding, OnItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        @SuppressLint("SetTextI18n")
        public void setData(Diagnosis diagnosis) {
            if (diagnosis.getCode().isEmpty())
                binding.diagnosisName.setText(diagnosis.getName());
            else
                binding.diagnosisName.setText(diagnosis.getCode() + " " + diagnosis.getName());
            binding.diagnosisName.setOnClickListener(view -> listener.onClick(diagnosis));
        }
    }

    public interface OnItemClickListener {
        void onClick(Diagnosis diagnosis);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Diagnosis> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(searchList);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Diagnosis item : searchList) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            //TODO
            submitList((List<Diagnosis>) filterResults.values, false);
        }
    };
}
