package com.injent.miscalls.ui.pdfviewer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.print.PDFPrint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentPdfViewerBinding;
import com.injent.miscalls.ui.overview.OverviewViewModel;

import java.io.File;

public class PdfViewerFragment extends Fragment {

    private OverviewViewModel viewModel;
    private FragmentPdfViewerBinding binding;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
               if (!Boolean.TRUE.equals(isGranted)) {
                   new AlertDialog.Builder(requireContext())
                           .setMessage(R.string.requestWritePermission)
                           .setPositiveButton(R.string.ok, (dialogInterface, i) ->
                                   this.requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                           .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                           .show();
               }
            });

    public PdfViewerFragment(ViewModel viewModel) {
        this.viewModel = (OverviewViewModel) viewModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pdf_viewer, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.pdfWebView.setInitialScale(150);
        viewModel.loadHtml(requireContext());
        setListeners();
    }

    private void setListeners() {
        // Listeners
        binding.pdfViewerSave.setOnClickListener(v -> generatePdfFile());

        // Observers
        viewModel.getHtml().observe(getViewLifecycleOwner(), s -> {
            binding.pdfWebView.loadDataWithBaseURL(null, s, "text/html", "utf-8", null);
            binding.pdfPreviewLoad.setVisibility(View.GONE);
            binding.pdfViewerSave.setEnabled(true);
        });

        viewModel.getRegistryError().observe(getViewLifecycleOwner(), throwable -> {
            binding.pdfPreviewLoad.setVisibility(View.INVISIBLE);
            Toast.makeText(requireContext(), R.string.unknownError, Toast.LENGTH_LONG).show();
        });
    }

    private boolean havePermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void generatePdfFile() {
        if (havePermission()) {
            viewModel.generatePdf(requireContext(), new PDFPrint.OnPDFPrintListener() {
                @Override
                public void onSuccess(File file) {
                    String toastText = getString(R.string.fileSaveByPath) + file.getPath();
                    Toast.makeText(requireContext(), toastText, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(requireContext(), R.string.errorOfDocumentGeneration, Toast.LENGTH_SHORT).show();
                }
            }, file -> true);
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}