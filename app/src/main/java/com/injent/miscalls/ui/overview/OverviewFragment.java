package com.injent.miscalls.ui.overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.databinding.FragmentOverviewBinding;
import com.injent.miscalls.ui.adapters.ViewPagerAdapter;

public class OverviewFragment extends Fragment {

    private OverviewViewModel viewModel;
    private FragmentOverviewBinding binding;
    private NavController navController;
    private ViewPagerAdapter adapter;
    private int registryId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            registryId = getArguments().getInt(getString(R.string.keyRegistryId));
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(OverviewViewModel.class);
        viewModel.init();
        navController = Navigation.findNavController(requireView());

        setListeners();
        setupViewPager();
        binding.overviewTitle.setText(R.string.patientInspected);
        viewModel.loadSelectedRegistry(registryId);
    }

    private void setListeners() {
        viewModel.getSelectedRegistry().observe(getViewLifecycleOwner(), this::loadRegistryData);
        binding.overviewBack.setOnClickListener(view -> {
            if (binding.overviewViewPager.getCurrentItem() == 1) {
                binding.overviewTitle.setText(R.string.patientInspected);
                binding.overviewLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.invisible));
                binding.overviewViewPager.setCurrentItem(0, true);
                binding.overviewDelete.setEnabled(true);
                binding.overviewDelete.setVisibility(View.VISIBLE);
            } else {
                navigateToRegistry(false);
            }
        });

        viewModel.getClickPreviewPdf().observe(getViewLifecycleOwner(), click -> {
            binding.overviewTitle.setText(R.string.document);
            binding.overviewLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            binding.overviewViewPager.setCurrentItem(1, true);
            binding.overviewDelete.setEnabled(false);
            binding.overviewDelete.setVisibility(View.INVISIBLE);
        });

        binding.overviewDelete.setOnClickListener(v -> navigateToRegistry(true));
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(OverviewFragment.this, 2, viewModel);
        binding.overviewViewPager.setAdapter(adapter);
        binding.overviewViewPager.setUserInputEnabled(false);
    }

    private void loadRegistryData(Registry registry) {

    }

    private void navigateToRegistry(boolean deleteRegistry) {
        if (!deleteRegistry || viewModel.getSelectedRegistry().getValue() == null) {
            navController.navigate(R.id.registryFragment);
            return;
        }
        Bundle args = new Bundle();
        args.putInt(getString(R.string.keyDeleteRegistry), viewModel.getSelectedRegistry().getValue().getId());
        navController.navigate(R.id.registryFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
        adapter = null;
        navController = null;
        binding = null;
    }
}