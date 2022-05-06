package com.injent.miscalls.ui.savedprotocols;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.data.savedprotocols.Inspection;
import com.injent.miscalls.databinding.SavedProtocolsItemBinding;

public class SavedProtocolAdapter extends ListAdapter<Inspection, SavedProtocolAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    protected SavedProtocolAdapter(OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    static DiffUtil.ItemCallback<Inspection> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Inspection oldItem, @NonNull Inspection newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Inspection oldItem, @NonNull Inspection newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SavedProtocolsItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.saved_protocols_item, parent, false);
        return new ViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getItem(position));
    }

    public interface OnItemClickListener {
        void onClick(int protocolId);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final SavedProtocolsItemBinding binding;
        private final OnItemClickListener listener;

        public ViewHolder(SavedProtocolsItemBinding binding, OnItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        public void setData(Inspection inspection) {
            binding.savedProtocolName.setText(inspection.getName());
            binding.savedProtocolType.setText(inspection.getDescription());

            binding.editProtocol.setOnClickListener(view -> listener.onClick(inspection.getId()));
        }
    }
}
