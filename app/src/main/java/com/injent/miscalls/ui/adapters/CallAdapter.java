package com.injent.miscalls.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.medcall.MedCall;
import com.injent.miscalls.databinding.CallListItemBinding;

import java.util.Collections;
import java.util.List;

public class CallAdapter extends ListAdapter<MedCall, CallAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    public CallAdapter(OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    static DiffUtil.ItemCallback<MedCall> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull MedCall oldItem, @NonNull MedCall newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MedCall oldItem, @NonNull MedCall newItem) {
            return false;
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CallListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.call_list_item,parent, false);
        return new ViewHolder(listener, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedCall medCall = getItem(position);
        holder.setData(medCall, position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final CallAdapter.OnItemClickListener listener;
        private final CallListItemBinding binding;

        public ViewHolder(CallAdapter.OnItemClickListener listener, CallListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        @SuppressLint("SetTextI18n")
        public void setData(MedCall medCall, int position){
            if (position == 0) binding.callCardView.setBackgroundResource(R.drawable.call_list_top_ripple);
            binding.listId.setText(String.valueOf(position + 1));
            binding.listHeader.setText(medCall.getFullName());
            binding.listAddress.setText(medCall.getResidence());
            binding.listAge.setText(medCall.getAge() + " " + binding.getRoot().getContext().getString(R.string.age));
            if (medCall.isInspected()) {
                binding.statusBar.setBackgroundResource(R.drawable.status_bar_done);
            }
            itemView.setOnClickListener(v -> listener.onClick(medCall.getId()));
        }
    }

    @Override
    public void submitList(@Nullable List<MedCall> list) {
        if (list != null)
            super.submitList(list);
        else
            submitList(Collections.emptyList());
    }

    public interface OnItemClickListener {
        void onClick(int callId);
    }
}
