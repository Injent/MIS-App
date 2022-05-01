package com.injent.miscalls.domain;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProtocolFletcher {

    public static final String fileName = "pdf_template.html";

    public ProtocolFletcher(Context context, Patient patient, User user) {
        this.context = context;
        this.patient = patient;
        this.user = user;
    }

    public ProtocolFletcher(Patient patient) {
        this.patient = patient;
    }

    Context context;
    Patient patient;
    User user;

    private final String[] key = new String[]{
            "@имя",
            "@фамилия",
            "@отчество",
            "@датарождения",
            "@категория",
            "@адрес"
    };
    
    public ProtocolTemp fletchProtocol(ProtocolTemp temp) {
        ExecutorService es = Executors.newSingleThreadExecutor();
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

    public void fletchPdfFile(Protocol protocol) throws IOException {

        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(fileName);
        Scanner scanner = new Scanner(inputStream);
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNextLine()){
            stringBuilder.append(scanner.nextLine());
        }

        String html = stringBuilder.toString()
                .replace("$docfullname", user.getFullName())
                .replace("$workingposition", user.getWorkingPosition())
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
}
