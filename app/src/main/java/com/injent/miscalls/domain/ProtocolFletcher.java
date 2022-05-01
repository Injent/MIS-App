package com.injent.miscalls.domain;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.print.PDFPrint;

import com.injent.miscalls.data.User;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.savedprotocols.Protocol;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.tejpratapsingh.pdfcreator.activity.PDFViewerActivity;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProtocolFletcher {

    private ExecutorService es;

    private final String[] key = new String[]{
            "@имя",
            "@фамилия",
            "@отчество",
            "@датарождения",
            "@категория",
            "@адрес"
    };
    
    public ProtocolTemp fletchProtocol(ProtocolTemp temp, Patient patient) {
        es = Executors.newSingleThreadExecutor();
        Callable<ProtocolTemp> callable = () -> {

            String[] values = new String[]{temp.getInspection(), temp.getTreatment(), temp.getConclusion()};
            for (int i = 0; i < values.length; i++) {
                String replace = values[i]
                        .replace(key[0], patient.getFirstname())
                        .replace(key[1], patient.getLastname())
                        .replace(key[2], patient.getMiddleName())
                        .replace(key[3], patient.getBornDate())
                        .replace(key[4], patient.getBenefitCategoryCode()
                        .replace(key[5], patient.getRegAddress())
                        );
                values[i] = replace;
            }

            ProtocolTemp protocolTemp = new ProtocolTemp();
            protocolTemp.setId(temp.getId());
            protocolTemp.setDescription(temp.getDescription());
            protocolTemp.setName(temp.getName());
            protocolTemp.setInspection(values[0]);
            protocolTemp.setTreatment(values[1]);
            protocolTemp.setConclusion(values[2]);

            return protocolTemp;
        };
        Future<ProtocolTemp> tempFuture = es.submit(callable);

        try {
            return tempFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return temp;
    }

    public void fletchPdfFile(Context context, Protocol protocol, User user, Patient patient) {
        String html = getProtocolTemp()
                .replace("$docfullname", user.getFullName())
                .replace("$patientfullname", patient.getFullName())
                .replace("$inspection",protocol.getInspection())
                .replace("$treatment",protocol.getTreatment()
                .replace("$conclusion",protocol.getConclusion()));

        final File savedPdfFile = FileManager.getInstance().createTempFile(context,"pdf",false);

        PDFUtil.generatePDFFromHTML(context, savedPdfFile, html, new PDFPrint.OnPDFPrintListener() {
            @Override
            public void onSuccess(File file) {
                Uri pdfUri = Uri.fromFile(savedPdfFile);

                Intent intent = new Intent(context, PDFViewerActivity.class);
                intent.putExtra(PDFViewerActivity.PDF_FILE_URI, pdfUri);

                context.startActivity(intent);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public String getProtocolTemp() {
        return " <!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h1>РУССКИЙ</h1>\n" +
                "<p>My first paragraph.</p>\n" +
                " <a href='https://www.example.com'>This is a link</a>" +
                "\n" +
                "</body>\n" +
                "</html> ";
    }
}
