package com.injent.miscalls.network.rest.dto;

import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.registry.Objectively;
import com.injent.miscalls.data.database.registry.Registry;

import java.util.List;

public class RegistryDto {

    private Objectively objectively;
    private CallDto callInfo;
    private List<Diagnosis> diagnoses;
    private String complaints;
    private String anamnesis;
    private String recommendation;
    private String createDate;
    private String surveys;
    private int callId;
    private int userId;

    public RegistryDto(
            Objectively objectively,
            List<Diagnosis> diagnoses,
            String complaints,
            String anamnesis,
            String recommendation,
            String createDate,
            String surveys,
            int callId,
            int userId
    ) {
        this.objectively = objectively;
        this.diagnoses = diagnoses;
        this.complaints = complaints;
        this.anamnesis = anamnesis;
        this.recommendation = recommendation;
        this.createDate = createDate;
        this.surveys = surveys;
        this.callId = callId;
        this.userId = userId;
    }

    public Objectively getObjectively() {
        return objectively;
    }

    public void setObjectively(Objectively objectively) {
        this.objectively = objectively;
    }

    public CallDto getCallInfo() {
        return callInfo;
    }

    public void setCallInfo(CallDto callInfo) {
        this.callInfo = callInfo;
    }

    public List<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(List<Diagnosis> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public String getComplaints() {
        return complaints;
    }

    public void setComplaints(String complaints) {
        this.complaints = complaints;
    }

    public String getAnamnesis() {
        return anamnesis;
    }

    public void setAnamnesis(String anamnesis) {
        this.anamnesis = anamnesis;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getSurveys() {
        return surveys;
    }

    public void setSurveys(String surveys) {
        this.surveys = surveys;
    }

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static RegistryDto toDto(Registry registry) {
        return new RegistryDto(
                registry.getObjectively(),
                registry.getDiagnoses(),
                registry.getComplaints(),
                registry.getAnamnesis(),
                registry.getRecommendation(),
                registry.getCreateDate(),
                registry.getSurveys(),
                registry.getCallId(),
                registry.getUserId()
        );
    }
}
