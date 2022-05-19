package com.injent.miscalls.ui.registry;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentRegistryBinding;
import com.injent.miscalls.ui.Section;
import com.injent.miscalls.ui.SectionAdapter;
import com.injent.miscalls.ui.callstuff.CallStuffViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegistryFragment extends Fragment {

    private RegistryAdapter adapter;
    private RegistryViewModel viewModel;
    private FragmentRegistryBinding binding;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registry, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RegistryViewModel.class);
        navController = Navigation.findNavController(requireView());

        //Observers
        viewModel.getRegistryItemsLiveData().observe(getViewLifecycleOwner(), registryItems -> adapter.submitList(registryItems));

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), throwable -> {

        });

        //RecyclerView
        setupSectionRecyclerView();
        setupDiagnosisRecyclerView();

        //Listeners
        binding.backFromRegistry.setOnClickListener(view0 -> navigateToHome());

        binding.registrySearchButton.setOnClickListener(view0 -> showSearch());

        binding.registrySearchCancel.setOnClickListener(view0 -> hideSearch());

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });
    }

    private void undoDeletingSnackbar() {
        @SuppressLint("ShowToast")
        Snackbar snackbar = Snackbar.make(requireView(),R.string.protocolDeleted, BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setAction(R.string.undoDeleting, view0 -> {

        });
        snackbar.show();
    }

    private void setupDiagnosisRecyclerView() {
        adapter = new RegistryAdapter(new RegistryAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //
            }

            @Override
            public void onLongClick(int position) {
                //
            }
        });
        binding.registryItemRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.registryItemRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.registryItemRecyclerView.setAdapter(adapter);

        viewModel.loadRegistryItems();
    }

    private void setupSectionRecyclerView() {
        SectionAdapter sectionAdapter = new SectionAdapter(type -> {

        });

        binding.registrySectionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.registrySectionsRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.registrySectionsRecyclerView.setAdapter(sectionAdapter);

        List<Section> sectionList = new ArrayList<>();
        Section section = new Section();
        section.setSectionName(getString(R.string.sendAll));
        section.setSectionType(Section.Type.SUBMIT_ALL);
        sectionList.add(section);
        sectionAdapter.submitList(sectionList);
    }

    private void showSearch() {
        binding.registryTitle.setVisibility(View.INVISIBLE);
        binding.registrySearchLayout.setVisibility(View.VISIBLE);
        binding.registrySearchButton.setVisibility(View.GONE);
    }

    private void hideSearch() {
        binding.registryTitle.setVisibility(View.VISIBLE);
        binding.registrySearchLayout.setVisibility(View.GONE);
        binding.registrySearchButton.setVisibility(View.VISIBLE);
        binding.registrySearchText.setText("");
        App.hideKeyBoard(requireContext(), requireView());
    }

    private void navigateToHome() {
        if (notMatchingDestination()) return;
        Bundle bundle = new Bundle();
        bundle.putBoolean(getString(R.string.keyUpdateList), true);
        navController.navigate(R.id.homeFragment, bundle);
    }

    private boolean notMatchingDestination() {
        return Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.registryFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
        binding = null;
    }
}