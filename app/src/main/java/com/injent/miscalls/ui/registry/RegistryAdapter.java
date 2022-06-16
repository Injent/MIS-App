package com.injent.miscalls.ui.registry;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.databinding.ItemRegistryBinding;

import java.util.List;

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

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Registry oldItem, @NonNull Registry newItem) {
            return oldItem.hashCode() == newItem.hashCode();
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
        holder.setData(getItem(position));
    }

    public interface OnItemClickListener {
        void onClick(int id);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemRegistryBinding binding;
        private final OnItemClickListener listener;

        public ViewHolder(@NonNull ItemRegistryBinding binding, OnItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        public void setData(@NonNull Registry registry) {
            binding.registryPatient.setText(registry.getCallInfo().getFullName());
            binding.registryDiagnosis.setText(Diagnosis.listToStringCodes(registry.getDiagnoses(),','));

            binding.editRegistry.setOnClickListener(view -> listener.onClick(registry.getId()));

            if (registry.isDelete()) {
                binding.registryCard.setForeground(
                        ResourcesCompat.getDrawable(
                                binding.getRoot().getResources(),
                                R.drawable.mute,
                                binding.getRoot().getContext().getTheme()
                        )
                );
            } else {
                binding.registryCard.setForeground(
                        ResourcesCompat.getDrawable(
                                binding.getRoot().getResources(),
                                R.drawable.diagnosis_focused_effect,
                                binding.getRoot().getContext().getTheme()
                        )
                );
            }
        }
    }
}
