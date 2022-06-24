package com.injent.miscalls.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.databinding.SectionItemBinding;
import com.injent.miscalls.ui.registry.Section;

public class SectionAdapter extends ListAdapter<Section,SectionAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    public SectionAdapter(OnItemClickListener listener) {
        super(diffCallBack);
        this.listener = listener;
    }

    static DiffUtil.ItemCallback<Section> diffCallBack = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Section oldItem, @NonNull Section newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Section oldItem, @NonNull Section newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SectionItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.section_item, parent, false);
        return new SectionAdapter.ViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getItem(position));
    }

    public interface OnItemClickListener {
        void onClick(int type);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final SectionItemBinding binding;
        private final OnItemClickListener listener;

        public ViewHolder(SectionItemBinding binding, OnItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        public void setData(Section section) {
            binding.sectionName.setText(section.getSectionName());
            binding.sectionName.setOnClickListener(view -> listener.onClick(section.getSectionType()));
        }
    }
}
