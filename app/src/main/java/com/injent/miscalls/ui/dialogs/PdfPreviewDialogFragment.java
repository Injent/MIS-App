package com.injent.miscalls.ui.dialogs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.injent.miscalls.R;

public class PdfPreviewDialogFragment extends DialogFragment {

    public static final String TAG = "PdfPreviewDialog";

    private String html;

    public PdfPreviewDialogFragment(String html) {
        super(R.layout.dialog_pdf_preview);
        this.html = html;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.dialog_pdf_preview, null);
        WebView pdfWebView = view.findViewById(R.id.dialogPdf);
        pdfWebView.setInitialScale(150);
        pdfWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        html = null;
    }
}
