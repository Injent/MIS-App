package com.injent.miscalls.ui.protocoltemp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.databinding.ProtocolTempItemBinding;

public class ProtocolAdapter extends ListAdapter<ProtocolTemp, ProtocolAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    protected ProtocolAdapter(OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    static DiffUtil.ItemCallback<ProtocolTemp> diffCallback = new DiffUtil.ItemCallback<ProtocolTemp>() {
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
        holder.setData(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ProtocolTempItemBinding binding;
        private final OnItemClickListener listener;


        public ViewHolder(ProtocolTempItemBinding binding, OnItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        public void setData(ProtocolTemp protocolTemp) {
            binding.listProtocolName.setText(protocolTemp.getName());
            binding.listProtocolDesc.setText(protocolTemp.getDescription());

            binding.protocolTempCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(protocolTemp.getId());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(int protocolId);
    }
}
