package com.injent.miscalls.ui.diagnosis;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.diagnosis.Diagnosis;
import com.injent.miscalls.databinding.FragmentDiagnosisBinding;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DiagnosisFragment extends Fragment {

    private FragmentDiagnosisBinding binding;
    private DiagnosisUsedAdapter diagnosisUsedAdapter;
    private DiagnosisSearchAdapter diagnosisSearchAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_diagnosis,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        binding.addDiagnosis.setOnClickListener(view1 -> showDiagnosisSearch());

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

    private void showDiagnosisSearch() {
        binding.searchDiagnosisLayout.setVisibility(View.VISIBLE);
        binding.addDiagnosis.setVisibility(View.GONE);
        binding.diagnosisRecyclerView.setVisibility(View.GONE);
        try {
            diagnosisSearchAdapter.submitList(getDiagnosisList(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addDiagnosis(Diagnosis diagnosis) {
        List<Diagnosis> currentList = new ArrayList<>(diagnosisUsedAdapter.getCurrentList());
        boolean added = false;
        for (Diagnosis d : currentList) {
            if (d.getContent().equals(diagnosis.getContent())) {
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

    private List<Diagnosis> getDiagnosisList() throws IOException {
        AssetManager assetManager = requireContext().getAssets();
        InputStream inputStream = assetManager.open("diagnoses.txt");
        Scanner scanner = new Scanner(inputStream);
        List<String> diagnosesRaw = new ArrayList<>();
        while (scanner.hasNextLine()){
            diagnosesRaw.add(scanner.nextLine());
        }
        scanner.close();
        List<Diagnosis> diagnosisList = new ArrayList<>();
        for (int i = 0; i < diagnosesRaw.size(); i++) {
            diagnosisList.add(new Diagnosis(i, diagnosesRaw.get(i)));
        }
        return diagnosisList;
    }
}