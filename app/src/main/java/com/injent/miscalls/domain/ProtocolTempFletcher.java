package com.injent.miscalls.domain;

import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.templates.ProtocolTemp;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProtocolTempFletcher {

    private final String[] key = new String[]{
            "@имя",
            "@фамилия",
            "@отчество",
            "@датарождения",
            "@категория",
            "@адрес"
    };
    
    public ProtocolTemp fletch(ProtocolTemp temp, Patient patient) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Callable<ProtocolTemp> callable = new Callable<ProtocolTemp>() {
            @Override
            public ProtocolTemp call() {

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
            }
        };
        Future<ProtocolTemp> tempFuture = es.submit(callable);

        try {
            return tempFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public void fletchPdfFile() {

    }
}
