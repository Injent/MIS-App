package com.injent.miscalls.ui.diagnosis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.diagnosis.Diagnosis;
import com.injent.miscalls.databinding.FragmentDiagnosisBinding;
import com.injent.miscalls.ui.callstuff.CallStuffFragment;
import com.injent.miscalls.ui.callstuff.CallStuffViewModel;

import java.util.List;

public class DiagnosisFragment extends Fragment {

    private FragmentDiagnosisBinding binding;
    private DiagnosisUsedAdapter diagnosisUsedAdapter;
    private CallStuffViewModel viewModel;
    private boolean inspected;

    public DiagnosisFragment(ViewModel viewModel) {
        this.viewModel = (CallStuffViewModel) viewModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_diagnosis,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewModel.getCallLiveData().getValue() != null)
            inspected = viewModel.getCallLiveData().getValue().isInspected();

        setListeners();
        setupDiagnosesRecyclerView();

        if (inspected && viewModel.getCurrentRegistryLiveData().getValue() != null) {
            diagnosisUsedAdapter.submitList(viewModel.getCurrentRegistryLiveData().getValue().getDiagnoses());
        }
    }

    private void setListeners() {
        // Listeners
        binding.cancelSearch.setOnClickListener(v -> {
            binding.diagnosisRecyclerView.setVisibility(View.VISIBLE);
            binding.searchDiagnosisText.setText("");
            binding.searchDiagnosisLayout.setVisibility(View.GONE);
        });

        // Observers
        viewModel.getSelectedDiagnosis().observe(getViewLifecycleOwner(),
                diagnosis -> viewModel.addItemToList(diagnosisUsedAdapter.getCurrentList(), diagnosis, diagnoses -> diagnosisUsedAdapter.submitList(diagnoses)));
    }

    private void setupDiagnosesRecyclerView() {
        binding.diagnosisRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        diagnosisUsedAdapter = new DiagnosisUsedAdapter(new DiagnosisUsedAdapter.OnItemClickListener() {
            @Override
            public void onDelete(Diagnosis diagnosis) {
                List<Diagnosis> newList = viewModel.deleteItemFromList(diagnosisUsedAdapter.getCurrentList(), diagnosis);
                diagnosisUsedAdapter.submitList(newList);
                Toast.makeText(requireContext(), R.string.diagnoseDeleted, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddClick() {
                viewModel.setPreviousFragment(DiagnosisFragment.this);
                viewModel.runAction(CallStuffFragment.CODE_OPEN_HANDBOOK);
            }
        });
        binding.diagnosisRecyclerView.setAdapter(diagnosisUsedAdapter);
        binding.diagnosisRecyclerView.setItemAnimator(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        diagnosisUsedAdapter = null;
        binding = null;
    }
}