package com.injent.miscalls.ui.overview;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.print.PDFPrint;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.domain.repositories.PdfRepository;
import com.injent.miscalls.domain.repositories.RegistryRepository;
import com.injent.miscalls.network.JResponse;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverviewViewModel extends ViewModel {

    private final RegistryRepository registryRepository;
    private final PdfRepository pdfRepository;

    private MutableLiveData<Throwable> error = new MutableLiveData<>();
    private MutableLiveData<Registry> selectedRegistry = new MutableLiveData<>();
    private MutableLiveData<String> html = new MutableLiveData<>();
    private MutableLiveData<Boolean> clickPreviewPdf = new MutableLiveData<>();

    public OverviewViewModel() {
        this.registryRepository = new RegistryRepository();
        this.pdfRepository = new PdfRepository();
    }

    public LiveData<Throwable> getErrorLiveData() {
        return error;
    }

    public LiveData<Registry> getSelectedRegistryLiveData() {
        return selectedRegistry;
    }

    public void loadSelectedRegistry(int id) {
        registryRepository.loadRegistryById(throwable -> {
            error.postValue(throwable);
            throwable.printStackTrace();
            return null;
        }, registry -> selectedRegistry.postValue(registry), id);
    }

    public LiveData<String> getHtmlLiveData() {
        return html;
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
        Registry registry = getSelectedRegistryLiveData().getValue();
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

    public void setClickPreviewPdf(boolean click) {
        this.clickPreviewPdf.postValue(click);
    }

    public LiveData<Boolean> getClickPreviewPdf() {
        return clickPreviewPdf;
    }

    public void sendDocument(Supplier<String> supplier) {
        Call<JResponse> call = registryRepository.sendDocument(selectedRegistry.getValue());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<JResponse> call, @NonNull Response<JResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccessful()) {
                        supplier.get();
                    } else {
                        error.postValue(new NetworkErrorException());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JResponse> call, @NonNull Throwable t) {
                error.postValue(t);
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        selectedRegistry = new MutableLiveData<>();
        error = new MutableLiveData<>();
        html = new MutableLiveData<>();
        clickPreviewPdf = new MutableLiveData<>();

        pdfRepository.cancelFutures();
        registryRepository.cancelFutures();
    }
}
