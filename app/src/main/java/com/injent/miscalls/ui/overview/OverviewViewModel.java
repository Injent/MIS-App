package com.injent.miscalls.ui.overview;

import android.content.Context;
import android.print.PDFPrint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.domain.repositories.PdfRepository;
import com.injent.miscalls.domain.repositories.RegistryRepository;

import java.io.IOException;

public class OverviewViewModel extends ViewModel {

    private RegistryRepository registryRepository;
    private PdfRepository pdfRepository;

    private LiveData<Throwable> registryError;
    private LiveData<Throwable> pdfError;
    private LiveData<Registry> selectedRegistry;
    private LiveData<String> html;
    private LiveData<String> docSentMessage;
    private MutableLiveData<Boolean> clickPreviewPdf = new MutableLiveData<>();

    public void init() {
        registryRepository = new RegistryRepository();
        pdfRepository = new PdfRepository();
        docSentMessage = registryRepository.getDocSentMessage();
        selectedRegistry = registryRepository.getRegistry();
        registryError = registryRepository.getError();
        pdfError = pdfRepository.getError();
        html = pdfRepository.getHtml();
    }

    public LiveData<Throwable> getErrorLiveData() {
        return registryError;
    }

    public LiveData<Registry> getSelectedRegistryLiveData() {
        return selectedRegistry;
    }

    public void loadSelectedRegistry(int id) {
        registryRepository.loadRegistryById(id);
    }

    public LiveData<String> getHtmlLiveData() {
        return html;
    }

    public void loadHtml(Context context) {
        pdfRepository.loadFetchedHtml(context, selectedRegistry.getValue());
    }


    public void generatePdf(Context context, PDFPrint.OnPDFPrintListener pdfListener, PdfRepository.FileManageListener fileManageListener) {
        Registry registry = getSelectedRegistryLiveData().getValue();
        if (registry == null) {
            return;
        }
        String fileName = registry.getCallInfo().getFullName() + "-" + registry.getCreateDate();
        try {
            pdfRepository.generatePdf(context, html.getValue(), fileName, pdfListener, fileManageListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setClickPreviewPdf(boolean click) {
        this.clickPreviewPdf.postValue(click);
    }

    public LiveData<Boolean> getClickPreviewPdf() {
        return clickPreviewPdf;
    }

    public LiveData<String> getDocSentMessage() {
        return docSentMessage;
    }

    public void sendDocument() {
        registryRepository.sendDocument(selectedRegistry.getValue());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        selectedRegistry = null;
        registryError = null;
        html = null;
        clickPreviewPdf = null;
        pdfError = null;

        pdfRepository.clear();
        registryRepository.clear();
    }
}
