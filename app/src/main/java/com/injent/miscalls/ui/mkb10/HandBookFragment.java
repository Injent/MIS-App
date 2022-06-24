package com.injent.miscalls.ui.mkb10;

import static com.injent.miscalls.ui.diagnosis.DiagnosisFragment.SEARCH_DELAY;

import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentHandbookBinding;
import com.injent.miscalls.util.CustomOnBackPressedFragment;
import com.injent.miscalls.ui.adapters.DiagnosisAdapter;

import java.util.Collections;
import java.util.Objects;

public class HandBookFragment extends Fragment implements CustomOnBackPressedFragment {

    public static final int DIAGNOSIS_SEARCH_LIMIT = 20;

    private HandBookViewModel viewModel;
    private FragmentHandbookBinding binding;
    private DiagnosisAdapter adapter;
    private NavController navController;
    private FragmentManager fragmentManager;
    private int diagnosisId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            diagnosisId = getArguments().getInt(getString(R.string.keyDiagnosisId));
        } else {
            diagnosisId = -1;
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_handbook, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HandBookViewModel.class);
        navController = Navigation.findNavController(requireView());
        fragmentManager = requireActivity().getSupportFragmentManager();

        setListeners();
        setupHandbookRecyclerView();
        setupSearch();

        viewModel.loadDiagnosesByParent(diagnosisId);
    }

    private void setupHandbookRecyclerView() {
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.list_divider, requireContext().getTheme())));
        binding.handbookRecycler.addItemDecoration(divider);
        binding.handbookRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.handbookRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.handbookRecycler.setItemAnimator(null);
        adapter = new DiagnosisAdapter(diagnosis -> {
            if (!diagnosis.isNotParent())
                nextPage(diagnosis.getId());
        });
        binding.handbookRecycler.setAdapter(adapter);
    }

    @Override
    public boolean onBackPressed() {
        if (diagnosisId == -1) {
            viewModel.onCleared();
        }
        return true;
    }

    private void setListeners() {
        // Listeners
        binding.handbookBack.setOnClickListener(v -> fragmentManager.popBackStack());
        binding.handbookSearch.setOnClickListener(v -> showSearch());
        binding.handbookSearchCancel.setOnClickListener(v -> {
            binding.handbookSearchText.setText("");
            hideSearch();
        });

        // Observers
        viewModel.getDiagnoses().observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list);
            if (list.isEmpty()) {
                binding.handbookError.setVisibility(View.VISIBLE);
            } else {
                binding.handbookError.setVisibility(View.GONE);
            }
        });

        viewModel.getSearchDiagnoses().observe(getViewLifecycleOwner(), list -> adapter.submitList(list));
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
                    adapter.submitList(Collections.emptyList());
                    return;
                }
                runnable = () -> viewModel.searchDiagnoses(s.toString().trim(), DIAGNOSIS_SEARCH_LIMIT);
                handler.postDelayed(runnable, SEARCH_DELAY);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }
        };
        binding.handbookSearchText.addTextChangedListener(textWatcherSearchListener);
    }

    private void showSearch() {
        binding.handbookSearchLayout.setVisibility(View.VISIBLE);
        binding.titleHandBook.setVisibility(View.INVISIBLE);
        binding.handbookSearch.setVisibility(View.INVISIBLE);
        binding.handbookSearch.setEnabled(false);
        adapter.submitList(viewModel.getSearchDiagnoses().getValue());
    }

    private void hideSearch() {
        binding.handbookSearchLayout.setVisibility(View.GONE);
        binding.titleHandBook.setVisibility(View.VISIBLE);
        binding.handbookSearch.setVisibility(View.VISIBLE);
        binding.handbookSearch.setEnabled(true);
        adapter.submitList(viewModel.getDiagnoses().getValue());
    }

    private void nextPage(int id) {
        Bundle args = new Bundle();
        args.putInt(getString(R.string.keyDiagnosisId), id);
        navController.navigate(R.id.handBookFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
        fragmentManager = null;
        navController = null;
        binding = null;
    }
}