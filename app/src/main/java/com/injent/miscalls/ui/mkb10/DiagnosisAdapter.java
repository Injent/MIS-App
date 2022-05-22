package com.injent.miscalls.ui.mkb10;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.injent.miscalls.databinding.ItemHandbookDiagnosisBinding;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisAdapter extends ListAdapter<Diagnosis, DiagnosisAdapter.ViewHolder> implements Filterable {

    private final OnItemClickListener listener;
    private List<Diagnosis> fullList;
    private boolean backAction;

    public DiagnosisAdapter(OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Diagnosis> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Diagnosis oldItem, @NonNull Diagnosis newItem) {
            return oldItem.equals(newItem);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Diagnosis oldItem, @NonNull Diagnosis newItem) {
            return oldItem.equals(newItem);
        }
    };

    public void submitList(@Nullable List<Diagnosis> list, boolean backAction) {
        this.backAction = backAction;
        this.fullList = new ArrayList<>(list);
        super.submitList(list);
        if (backAction) {
            listener.previousPage(getItem(0).getParentId());
        }
    }

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
        void onClickStar(int diagnosisId);
        void onClick(Diagnosis diagnosis);
        void nextPage(int id);
        void previousPage(int id);
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
            if (diagnosis.isParent()) {
                binding.handbookArrow.setVisibility(View.VISIBLE);
            } else {
                binding.handbookArrow.setVisibility(View.GONE);
            }
            binding.handbookItemLayout.setOnClickListener(view -> listener.onClick(diagnosis));
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @NonNull
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Diagnosis> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(fullList);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Diagnosis item : fullList) {
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
        protected void publishResults(CharSequence charSequence, @NonNull FilterResults filterResults) {
            submitList((List<Diagnosis>) filterResults.values);
        }
    };
}
