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
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.injent.miscalls.MainActivity;
import com.injent.miscalls.R;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.databinding.FragmentRegistryBinding;
import com.injent.miscalls.domain.repositories.RegistryRepository;
import com.injent.miscalls.ui.Section;
import com.injent.miscalls.ui.SectionAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class RegistryFragment extends Fragment {

    private RegistryAdapter adapter;
    private RegistryViewModel viewModel;
    private FragmentRegistryBinding binding;
    private NavController navController;
    private int deleteRegistryId;
    private Snackbar snackbar;
    public static final long DELETE_DELAY = 5000L;
    private Timer timer;
    private final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                viewModel.deleteSelectedRegistry(deleteRegistryId);
                if (snackbar != null)
                    snackbar.dismiss();
            }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            if (getArguments().containsKey(getString(R.string.keyDeleteRegistry))) {
                deleteRegistryId = getArguments().getInt(getString(R.string.keyDeleteRegistry));
            } else {
                deleteRegistryId = -1;
            }
        } else {
            deleteRegistryId = -1;
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registry, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RegistryViewModel.class);
        navController = Navigation.findNavController(requireView());

        setListeners();

        setupSectionRecyclerView();
        setupRegistryRecyclerView();

        if (deleteRegistryId != -1) {
            deleteRegistry();
        }
    }

    private void setupRegistryRecyclerView() {
        adapter = new RegistryAdapter(this::navigateToEditor);
        binding.registryItemRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.registryItemRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.registryItemRecyclerView.setAdapter(adapter);

        viewModel.loadRegistryItems(deleteRegistryId);
    }

    private void setListeners() {
        //Listeners
        binding.backFromRegistry.setOnClickListener(v -> navigateToHome());

        binding.registrySearchButton.setOnClickListener(v -> showSearch());

        binding.registrySearchCancel.setOnClickListener(v -> hideSearch());

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });

        //Observers
        viewModel.getRegistryItemsLiveData().observe(getViewLifecycleOwner(), registryItems -> {
            adapter.submitList(registryItems);
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), throwable -> {

        });
    }

    private void deleteRegistry() {
        timer = new Timer();
        timer.schedule(timerTask, DELETE_DELAY);
        snackbar = Snackbar.make(requireView(), R.string.registryDeleted, BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setAction(R.string.cancel, view -> {
                    timer.cancel();
                    List<Registry> list = new ArrayList<>(adapter.getCurrentList());
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setDelete(false);
                    }
                    adapter.submitList(list);
                })
                .setBackgroundTint(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, requireContext().getTheme()))
                .setTextColor(ResourcesCompat.getColor(getResources(), R.color.darkGrayText, requireContext().getTheme()))
                .setActionTextColor(ResourcesCompat.getColor(getResources(), R.color.red, requireContext().getTheme()));
        snackbar.show();
    }

    private void setupSectionRecyclerView() {
        SectionAdapter sectionAdapter = new SectionAdapter(type -> {
            if (Section.Type.SUBMIT_ALL == type) {
                if (viewModel.getRegistryItemsLiveData().getValue() != null && !viewModel.getRegistryItemsLiveData().getValue().isEmpty()) {
                    Toast.makeText(requireContext(), R.string.docsSent,Toast.LENGTH_LONG).show();
                    viewModel.sendRegistries();
                } else {
                    Toast.makeText(requireContext(), R.string.listEmpty,Toast.LENGTH_LONG).show();
                }
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
        navController.navigate(R.id.overviewFragment, args);
    }

    private boolean notMatchingDestination() {
        return Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.registryFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer = null;
        if (snackbar != null)
            snackbar.dismiss();
        snackbar = null;
        viewModel.onCleared();
        binding = null;
    }
}