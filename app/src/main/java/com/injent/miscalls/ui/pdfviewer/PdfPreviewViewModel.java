package com.injent.miscalls.ui.pdfviewer;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.domain.repositories.PdfRepository;

import java.io.File;
import java.io.IOException;

public class PdfPreviewViewModel extends ViewModel {

    private final PdfRepository repository;

    private MutableLiveData<Bitmap> bitmapMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Throwable> error = new MutableLiveData<>();

    public PdfPreviewViewModel() {
        repository = new PdfRepository();
    }

    public LiveData<Bitmap> getBitmapLiveData() {
        return bitmapMutableLiveData;
    }

    public LiveData<Throwable> getErrorLiveData() {
        return error;
    }

    public void loadBitmap(String path, int page) {
        repository.createBitmap(throwable -> {
            error.postValue(throwable);
            return null;
        }, bitmap -> bitmapMutableLiveData.postValue(bitmap), path, page);
    }

    public void saveFile(File file, String fileName) {
        try {
            repository.copy(file, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCleared() {
        bitmapMutableLiveData = new MutableLiveData<>();
        error = new MutableLiveData<>();

        repository.cancelFutures();
        super.onCleared();
    }
}
