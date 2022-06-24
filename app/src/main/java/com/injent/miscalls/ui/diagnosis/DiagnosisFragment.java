package com.injent.miscalls.ui.diagnosis;

import static com.injent.miscalls.domain.repositories.DiagnosisRepository.DIAGNOSES_SEARCH_LIMIT;

import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.diagnosis.Diagnosis;
import com.injent.miscalls.databinding.FragmentDiagnosisBinding;
import com.injent.miscalls.ui.callstuff.CallStuffViewModel;
import com.injent.miscalls.ui.adapters.DiagnosisAdapter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DiagnosisFragment extends Fragment {

    public static final long SEARCH_DELAY = 500;

    private FragmentDiagnosisBinding binding;
    private DiagnosisUsedAdapter diagnosisUsedAdapter;
    private DiagnosisAdapter diagnosesSearchAdapter;
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
        setupSearchRecyclerView();
        setupSearch();

        if (inspected && viewModel.getCurrentRegistryLiveData().getValue() != null) {
            diagnosisUsedAdapter.submitList(viewModel.getCurrentRegistryLiveData().getValue().getDiagnoses());
        }
    }

    private void setupSearch() {
        final TextWatcher textWatcherSearchListener = new TextWatcher() {
            final android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
            Runnable runnable;

            public void onTextChanged(final CharSequence s, int start, final int before, int count) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.toString().trim().isEmpty()) {
                    diagnosesSearchAdapter.submitList(Collections.emptyList());
                    return;
                }
                runnable = () -> viewModel.searchDiagnosis(s.toString().trim(), DIAGNOSES_SEARCH_LIMIT);
                handler.postDelayed(runnable, SEARCH_DELAY);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        };

        binding.searchDiagnosisText.addTextChangedListener(textWatcherSearchListener);
    }

    private void setListeners() {
        // Listeners
        binding.cancelSearch.setOnClickListener(v -> {
            binding.diagnosisRecyclerView.setVisibility(View.VISIBLE);
            binding.searchDiagnosisText.setText("");
            binding.searchDiagnosisLayout.setVisibility(View.GONE);
        });

        // Observers
        viewModel.getSearchDiagnoses().observe(getViewLifecycleOwner(), list -> {
            diagnosesSearchAdapter.submitList(list);
            Log.e("TAG", "setListeners: " );
        });
    }

    private void setupDiagnosesRecyclerView() {
        binding.diagnosisRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        diagnosisUsedAdapter = new DiagnosisUsedAdapter(new DiagnosisUsedAdapter.OnItemClickListener() {
            @Override
            public void onDelete(Diagnosis diagnosis) {
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
        diagnosesSearchAdapter = new DiagnosisAdapter(diagnosis -> viewModel.addItemToList(diagnosisUsedAdapter.getCurrentList(), diagnosis, list -> {
            if (list != null) {
                binding.searchDiagnosisLayout.setVisibility(View.GONE);
                binding.searchDiagnosisText.setText("");
                binding.diagnosisRecyclerView.setVisibility(View.VISIBLE);
                diagnosisUsedAdapter.submitList(list);
                return;
            }
            Toast.makeText(requireContext(), R.string.diagnosisAlreadyAdded,Toast.LENGTH_SHORT).show();
        }));
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.divider_layer, requireContext().getTheme())));
        binding.diagnosisSelectRecyclerView.addItemDecoration(divider);
        binding.diagnosisSelectRecyclerView.setAdapter(diagnosesSearchAdapter);
    }

    private void showDiagnosisSearch() {
        binding.searchDiagnosisLayout.setVisibility(View.VISIBLE);
        binding.diagnosisRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        diagnosisUsedAdapter = null;
        diagnosesSearchAdapter = null;
        binding = null;
    }
}