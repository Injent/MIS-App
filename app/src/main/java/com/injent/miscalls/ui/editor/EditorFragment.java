package com.injent.miscalls.ui.editor;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.databinding.FragmentEditorBinding;

public class EditorFragment extends Fragment {

    private FragmentEditorBinding binding;

    private EditorViewModel viewModel;
    private NavController navController;

    private final ActivityResultLauncher<String[]> activityResultLauncher;

    private boolean keepData;
    private boolean changesAreSaved;
    private int registryId;

    public EditorFragment() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean areAllGranted = true;
            for(Boolean b : result.values()) {
                areAllGranted = areAllGranted && b;
            }

            if(!areAllGranted) {
                requestPermission();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            registryId = getArguments().getInt(getString(R.string.keyRegistryId));
            keepData = getArguments().getBoolean(getString(R.string.keyKeepData));
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_editor, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(EditorViewModel.class);
        navController = Navigation.findNavController(requireView());

        activityResultLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});

        viewModel.getRegistryLiveData().observe(getViewLifecycleOwner(), this::loadRegistryData);

        viewModel.loadRegistry(registryId);

        setListeners();
    }

    private void setListeners() {
        binding.editorCard.setOnClickListener(view -> navigateToPdfPreview());
        binding.editorBack.setOnClickListener(v -> navigateToRegistry());

        binding.editorSaveDoc.setOnClickListener(v -> saveChanges());

        binding.editorInspectionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changesAreSaved = false;
                viewModel.setInspection(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Nothing to do
            }
        });

        binding.editorRecText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changesAreSaved = false;
                viewModel.setRecommendation(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Nothing to do
            }
        });

        binding.editorDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadRegistryData(@NonNull Registry registry) {
        if (keepData) {
            registry = viewModel.getRegistryLiveData().getValue();
        }
        if (registry == null) return;
        binding.editorRecText.setText(registry.getRecommendation());
        binding.editorFullName.setText(registry.getCallInfo().getFullName());
        binding.editorInspectionText.setText(registry.getAnamnesis());
        binding.createDateText.setText(registry.getCreateDate());
    }

    private void saveChanges() {
        changesAreSaved = true;
        Toast.makeText(requireContext(), R.string.changesAreSaved, Toast.LENGTH_SHORT).show();
        viewModel.saveChanges();
    }

    private void requestPermission() {
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.allow)
                .setPositiveButton(R.string.ok, (dialog, b) -> activityResultLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}))
                .setNegativeButton(R.string.cancel, (dialog, b) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    private void confirmDelete() {
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dataWillDeleteForever)
                .setPositiveButton(R.string.ok, (dialog, b) -> viewModel.deleteCurrentRegistry())
                .setNegativeButton(R.string.cancel, (dialog, b) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    private void navigateToRegistry() {
        keepData = false;
        if (!changesAreSaved) {
            AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dataWontSave)
                    .setMessage(R.string.beforeExitSaveData)
                    .setPositiveButton(R.string.ok, (dialog, b) -> navController.navigate(R.id.registryFragment))
                    .setNegativeButton(R.string.cancel, (dialog, b) -> dialog.dismiss())
                    .create();
            alertDialog.show();
        } else {
            navController.navigate(R.id.registryFragment);
        }
    }

    private void navigateToPdfPreview() {
        if (navController.getCurrentDestination() == null) return;
        keepData = true;
        Bundle args = new Bundle();
        args.putInt(getString(R.string.keyFragmentId), navController.getCurrentDestination().getId());
        navController.navigate(R.id.pdfViewerFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!keepData) {
            viewModel.onCleared();
        }
        binding = null;
    }
}