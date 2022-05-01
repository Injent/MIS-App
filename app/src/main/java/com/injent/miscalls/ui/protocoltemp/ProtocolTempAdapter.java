package com.injent.miscalls.ui.protocoltemp;

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
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.databinding.ProtocolTempItemBinding;

import java.util.ArrayList;
import java.util.List;

public class ProtocolTempAdapter extends ListAdapter<ProtocolTemp, ProtocolTempAdapter.ViewHolder> implements Filterable {

    private final OnItemClickListener listener;
    private List<ProtocolTemp> searchList;
    private final boolean editMode;

    protected ProtocolTempAdapter(OnItemClickListener listener, boolean editMode) {
        super(diffCallback);
        this.listener = listener;
        this.editMode = editMode;
    }

    public void submitList(@Nullable List<ProtocolTemp> list, boolean searchList) {
        super.submitList(list);
        if (searchList && list != null)
            this.searchList = new ArrayList<>(list);
    }

    static DiffUtil.ItemCallback<ProtocolTemp> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProtocolTemp oldItem, @NonNull ProtocolTemp newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProtocolTemp oldItem, @NonNull ProtocolTemp newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ProtocolTempItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.protocol_temp_item, parent, false);
        return new ViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getItem(position), editMode);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ProtocolTempItemBinding binding;
        private final OnItemClickListener listener;

        public ViewHolder(ProtocolTempItemBinding binding, OnItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        public void setData(ProtocolTemp protocolTemp, boolean editMode) {
            binding.listProtocolName.setText(protocolTemp.getName());
            binding.listProtocolDesc.setText(protocolTemp.getDescription());

            if (editMode) {
                binding.modeImage.setBackgroundResource(R.drawable.ic_edit);
            } else {
                binding.modeImage.setBackgroundResource(R.drawable.ic_navigate_right);
            }

            binding.protocolTempCard.setOnClickListener(view0 -> listener.onClick(protocolTemp.getId()));
        }
    }

    public interface OnItemClickListener {
        void onClick(int protocolTempId);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ProtocolTemp> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(searchList);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (ProtocolTemp item : searchList) {
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
            submitList((List<ProtocolTemp>) filterResults.values, false);
        }
    };
}
