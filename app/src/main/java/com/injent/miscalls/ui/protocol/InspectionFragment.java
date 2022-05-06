package com.injent.miscalls.ui.protocol;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.calllist.CallInfo;
import com.injent.miscalls.data.savedprotocols.Inspection;
import com.injent.miscalls.databinding.FragmentInspectionBinding;
import com.injent.miscalls.domain.ProtocolFletcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InspectionFragment extends Fragment {

    private FragmentInspectionBinding binding;
    private CallInfo callInfo;

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

        if (getArguments() != null) {
            callInfo = getArguments().getParcelable(getString(R.string.keyCallParcelable));
            setInfo(callInfo);
        }

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        FieldAdapter fieldAdapter = new FieldAdapter();
        List<Field> fieldList = new ArrayList<>();
        String[] names = getResources().getStringArray(R.array.fieldType);
        String[] hints = getResources().getStringArray(R.array.fieldHint);
        for (int i = 0; i < names.length; i++) {
            fieldList.add(new Field(names[i], hints[i]));
        }
        binding.fieldRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.fieldRecyclerView.setAdapter(fieldAdapter);
        fieldAdapter.submitList(fieldList);
    }

    private Inspection protocolFieldsToProtocol() {
        return null;
    }

    private void generatePdf() {
        try {
            new ProtocolFletcher(requireContext(), callInfo,App.getUser()).fletchPdfFile(protocolFieldsToProtocol());
            Toast.makeText(requireContext(),getText(R.string.pdfGeneration),Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(),getText(R.string.pdfGenerationFailed),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setInfo(CallInfo callInfo) {
        this.callInfo = callInfo;
        binding.fullnameText.setText(callInfo.getFullName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}