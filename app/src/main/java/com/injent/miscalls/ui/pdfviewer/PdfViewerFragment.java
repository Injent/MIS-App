package com.injent.miscalls.ui.pdfviewer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.print.PDFPrint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentPdfViewerBinding;
import com.injent.miscalls.ui.overview.OverviewViewModel;

import java.io.File;

public class PdfViewerFragment extends Fragment {

    private OverviewViewModel viewModel;
    private FragmentPdfViewerBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pdf_viewer, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(OverviewViewModel.class);

        binding.pdfWebView.setInitialScale(150);

        viewModel.getHtmlLiveData().observe(getViewLifecycleOwner(), s -> {
            binding.pdfWebView.loadDataWithBaseURL(null, s, "text/html", "utf-8", null);
            binding.pdfPreviewLoad.setVisibility(View.GONE);
            binding.pdfViewerSave.setEnabled(true);
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), throwable -> {
            binding.pdfPreviewLoad.setVisibility(View.INVISIBLE);
            Toast.makeText(requireContext(), R.string.unknownError, Toast.LENGTH_LONG).show();
        });

        viewModel.loadHtml(requireContext());

        binding.pdfViewerSave.setOnClickListener(v -> viewModel.generatePdf(requireContext(), new PDFPrint.OnPDFPrintListener() {
            @Override
            public void onSuccess(File file) {
                String toastText = getString(R.string.fileSaveByPath) + file.getPath();
                Toast.makeText(requireContext(), toastText, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(requireContext(), R.string.errorOfDocumentGeneration, Toast.LENGTH_SHORT).show();
            }
        }, file -> {
            final boolean[] replace = new boolean[1];

            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.fileAlreadyExists)
                    .setMessage(R.string.replaceFile)
                    .setPositiveButton(R.string.yes, (dialog, b) -> {
                        replace[0] = true;
                        String toastText = getString(R.string.fileSaveByPath) + file.getPath();
                        Toast.makeText(requireContext(), toastText, Toast.LENGTH_LONG).show();
                    })
                    .setNegativeButton(R.string.no, (dialog, b) -> dialog.dismiss())
                    .create()
                    .show();
            return replace[0];
        }));
    }
}