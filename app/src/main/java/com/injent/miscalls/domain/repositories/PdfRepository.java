package com.injent.miscalls.domain.repositories;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PDFPrint;

import com.injent.miscalls.App;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.ui.pdfviewer.PdfBundle;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PdfRepository {

    public void cancelFutures() {
        if (bitmapFuture != null) {
            bitmapFuture.cancel(true);
        }
        if (generatePdfFuture != null) {
            generatePdfFuture.cancel(true);
        }
    }

    private CompletableFuture<Bitmap> bitmapFuture;
    private CompletableFuture<PdfBundle> generatePdfFuture;

    public void createBitmap(Function<Throwable, Bitmap> ex, Consumer<Bitmap> consumer, String path, int page) {
        bitmapFuture = CompletableFuture
                .supplyAsync((Supplier<Bitmap>) () -> {
                    ParcelFileDescriptor fileDescriptor;
                    PdfRenderer pdfRenderer;
                    PdfRenderer.Page pdfPage;
                    Bitmap bitmap = null;
                    try {
                        File file = new File(path);

                        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

                        pdfRenderer = new PdfRenderer(fileDescriptor);
                        pdfPage = pdfRenderer.openPage(page);

                        bitmap = Bitmap.createBitmap(pdfPage.getWidth(), pdfPage.getHeight(), Bitmap.Config.ARGB_8888);
                        pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                        fileDescriptor.close();
                        pdfPage.close();
                        pdfRenderer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return bitmap;
                })
                .exceptionally(ex);
        bitmapFuture.thenAcceptAsync(consumer);
    }

    public void generatePdf(Context context, Registry registry, Function<Throwable, PdfBundle> ex, Consumer<PdfBundle> consumer) {
        generatePdfFuture = CompletableFuture
                .supplyAsync((Supplier<PdfBundle>) () -> {
                    File file;
                    String html;
                    file = FileManager.getInstance().createTempFile(context,"pdf", false);

                    html = fletchHtml(context, registry);

                    return new PdfBundle(html, file.getAbsolutePath());
                })
                .exceptionally(ex);
        generatePdfFuture.thenAcceptAsync(consumer);
    }

    public void copy(File tempFile, String fileName) throws IOException {
        try (InputStream in = new FileInputStream(tempFile)) {
            File newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + fileName + ".pdf");
            try (OutputStream out = new FileOutputStream(newFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
        }
    }

    public String fletchHtml(Context context, Registry registry) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream;
            inputStream = assetManager.open("pdf_template.html");

            StringBuilder sb = new StringBuilder();
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            scanner.close();

            return sb.toString()
                    .replace("$patientname", registry.getCallInfo().getFullName())
                    .replace("$docname", App.getUser().getFullName())
                    .replace("$complaints", registry.getCallInfo().getComplaints())
                    .replace("$recommendations", registry.getRecommendation())
                    .replace("$date", registry.getCreateDate())
                    .replace("$diagnoses", Diagnosis.listToStringNames(registry.getDiagnoses(), "\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
