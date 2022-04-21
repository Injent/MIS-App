package com.injent.miscalls.domain;

import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.templates.ProtocolTemp;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProtocolTempFletcher {

    public static final String[] patientInfo = new String[]{
            "#имя",
            "#фамилия",
            "#отчество"
    };

    public ProtocolTemp fletch(ProtocolTemp temp, Patient patient) {
        String treatment = temp.getTreatment();
        String conclusion = temp.getConclusion();
        String inspection = temp.getInspection();
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<ProtocolTemp> tempFuture = es.submit(new Callable<ProtocolTemp>() {
            @Override
            public ProtocolTemp call() {
                conclusion.replaceAll(patientInfo[0], patient.getFirstname());
                conclusion.replaceAll(patientInfo[1], patient.getLastname());
                conclusion.replaceAll(patientInfo[2], patient.getMiddleName());

                treatment.replaceAll(patientInfo[0], patient.getFirstname());
                treatment.replaceAll(patientInfo[1], patient.getLastname());
                treatment.replaceAll(patientInfo[2], patient.getMiddleName());

                inspection.replaceAll(patientInfo[0], patient.getFirstname());
                inspection.replaceAll(patientInfo[1], patient.getLastname());
                inspection.replaceAll(patientInfo[2], patient.getMiddleName());

                ProtocolTemp protocolTemp = new ProtocolTemp();
                protocolTemp.setTreatment(treatment);
                protocolTemp.setConclusion(conclusion);
                protocolTemp.setInspection(inspection);

                return protocolTemp;
            }
        });

        if (tempFuture.isDone()) {
            try {
                return tempFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return temp;
    }
}
