package com.injent.miscalls.domain.repositories;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.print.PDFPrint;
import android.util.Log;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.registry.Objectively;
import com.injent.miscalls.data.database.registry.Registry;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class PdfRepository {

    public interface OnFileManageListener {
        boolean onFileExists(File file);
    }

    private CompletableFuture<String> getFetchedHtmlFuture;

    public void cancelFutures() {
        if (getFetchedHtmlFuture != null) {
            getFetchedHtmlFuture.cancel(true);
        }
    }

    public void generatePdf(Context context, String html, String fileName, PDFPrint.OnPDFPrintListener pdfListener, OnFileManageListener fileListener) throws IOException {
        String filePath = App.getUserDataManager().getString(R.string.keyPdfFilePath);
        File file = new File(filePath
                + File.separator
                + fileName
                + ".pdf");
        boolean fileCreated = false;
        if (!file.exists()) {
            fileCreated = file.createNewFile();
        } else {
            if (fileListener.onFileExists(file)) {
                file.delete();
                fileCreated = file.createNewFile();
            }
        }

        if (fileCreated)
            PDFUtil.generatePDFFromHTML(context, file, html, pdfListener);
    }

    public void getFetchedHtml(Function<Throwable,String> ex, Consumer<String> consumer, Context context, Registry registry) {
        getFetchedHtmlFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        AssetManager assetManager = context.getAssets();
                        InputStream inputStream;
                        inputStream = assetManager.open("doc_temp.html");

                        StringBuilder sb = new StringBuilder();
                        Scanner scanner = new Scanner(inputStream);
                        while (scanner.hasNextLine()) {
                            sb.append(scanner.nextLine());
                        }
                        scanner.close();
                        inputStream.close();

                        Objectively obj = registry.getObjectively();

                        return sb.toString()
                                .replace("$organization", App.getUser().getOrganization().getName())
                                .replace("$patient_fullname", registry.getCallInfo().getFullName())
                                .replace("$doctor_fullname", App.getUser().getFullName())
                                .replace("$complaints", registry.getComplaints())
                                .replace("$recommendations", registry.getRecommendation())
                                .replace("$date_of_birth", registry.getCallInfo().getBornDate().toString())
                                .replace("$age", String.valueOf(registry.getCallInfo().getAge()))
                                .replace("$work_position", App.getUser().getWorkingPosition())
                                .replace("$address", registry.getCallInfo().getResidence())
                                .replace("$visit_date", registry.getCreateDate())
                                .replace("$state", obj.getGeneralState())
                                .replace("$bodybuild", obj.getBodyBuild())
                                .replace("$skin", obj.getSkin())
                                .replace("$nodes_gland", obj.getNodeAndGland())
                                .replace("$temperature", obj.getTemperature())
                                .replace("$pharynx", obj.getPharynx())
                                .replace("$respriratory_rate", obj.getRespiratoryRate())
                                .replace("$breathing", obj.getBreathing())
                                .replace("$arterial_pressure", obj.getArterialPressure())
                                .replace("$pulse", obj.getPulse())
                                .replace("$abdomen", obj.getAbdomen())
                                .replace("$liver", obj.getLiver())
                                .replace("$diagnosis", Diagnosis.listToStringNames(registry.getDiagnoses(), "<br>"))
                                .replace("$surveys", registry.getSurveys());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return "";
                })
                .exceptionally(ex);
        getFetchedHtmlFuture.thenAcceptAsync(consumer);
    }
}
