package com.injent.miscalls.ui.editor;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import android.print.PDFPrint;
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
import com.injent.miscalls.ui.pdfviewer.PdfBundle;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;

public class EditorFragment extends Fragment {

    private FragmentEditorBinding binding;

    private EditorViewModel viewModel;
    private NavController navController;

    private final ActivityResultLauncher<String[]> activityResultLauncher;

    private boolean saveFragment;
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

        viewModel = new ViewModelProvider(requireActivity()).get(EditorViewModel.class);
        navController = Navigation.findNavController(requireView());

        if (getArguments() == null) return;

        activityResultLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});

        viewModel.getRegistryLiveData().observe(getViewLifecycleOwner(), this::loadRegistryData);
        if (!getArguments().getBoolean(getString(R.string.keySaveFragment)))
            viewModel.loadRegistry(getArguments().getInt(getString(R.string.keyRegistryId)));
        viewModel.getGeneratedPdfItemsLiveData().observe(getViewLifecycleOwner(), bundle -> {
            if (!getArguments().getBoolean(getString(R.string.keySaveFragment))) {
                generatePdf(bundle);
            }
        });

        binding.editorBack.setOnClickListener(v -> navigateToRegistry());

        binding.editorCard.setOnClickListener(v -> loadPdfGenerator());

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

        binding.editorDelete.setOnClickListener(view1 -> {

        });
    }

    private void loadRegistryData(@NonNull Registry registry) {
        if (getArguments().getBoolean(getString(R.string.keySaveFragment))) {
            registry = viewModel.getRegistryLiveData().getValue();
            if (registry == null) return;
        }
        binding.editorRecText.setText(registry.getRecommendation());
        binding.editorFullName.setText(registry.getCallInfo().getFullName());
        binding.editorInspectionText.setText(registry.getInspection());
        binding.createDateText.setText(registry.getCreateDate());
    }

    private void loadPdfGenerator() {
        viewModel.generatePdf(requireContext());
    }

    private void saveChanges() {
        changesAreSaved = true;
        Toast.makeText(requireContext(), R.string.changesAreSaved, Toast.LENGTH_SHORT).show();
        viewModel.saveChanges();
    }

    private void generatePdf(@NonNull PdfBundle bundle) {
        PDFUtil.generatePDFFromHTML(requireActivity(), new File(bundle.getFilePath()), bundle.getHtml(), new PDFPrint.OnPDFPrintListener() {
            @Override
            public void onSuccess(File file) {
                navigateToPdfPreview(file.getAbsolutePath());
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
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

    private void navigateToRegistry() {
        saveFragment = false;
        if (!changesAreSaved) {
            AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dataWontSave)
                    .setPositiveButton(R.string.ok, (dialog, b) -> navController.navigate(R.id.registryFragment))
                    .setNegativeButton(R.string.cancel, (dialog, b) -> dialog.dismiss())
                    .create();
            alertDialog.show();
        } else {
            navController.navigate(R.id.registryFragment);
        }
    }

    private void navigateToPdfPreview(String path) {
        saveFragment = true;
        Registry registry = viewModel.getRegistryLiveData().getValue();
        if (registry == null) return;
        Bundle args = new Bundle();
        args.putString(getString(R.string.keyPath), path);
        args.putString(getString(R.string.keyFileName), registry.getCallInfo().getFullName() + "-" + registry.getCreateDate());
        navController.navigate(R.id.pdfPreviewFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!saveFragment) {
            viewModel.onCleared();
        }
        binding = null;
    }
}