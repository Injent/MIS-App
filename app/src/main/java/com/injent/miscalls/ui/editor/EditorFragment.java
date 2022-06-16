package com.injent.miscalls.ui.editor;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
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

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.databinding.FragmentEditorBinding;
import com.injent.miscalls.ui.overview.OverviewViewModel;

import java.util.function.Supplier;

public class EditorFragment extends Fragment {

    private FragmentEditorBinding binding;

    private OverviewViewModel viewModel;

    private final ActivityResultLauncher<String[]> activityResultLauncher;

    private boolean changesAreSaved;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_editor, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(OverviewViewModel.class);

        activityResultLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});

        setListeners();
    }

    private void setListeners() {
        // Listeners
        binding.editorSaveDoc.setOnClickListener(v -> sendRegistry());
        binding.editorCard.setOnClickListener(view -> viewModel.setClickPreviewPdf(true));

        // Observers
        viewModel.getSelectedRegistryLiveData().observe(getViewLifecycleOwner(), this::loadRegistryData);
    }

    private void loadRegistryData(Registry registry) {
        if (registry == null) return;
        binding.editorFullName.setText(registry.getCallInfo().getFullName());
        binding.createDateText.setText(registry.getCreateDate());
        binding.editorCard.setEnabled(true);
    }

    private void sendRegistry() {
        changesAreSaved = true;
        Toast.makeText(requireContext(), R.string.documentSending, Toast.LENGTH_SHORT).show();
        viewModel.sendDocument(() -> {
            Toast.makeText(requireContext(), R.string.docsSent, Toast.LENGTH_SHORT).show();
            return null;
        });
    }

    private void requestPermission() {
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.allow)
                .setPositiveButton(R.string.ok, (dialog, b) -> activityResultLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}))
                .setNegativeButton(R.string.cancel, (dialog, b) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}