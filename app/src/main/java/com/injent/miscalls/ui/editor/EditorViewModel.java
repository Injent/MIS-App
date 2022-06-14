package com.injent.miscalls.ui.editor;

import android.content.Context;
import android.print.PDFPrint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.domain.repositories.PdfRepository;
import com.injent.miscalls.domain.repositories.RegistryRepository;

import java.io.IOException;

public class EditorViewModel extends ViewModel {

    private final RegistryRepository registryRepository;
    private final PdfRepository pdfRepository;

    private MutableLiveData<String> html = new MutableLiveData<>();
    private MutableLiveData<Registry> selectedRegistry = new MutableLiveData<>();
    private MutableLiveData<Throwable> error = new MutableLiveData<>();
    private MutableLiveData<String> inspection = new MutableLiveData<>();
    private MutableLiveData<String> recommendation = new MutableLiveData<>();

    public EditorViewModel() {
        registryRepository = new RegistryRepository();
        pdfRepository= new PdfRepository();
    }

    public LiveData<Registry> getRegistryLiveData() {
        return selectedRegistry;
    }

    public void loadRegistry(int id) {
        registryRepository.loadRegistryById(throwable -> {
            error.postValue(throwable);
            throwable.printStackTrace();
            return null;
        }, registry -> selectedRegistry.postValue(registry), id);
    }

    public void setInspection(String s) {
        inspection.setValue(s);
    }

    public void setRecommendation(String s) {
        recommendation.setValue(s);
    }

    public void saveChanges() {
        Registry registry = selectedRegistry.getValue();
        if (registry == null) {
            throw new IllegalStateException();
        }

        registry.setAnamnesis(inspection.getValue());
        registry.setRecommendation(recommendation.getValue());

        registryRepository.insertRegistry(throwable -> {
            error.postValue(throwable);
            return null;
        }, registry);
    }

    public void deleteCurrentRegistry() {
        if (selectedRegistry.getValue() == null) return;
        registryRepository.deleteRegistry(throwable -> {
            error.postValue(throwable);
            return null;
        }, selectedRegistry.getValue().getId());
    }

    public LiveData<String> getHtmlLiveData() {
        return html;
    }

    public LiveData<Throwable> getErrorLiveData() {
        return error;
    }

    public void loadHtml(Context context) {
        if (selectedRegistry.getValue() == null) {
            error.setValue(new IllegalStateException());
            return;
        }
        pdfRepository.getFetchedHtml(throwable -> {
            error.postValue(throwable);
            throwable.printStackTrace();
            return "";
        }, s -> html.postValue(s), context, selectedRegistry.getValue());
    }

    public void generatePdf(Context context, PDFPrint.OnPDFPrintListener pdfListener, PdfRepository.OnFileManageListener fileManageListener) {
        Registry registry = getRegistryLiveData().getValue();
        if (registry == null) {
            error.setValue(new IllegalStateException());
            return;
        }
        String fileName = registry.getCallInfo().getFullName() + "-" + registry.getCreateDate();
        try {
            pdfRepository.generatePdf(context, html.getValue(), fileName, pdfListener, fileManageListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCleared() {
        selectedRegistry = new MutableLiveData<>();
        error = new MutableLiveData<>();
        recommendation = new MutableLiveData<>();
        inspection = new MutableLiveData<>();
        html = new MutableLiveData<>();

        registryRepository.cancelFutures();
        pdfRepository.cancelFutures();
        super.onCleared();
    }
}
