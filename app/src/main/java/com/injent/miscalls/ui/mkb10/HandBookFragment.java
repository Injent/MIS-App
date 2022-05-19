package com.injent.miscalls.ui.mkb10;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.databinding.FragmentHandbookBinding;

import java.util.Objects;

public class HandBookFragment extends Fragment {

    private HandBookViewModel viewModel;
    private FragmentHandbookBinding binding;
    private DiagnosisAdapter adapter;
    private int previousPage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_handbook, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HandBookViewModel.class);

        viewModel.getDiagnosesLiveData().observe(getViewLifecycleOwner(), list -> adapter.submitList(list));
        viewModel.getBackDiagnosis().observe(getViewLifecycleOwner(), list -> adapter.submitList(list,true));
        viewModel.getSelectedDiagnosisLiveData().observe(getViewLifecycleOwner(), this::showDiagnosis);

        setupHandbookRecyclerView();

        viewModel.loadDiagnosesByParent(-1);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });
    }

    private void setupHandbookRecyclerView() {
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.list_divider, requireContext().getTheme())));
        binding.handbookRecycler.addItemDecoration(divider);
        binding.handbookRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.handbookRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.handbookRecycler.setItemAnimator(null);
        adapter = new DiagnosisAdapter(new DiagnosisAdapter.OnItemClickListener() {
            @Override
            public void onClickStar(int diagnosisId) {

            }

            @Override
            public void onClick(Diagnosis diagnosis) {
                if (diagnosis.isParent())
                    viewModel.loadDiagnosesByParent(diagnosis.getId());
                else {
                    Log.d("AE", "onClick: " + diagnosis.getName());
                }
            }

            @Override
            public void nextPage(int id) {

            }

            @Override
            public void previousPage(int id) {
                previousPage = id;
                Log.d("TAG", "previousPage: " + id);
            }
        });
        binding.handbookRecycler.setAdapter(adapter);
    }

    private void showDiagnosis(Diagnosis diagnosis) {
        Log.e("TAG", "showDiagnosis: " + diagnosis.getName() );
    }

    private void navigateToHome() {
        Bundle args = new Bundle();
        args.putBoolean(getString(R.string.keyUpdateList), true);
        Navigation.findNavController(requireView()).navigate(R.id.homeFragment, args);
    }
}