package com.injent.miscalls.ui.editor;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.domain.repositories.PdfRepository;
import com.injent.miscalls.domain.repositories.RegistryRepository;
import com.injent.miscalls.ui.pdfviewer.PdfBundle;

import java.util.List;

public class EditorViewModel extends ViewModel {

    private final RegistryRepository registryRepository;
    private final PdfRepository pdfRepository;

    private MutableLiveData<Registry> selectedRegistry = new MutableLiveData<>();
    private MutableLiveData<Throwable> error = new MutableLiveData<>();
    private MutableLiveData<PdfBundle> generatedPdfItems = new MutableLiveData<>();
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
            return null;
        }, registry -> selectedRegistry.postValue(registry), id);
    }

    public void setInspection(String s) {
        inspection.setValue(s);
    }

    public void setRecommendation(String s) {
        recommendation.setValue(s);
    }

    public LiveData<PdfBundle> getGeneratedPdfItemsLiveData() {
        return generatedPdfItems;
    }

    public void saveChanges() {
        Registry registry = selectedRegistry.getValue();
        if (registry == null) {
            throw new IllegalStateException();
        }

        registry.setInspection(inspection.getValue());
        registry.setRecommendation(recommendation.getValue());
        //String ids = Diagnosis.listToStringIds(list,';');
        //registry.setDiagnosesId(ids);

        registryRepository.insertRegistry(throwable -> {
            error.postValue(throwable);
            return null;
        }, registry);
    }

    public void generatePdf(Context context) {
        pdfRepository.generatePdf(context, selectedRegistry.getValue(), throwable -> {
            error.postValue(throwable);
            return null;
        }, pdfBundle -> {
            generatedPdfItems.postValue(pdfBundle);
        });
    }

    @Override
    protected void onCleared() {
        selectedRegistry = new MutableLiveData<>();
        error = new MutableLiveData<>();
        generatedPdfItems = new MutableLiveData<>();
        recommendation = new MutableLiveData<>();
        inspection = new MutableLiveData<>();

        registryRepository.cancelFutures();
        super.onCleared();
    }
}
