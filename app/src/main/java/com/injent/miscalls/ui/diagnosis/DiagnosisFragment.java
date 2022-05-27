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
import com.injent.miscalls.ui.mkb10.DiagnosisAdapter;

import java.util.List;
import java.util.Objects;

public class DiagnosisFragment extends Fragment {

    private FragmentDiagnosisBinding binding;
    private DiagnosisUsedAdapter diagnosisUsedAdapter;
    private DiagnosisAdapter diagnosesSearchAdapter;
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

        binding.cancelSearch.setOnClickListener(view0 -> {
            binding.diagnosisRecyclerView.setVisibility(View.VISIBLE);
            binding.searchDiagnosisText.setText("");
            binding.searchDiagnosisLayout.setVisibility(View.GONE);
        });

        setupSearchRecyclerView();

        //Observer
        viewModel.getDiagnosesListLiveData().observe(getViewLifecycleOwner(), list -> {
            diagnosesSearchAdapter.submitList(list, true);
        });

        binding.diagnosisRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        diagnosisUsedAdapter = new DiagnosisUsedAdapter(new DiagnosisUsedAdapter.OnItemClickListener() {
            @Override
            public void onLongClick(Diagnosis diagnosis) {
                List<Diagnosis> newList = viewModel.deleteItemFromList(diagnosisUsedAdapter.getCurrentList(), diagnosis);
                diagnosisUsedAdapter.submitList(newList);
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
        binding.diagnosisSelectRecyclerView.setItemAnimator(null);
        diagnosesSearchAdapter = new DiagnosisAdapter(new DiagnosisAdapter.OnItemClickListener() {
            @Override
            public void onClickStar(int diagnosisId) {

            }

            @Override
            public void onClick(Diagnosis diagnosis) {
                if (!diagnosis.isParent()) {
                    addDiagnosis(diagnosis);
                } else {
                    viewModel.loadDiagnosisList(diagnosis.getId());
                }
            }

            @Override
            public void nextPage(int id) {

            }

            @Override
            public void previousPage(int id) {

            }
        });
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.divider_layer, requireContext().getTheme())));
        binding.diagnosisSelectRecyclerView.addItemDecoration(divider);
        binding.diagnosisSelectRecyclerView.setAdapter(diagnosesSearchAdapter);

        binding.searchDiagnosisText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                diagnosesSearchAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Nothing to do
            }
        });
    }

    private void showDiagnosisSearch() {
        binding.searchDiagnosisLayout.setVisibility(View.VISIBLE);
        binding.diagnosisRecyclerView.setVisibility(View.GONE);
        viewModel.loadDiagnosisList(-1);
    }

    private void addDiagnosis(Diagnosis diagnosis) {
        List<Diagnosis> newList = viewModel.addItemToList(requireContext(), diagnosisUsedAdapter.getCurrentList(), diagnosis);
        App.hideKeyBoard(requireView());
        diagnosisUsedAdapter.submitList(newList);

        binding.diagnosisRecyclerView.setVisibility(View.VISIBLE);
        binding.searchDiagnosisText.setText("");
        binding.searchDiagnosisLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}