package com.injent.miscalls.ui.diagnosis;

import android.content.res.AssetManager;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.diagnosis.Diagnosis;
import com.injent.miscalls.databinding.FragmentDiagnosisBinding;
import com.injent.miscalls.ui.callstuff.CallStuffViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DiagnosisFragment extends Fragment {

    private FragmentDiagnosisBinding binding;
    private DiagnosisUsedAdapter diagnosisUsedAdapter;
    private DiagnosisSearchAdapter diagnosisSearchAdapter;
    private CallStuffViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_diagnosis,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CallStuffViewModel.class);

        binding.searchDiagnosisText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                diagnosisSearchAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });

        binding.cancelSearch.setOnClickListener(view12 -> {
            binding.diagnosisRecyclerView.setVisibility(View.VISIBLE);
            binding.addDiagnosis.setVisibility(View.VISIBLE);
            binding.searchDiagnosisText.setText("");
            binding.searchDiagnosisLayout.setVisibility(View.GONE);
        });

        binding.diagnosisSelectRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        diagnosisSearchAdapter = new DiagnosisSearchAdapter(this::addDiagnosis);
        binding.diagnosisSelectRecyclerView.setAdapter(diagnosisSearchAdapter);

        //Observer
        viewModel.getDiagnosisLiveData().observe(getViewLifecycleOwner(), diagnosisList -> binding.addDiagnosis.setOnClickListener(view1 -> showDiagnosisSearch(diagnosisList)));

        binding.diagnosisRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        diagnosisUsedAdapter = new DiagnosisUsedAdapter(diagnosisId -> {
            List<Diagnosis> diagnosisList = new ArrayList<>(diagnosisUsedAdapter.getCurrentList());
            int idToDelete = 0;
            for (int i = 0; i < diagnosisList.size(); i++) {
                if (diagnosisList.get(i).getId() == diagnosisId) {
                    idToDelete = i;
                    break;
                }
            }
            diagnosisList.remove(idToDelete);
            diagnosisUsedAdapter.submitList(diagnosisList);
            Toast.makeText(requireContext(),R.string.diagnoseDeleted,Toast.LENGTH_SHORT).show();
        });
        binding.diagnosisRecyclerView.setAdapter(diagnosisUsedAdapter);
    }

    private void showDiagnosisSearch(List<Diagnosis> list) {
        binding.searchDiagnosisLayout.setVisibility(View.VISIBLE);
        binding.addDiagnosis.setVisibility(View.GONE);
        binding.diagnosisRecyclerView.setVisibility(View.GONE);
        diagnosisSearchAdapter.submitList(list, true);
    }

    private void addDiagnosis(Diagnosis diagnosis) {
        List<Diagnosis> currentList = new ArrayList<>(diagnosisUsedAdapter.getCurrentList());
        boolean added = false;
        for (Diagnosis d : currentList) {
            if (d.getCode().equals(diagnosis.getCode())) {
                Toast.makeText(requireContext(),R.string.diagnosisAlreadyAdded,Toast.LENGTH_SHORT).show();
                added = true;
                break;
            }
        }
        if (added) return;
        App.hideKeyBoard(requireContext(), requireView());
        currentList.add(diagnosis);
        diagnosisUsedAdapter.submitList(currentList);

        binding.diagnosisRecyclerView.setVisibility(View.VISIBLE);
        binding.addDiagnosis.setVisibility(View.VISIBLE);
        binding.searchDiagnosisText.setText("");
        binding.searchDiagnosisLayout.setVisibility(View.GONE);
    }
}