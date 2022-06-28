package com.injent.miscalls.domain.repositories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.print.PDFPrint;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.database.diagnosis.Diagnosis;
import com.injent.miscalls.data.database.registry.Objectively;
import com.injent.miscalls.data.database.registry.Registry;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    public void generatePdf(Context context, String html, String fileName, PDFPrint.OnPDFPrintListener pdfListener, FileManageListener fileListener) throws IOException {
        File file = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            final String appFolder = Environment.DIRECTORY_DOCUMENTS + "/" + context.getString(R.string.app_name) + "/";
//            file = FileManager.getInstance().createTempFile(context, "pdf", false);
//
//            Uri contentUri = MediaStore.Files.getContentUri("external");
//            String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?";
//            String[] selectionArgs = new String[]{appFolder};
//
//            Cursor cursor = context.getContentResolver().query(contentUri, null, selection, selectionArgs, null);
//
//            Uri uri = null;
//
//            if (cursor.getCount() == 0) {
//                Toast.makeText(context, "No file found in \"" + appFolder + "\"", Toast.LENGTH_LONG).show();
//            } else {
//                while (cursor.moveToNext()) {
//                    fileName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
//
//                    if (fileName.equals(fileName + ".pdf")) {
//                        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
//                        uri = ContentUris.withAppendedId(contentUri, id);
//                        break;
//                    }
//                }
//
//                context.getContentResolver().
//
//                if (uri == null) {
//                    Toast.makeText(context, "\"" + fileName + ".pdf\" not found", Toast.LENGTH_SHORT).show();
//                } else {
//                    try {
//                        OutputStream outputStream = context.getContentResolver().openOutputStream(uri, "rwt");
//                        outputStream.write("This is overwritten data".getBytes());
//                        outputStream.close();
//
//                        Toast.makeText(context, "File written successfully", Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        Toast.makeText(context, "Fail to write file", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }

//            ContentValues values = new ContentValues();
//
//            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
//            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
//            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/" + context.getString(R.string.app_name) + "/");
//
//            Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
//
//            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
//            outputStream.write("".getBytes());
//            outputStream.close();

            Toast.makeText(context, "OPERATION DONE",Toast.LENGTH_LONG).show();
        } else {
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
            file = new File(filePath
                    + File.separator
                    + fileName
                    + ".pdf");
            boolean fileCreated = false;
            if (!file.exists()) {
                 file.createNewFile();

                if (fileListener.onFileExists(file)) {
                    file.delete();
                    file.createNewFile();
                }
            }
        }
        PDFUtil.generatePDFFromHTML(context, file, html, pdfListener);
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

    public interface FileManageListener {
        boolean onFileExists(File file);
    }
}
