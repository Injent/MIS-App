package com.injent.miscalls.ui.registry;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.MainActivity;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentRegistryBinding;
import com.injent.miscalls.domain.repositories.RegistryRepository;
import com.injent.miscalls.ui.Section;
import com.injent.miscalls.ui.SectionAdapter;

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
        setupRegistryRecyclerView();

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

    private void setupRegistryRecyclerView() {
        adapter = new RegistryAdapter(new RegistryAdapter.OnItemClickListener() {
            @Override
            public void onClick(int id) {
                navigateToEditor(id);
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
            if (Section.Type.SUBMIT_ALL == type) {
                Toast.makeText(requireContext(), R.string.docsSent,Toast.LENGTH_LONG).show();
                new RegistryRepository().dropTable();
            }
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
        MainActivity.hideKeyBoard(requireView());
    }

    private void navigateToHome() {
        if (notMatchingDestination()) return;
        navController.navigate(R.id.homeFragment);
    }

    private void navigateToEditor(int id) {
        Bundle args = new Bundle();
        args.putInt(getString(R.string.keyRegistryId), id);
        navController.navigate(R.id.editorFragment, args);
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