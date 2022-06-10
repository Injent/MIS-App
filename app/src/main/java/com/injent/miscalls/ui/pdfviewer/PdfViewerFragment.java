package com.injent.miscalls.ui.pdfviewer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.print.PDFPrint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentPdfViewerBinding;
import com.injent.miscalls.domain.repositories.PdfRepository;
import com.injent.miscalls.ui.editor.EditorViewModel;

import java.io.File;

public class PdfViewerFragment extends Fragment {

    private EditorViewModel viewModel;
    private FragmentPdfViewerBinding binding;
    private int previousFragmentId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            previousFragmentId = getArguments().getInt(getString(R.string.keyFragmentId));
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pdf_viewer, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(EditorViewModel.class);

        binding.pdfWebView.setInitialScale(150);

        viewModel.getHtmlLiveData().observe(getViewLifecycleOwner(), s -> {
            binding.pdfWebView.loadDataWithBaseURL(null, s, "text/html", "utf-8", null);
            binding.pdfPreviewLoad.setVisibility(View.GONE);
            binding.pdfViewerSave.setEnabled(true);
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

        binding.pdfViewerBack.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(previousFragmentId));
    }
}