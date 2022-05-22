package com.injent.miscalls.ui.pdfviewer;

import android.graphics.Bitmap;
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

import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentPdfPreviewBinding;

import java.io.File;

public class PdfPreviewFragment extends Fragment {

    private FragmentPdfPreviewBinding binding;
    private PdfPreviewViewModel viewModel;

    private File tempFile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pdf_preview, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PdfPreviewViewModel.class);

        if (getArguments() == null) {
            navigateToEditor();
            return;
        }

        viewModel.loadBitmap(getArguments().getString(getString(R.string.keyPath)), 0);

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToEditor();
            }
        });

        binding.pdfViewerBack.setOnClickListener(v -> navigateToEditor());

        viewModel.getBitmapLiveData().observe(getViewLifecycleOwner(), this::loadImage);

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), throwable -> {navigateToEditor();
            throwable.printStackTrace();});

        binding.pdfViewerSave.setOnClickListener(v -> saveFile());
    }

    private void saveFile() {
        tempFile = new File(getArguments().getString(getString(R.string.keyPath)));
        String fileName = getArguments().getString(getString(R.string.keyFileName));
        viewModel.saveFile(tempFile, fileName);
        Toast.makeText(requireContext(),R.string.documentSaved,Toast.LENGTH_LONG).show();
        navigateToEditor();
    }

    private void navigateToEditor() {
        Bundle args = new Bundle();
        args.putBoolean(getString(R.string.keySaveFragment), true);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.editorFragment, args);
    }

    private void loadImage(Bitmap bitmap) {
        binding.pdfPreviewLoad.setVisibility(View.GONE);
        binding.pdfImage.setImageBitmap(bitmap);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (tempFile != null && tempFile.exists())
            tempFile.delete();
        viewModel.onCleared();
        binding = null;
    }
}