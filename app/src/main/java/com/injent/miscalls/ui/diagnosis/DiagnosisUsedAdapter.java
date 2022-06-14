package com.injent.miscalls.ui.diagnosis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.databinding.DiagnosisItemBinding;

public class DiagnosisUsedAdapter extends ListAdapter<Diagnosis, DiagnosisUsedAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    protected DiagnosisUsedAdapter(OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    static DiffUtil.ItemCallback<Diagnosis> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Diagnosis oldItem, @NonNull Diagnosis newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Diagnosis oldItem, @NonNull Diagnosis newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        DiagnosisItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.diagnosis_item,parent,false);
        return new ViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == getCurrentList().size() || getCurrentList().isEmpty()) {
            holder.setAddActionItem();
        } else {
            holder.setData(getItem(position));
        }
    }

    public interface OnItemClickListener {
        void onDelete(Diagnosis diagnosis);
        void onAddClick();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final DiagnosisItemBinding binding;
        private final OnItemClickListener listener;

        public ViewHolder(@NonNull DiagnosisItemBinding binding, OnItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        public void setData(Diagnosis diagnosis) {
            binding.usedDiagnosisText.setText(diagnosis.getName());
            binding.deleteDiagnosis.setOnClickListener(view -> {
                listener.onDelete(diagnosis);
            });
        }

        public void setAddActionItem() {
            binding.usedDiagnosisText.setOnClickListener(view -> listener.onAddClick());
            binding.usedDiagnosisText.setText(R.string.addDiagnosis);
            Context context = binding.getRoot().getContext();
            ColorStateList cl = null;
            try {
                @SuppressLint("ResourceType")
                XmlResourceParser xpp = context.getResources().getXml(R.color.button_text_color);
                cl = ColorStateList.createFromXml(context.getResources(), xpp,context.getTheme());
            } catch (Exception ignored) {
            }
            binding.usedDiagnosisText.setTextColor(cl);
            binding.usedDiagnosisText.setCompoundDrawables(ResourcesCompat.getDrawable(
                    binding.getRoot().getResources(),
                    R.drawable.ic_diagnosis,binding.getRoot().getContext().getTheme()),
                    null,
                    null,
                    null
            );
            binding.usedDiagnosisText.setBackgroundResource(R.drawable.add_diagnosis_bg);
            binding.deleteDiagnosis.setEnabled(false);
            binding.deleteDiagnosis.setVisibility(View.GONE);
        }
    }
}
