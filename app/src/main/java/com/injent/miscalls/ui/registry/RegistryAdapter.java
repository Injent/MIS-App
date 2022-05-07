package com.injent.miscalls.ui.registry;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.data.registry.Registry;
import com.injent.miscalls.databinding.ItemRegistryBinding;

public class RegistryAdapter extends ListAdapter<Registry, RegistryAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    protected RegistryAdapter(OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    static DiffUtil.ItemCallback<Registry> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Registry oldItem, @NonNull Registry newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Registry oldItem, @NonNull Registry newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRegistryBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_registry, parent, false);
        return new ViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getItem(position), position);
    }

    public interface OnItemClickListener {
        void onClick(int position);

        void onLongClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemRegistryBinding binding;
        private final OnItemClickListener listener;

        public ViewHolder(ItemRegistryBinding binding, OnItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        public void setData(Registry registry, int position) {
            binding.registryPatient.setText(String.valueOf("Мирослав Дудко Алексеевич"));
            binding.registryDiagnosis.setText(String.valueOf(registry.getDiagnosis()));

            binding.editRegistry.setOnClickListener(view -> listener.onClick(position));
            binding.protocolCard.setOnLongClickListener(view -> {
                listener.onLongClick(position);
                return false;
            });
        }
    }
}
