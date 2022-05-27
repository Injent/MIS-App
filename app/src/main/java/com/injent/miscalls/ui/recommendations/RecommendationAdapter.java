package com.injent.miscalls.ui.recommendations;

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
import com.injent.miscalls.data.recommendation.Recommendation;
import com.injent.miscalls.databinding.ItemRecommendationBinding;

import java.util.ArrayList;
import java.util.List;

public class RecommendationAdapter extends ListAdapter<Recommendation, RecommendationAdapter.ViewHolder> implements Filterable {

    private final OnItemClickListener listener;
    private List<Recommendation> searchList;

    protected RecommendationAdapter(OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    public void submitList(@Nullable List<Recommendation> list, boolean searchList) {
        super.submitList(list);
        if (searchList && list != null)
            this.searchList = new ArrayList<>(list);
    }

    static DiffUtil.ItemCallback<Recommendation> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Recommendation oldItem, @NonNull Recommendation newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Recommendation oldItem, @NonNull Recommendation newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRecommendationBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_recommendation, parent, false);
        return new ViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecommendationBinding binding;
        private final OnItemClickListener listener;

        public ViewHolder(@NonNull ItemRecommendationBinding binding, OnItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        public void setData(@NonNull Recommendation recommendation) {
            binding.listProtocolName.setText(recommendation.getName());
            binding.listProtocolDesc.setText(recommendation.getDescription());

            binding.protocolTempCard.setOnClickListener(v -> listener.onClick(recommendation.getId()));
        }
    }

    public interface OnItemClickListener {
        void onClick(int recommendationId);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @NonNull
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Recommendation> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(searchList);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Recommendation item : searchList) {
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
            submitList((List<Recommendation>) filterResults.values, false);
        }
    };
}
