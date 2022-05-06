package com.injent.miscalls.domain;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.print.PDFPrint;

import com.injent.miscalls.data.User;
import com.injent.miscalls.data.calllist.CallInfo;
import com.injent.miscalls.data.savedprotocols.Inspection;
import com.injent.miscalls.data.diagnosis.RecommendationTemp;
import com.tejpratapsingh.pdfcreator.activity.PDFViewerActivity;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ProtocolFletcher {

    public static final String FILE_NAME = "pdf_template.html";

    public ProtocolFletcher(Context context, CallInfo callInfo, User user) {
        this.context = context;
        this.callInfo = callInfo;
        this.user = user;
    }

    public ProtocolFletcher(CallInfo callInfo) {
        this.callInfo = callInfo;
    }

    Context context;
    CallInfo callInfo;
    User user;

    private final String[] key = new String[]{
            "@имя",
            "@фамилия",
            "@отчество",
            "@датарождения",
            "@категория",
            "@адрес"
    };
    
    public RecommendationTemp fletchRecommendation(RecommendationTemp temp) {
        String content = temp.getContent()
                .replace(key[0], callInfo.getFirstname())
                .replace(key[1], callInfo.getLastname())
                .replace(key[2], callInfo.getMiddleName())
                .replace(key[3], callInfo.getBornDate())
                .replace(key[4], callInfo.getBenefitCategoryCode()
                .replace(key[5], callInfo.getResidence())
                );

        RecommendationTemp recommendationTemp = new RecommendationTemp();
        recommendationTemp.setId(temp.getId());
        recommendationTemp.setDescription(temp.getDescription());
        recommendationTemp.setName(temp.getName());
        recommendationTemp.setContent(content);

        return recommendationTemp;
    }

    public void fletchPdfFile(Inspection inspection) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(FILE_NAME);
        Scanner scanner = new Scanner(inputStream);
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNextLine()){
            stringBuilder.append(scanner.nextLine());
        }
        scanner.close();

        String html = stringBuilder.toString()
                .replace("$docfullname", user.getFullName())
                .replace("$workingposition", user.getWorkingPosition())
                .replace("$patientfullname", callInfo.getFullName())
                .replace("$inspection", inspection.getInspection())
                .replace("$treatment", inspection.getTreatment()
                .replace("$conclusion", inspection.getConclusion()));

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
