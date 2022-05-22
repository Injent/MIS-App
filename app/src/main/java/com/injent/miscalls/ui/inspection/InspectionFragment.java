package com.injent.miscalls.ui.inspection;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.databinding.FragmentInspectionBinding;
import com.injent.miscalls.ui.callstuff.CallStuffViewModel;

public class InspectionFragment extends Fragment {

    private FragmentInspectionBinding binding;
    private CallStuffViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inspection,
                container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CallStuffViewModel.class);

        viewModel.getCallLiveData().observe(getViewLifecycleOwner(), this::setInfo);

        binding.presetCard.setOnClickListener(v -> setPresetInfo(viewModel.getCallLiveData().getValue()));

        binding.inspectionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setCurrentInspection(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Nothing to do
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setInfo(CallInfo callInfo) {
        binding.fullnameText.setText(callInfo.getFullName());
    }

    private void setPresetInfo(CallInfo callInfo) {
        if (callInfo == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("Значение 1: ").append(callInfo.getBornDate()).append("\n")
                .append("Значение 2: ").append(callInfo.getPolis());
        binding.inspectionField.setText(sb.toString());
        Toast.makeText(requireContext(),R.string.tempUsed,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}