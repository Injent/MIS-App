package com.injent.miscalls.ui.home;

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
import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.databinding.CallListItemBinding;

import java.util.Collections;
import java.util.List;

public class CallAdapter extends ListAdapter<CallInfo, CallAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    protected CallAdapter(OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    static DiffUtil.ItemCallback<CallInfo> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull CallInfo oldItem, @NonNull CallInfo newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull CallInfo oldItem, @NonNull CallInfo newItem) {
            return oldItem.sameContent(newItem);
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
        CallInfo callInfo = getItem(position);
        holder.setData(callInfo, position);
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
        public void setData(CallInfo callInfo, int position){
            if (position == 0) binding.callCardView.setBackgroundResource(R.drawable.call_list_top_ripple);
            binding.listId.setText(String.valueOf(position + 1));
            binding.listHeader.setText(callInfo.getFullName());
            binding.listAddress.setText(callInfo.getResidence());
            binding.listAge.setText(callInfo.getAge() + " " + binding.getRoot().getContext().getString(R.string.age));
            if (callInfo.isInspected()) {
                binding.statusBar.setBackgroundResource(R.drawable.status_bar_done);
            }
            itemView.setOnClickListener(v -> listener.onClick(callInfo.getId()));
        }
    }

    @Override
    public void submitList(@Nullable List<CallInfo> list) {
        if (list != null)
            super.submitList(list);
        else
            submitList(Collections.emptyList());
    }

    public interface OnItemClickListener {
        void onClick(int callId);
    }
}
