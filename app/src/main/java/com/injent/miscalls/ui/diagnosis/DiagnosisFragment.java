package com.injent.miscalls.ui.diagnosis;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.databinding.FragmentDiagnosisBinding;
import com.injent.miscalls.ui.callstuff.CallStuffViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        viewModel = new ViewModelProvider(requireActivity()).get(CallStuffViewModel.class);

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

        binding.cancelSearch.setOnClickListener(view0 -> {
            binding.diagnosisRecyclerView.setVisibility(View.VISIBLE);
            binding.searchDiagnosisText.setText("");
            binding.searchDiagnosisLayout.setVisibility(View.GONE);
        });

        setupSearchRecyclerView();

        //Observer
        viewModel.getDiagnosisDatabaseListLiveData().observe(getViewLifecycleOwner(), list -> diagnosisSearchAdapter.submitList(list));

        viewModel.loadDiagnosisDatabase();

        binding.diagnosisRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        diagnosisUsedAdapter = new DiagnosisUsedAdapter(new DiagnosisUsedAdapter.OnItemClickListener() {
            @Override
            public void onClick(Diagnosis diagnosis) {
                List<Diagnosis> diagnosisList = new ArrayList<>(diagnosisUsedAdapter.getCurrentList());
                int idToDelete = 0;
                for (int i = 0; i < diagnosisList.size(); i++) {
                    if (diagnosisList.get(i).equals(diagnosis)) {
                        idToDelete = i;
                        break;
                    }
                }
                diagnosisList.remove(idToDelete);
                diagnosisUsedAdapter.submitList(diagnosisList);
                viewModel.setCurrentDiagnoses(diagnosisList);
                Toast.makeText(requireContext(),R.string.diagnoseDeleted,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddClick() {
                showDiagnosisSearch();
            }
        });
        binding.diagnosisRecyclerView.setAdapter(diagnosisUsedAdapter);
        binding.diagnosisRecyclerView.setItemAnimator(null);
    }

    private void setupSearchRecyclerView() {
        binding.diagnosisSelectRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        diagnosisSearchAdapter = new DiagnosisSearchAdapter(this::addDiagnosis);
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.divider_layer, requireContext().getTheme())));
        binding.diagnosisSelectRecyclerView.addItemDecoration(divider);
        binding.diagnosisSelectRecyclerView.setAdapter(diagnosisSearchAdapter);
    }

    private void showDiagnosisSearch() {
        binding.searchDiagnosisLayout.setVisibility(View.VISIBLE);
        binding.diagnosisRecyclerView.setVisibility(View.GONE);
    }

    private void addDiagnosis(Diagnosis diagnosis) {
        List<Diagnosis> currentList = new ArrayList<>(diagnosisUsedAdapter.getCurrentList());
        boolean added = false;
        for (Diagnosis d : currentList) {
            if (d.equals(diagnosis)) {
                Toast.makeText(requireContext(),R.string.diagnosisAlreadyAdded,Toast.LENGTH_SHORT).show();
                added = true;
                break;
            }
        }
        if (added) return;
        App.hideKeyBoard(requireContext(), requireView());
        currentList.add(diagnosis);
        diagnosisUsedAdapter.submitList(currentList);
        viewModel.setCurrentDiagnoses(currentList);

        binding.diagnosisRecyclerView.setVisibility(View.VISIBLE);
        binding.searchDiagnosisText.setText("");
        binding.searchDiagnosisLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getViewModelStore().clear();
        binding = null;
    }
}