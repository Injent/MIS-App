package com.injent.miscalls.domain.repositories;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.print.PDFPrint;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.database.diagnosis.Diagnosis;
import com.injent.miscalls.data.database.registry.Objectively;
import com.injent.miscalls.data.database.registry.Registry;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class PdfRepository {

    private MutableLiveData<String> html;
    private MutableLiveData<Throwable> error;

    private CompletableFuture<String> getFetchedHtmlFuture;

    public PdfRepository() {
        html = new MutableLiveData<>();
        error = new MutableLiveData<>();
    }

    public void clear() {
        if (getFetchedHtmlFuture != null) {
            getFetchedHtmlFuture.cancel(true);
        }
        html = null;
        error = null;
    }

    public LiveData<String> getHtml() {
        return html;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    @SuppressLint("Range")
    public void generatePdf(Context context, String html, String fileName) throws IOException {
        final String docFolder = Environment.DIRECTORY_DOCUMENTS + "/" + context.getString(R.string.app_name) + "/";
        final File file;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Create temp file in app folder
            file = FileManager.getInstance().createTempFile(context, "pdf", false);
        } else {
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
            file = new File(filePath
                    + File.separator
                    + fileName
                    + ".pdf");
            if (!file.exists()) {
                 file.createNewFile();
            }
        }
        PDFUtil.generatePDFFromHTML(context, file, html, new PDFPrint.OnPDFPrintListener() {
            @Override
            public void onSuccess(File file) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Toast.makeText(context, context.getString(R.string.fileSaveByPath) + " " + docFolder, Toast.LENGTH_LONG).show();
                    OutputStream outputStream = null;
                    try {
                        // Read bytes from app folder temp file
                        byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
                        ContentValues values = new ContentValues();

                        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                        values.put(MediaStore.MediaColumns.RELATIVE_PATH, docFolder);

                        Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);

                        outputStream = context.getContentResolver().openOutputStream(uri);
                        outputStream.write(bytes);
                        outputStream.close();

                    } catch (IOException e) {
                        Toast.makeText(context, R.string.pdfGenerationFailed, Toast.LENGTH_SHORT).show();
                        error.setValue(e);
                        e.printStackTrace();
                    } finally {
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Exception exception) {
                error.postValue(exception);
                exception.printStackTrace();
            }
        });
    }

    public void loadFetchedHtml(Context context, Registry registry) {
        getFetchedHtmlFuture = CompletableFuture
                .supplyAsync(() -> {
                    StringBuilder sb = new StringBuilder();
                    try {
                        AssetManager assetManager = context.getAssets();
                        InputStream inputStream;
                        inputStream = assetManager.open("doc_temp.html");

                        Scanner scanner = new Scanner(inputStream);
                        while (scanner.hasNextLine()) {
                            sb.append(scanner.nextLine());
                        }

                        inputStream.close();
                        scanner.close();

                        if (registry == null) {
                            return "";
                        }

                        Objectively obj = registry.getObjectively();

                        if (obj == null) {
                            obj = new Objectively();
                        }

                        return sb.toString()
                                .replace("$medication_therapy", registry.getRecommendation())
                                .replace("$anamnesis", registry.getAnamnesis())
                                .replace("$organization", App.getUser().getOrganization().getName())
                                .replace("$patient_fullname", registry.getCallInfo().getFullName())
                                .replace("$doctor_fullname", App.getUser().getFullName())
                                .replace("$complaints", registry.getComplaints())
                                .replace("$recommendations", registry.getRecommendation())
                                .replace("$date_of_birth", registry.getCallInfo().getBornDateString())
                                .replace("$age", String.valueOf(registry.getCallInfo().getAge()))
                                .replace("$work_position", App.getUser().getWorkingPosition())
                                .replace("$address", registry.getCallInfo().getResidence())
                                .replace("$visit_date", registry.getCreateDate())
                                .replace("$state", obj.getGeneralState())
                                .replace("$bodybuild", obj.getBodyBuild())
                                .replace("$skin", obj.getSkin())
                                .replace("$nodes_gland", obj.getNodes())
                                .replace("$temperature", obj.getTemperature())
                                .replace("$pharynx", obj.getPharynx())
                                .replace("$respiratory_rate", obj.getRespiratoryRate())
                                .replace("$breathing", obj.getBreathing())
                                .replace("$arterial_pressure", obj.getArterialPressure())
                                .replace("$pulse", obj.getPulse())
                                .replace("$abdomen", obj.getAbdomen())
                                .replace("$liver", obj.getLiver())
                                .replace("$diagnosis_code", Diagnosis.listToStringCodes(registry.getDiagnoses(), ','))
                                .replace("$diagnosis", Diagnosis.listToStringNames(registry.getDiagnoses(), "<br>"))
                                .replace("$surveys", registry.getSurveys())
                                .replace("$working", obj.isWorking())
                                .replace("$sick", obj.isSick());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return "";
                })
                .exceptionally(throwable -> {
                    error.postValue(throwable);
                    throwable.printStackTrace();
                    return null;
                });
        getFetchedHtmlFuture.thenAcceptAsync(s -> html.postValue(s));
    }
}
