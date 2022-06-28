package com.injent.miscalls.ui.callstuff;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.database.diagnosis.Diagnosis;
import com.injent.miscalls.data.database.medcall.Geo;
import com.injent.miscalls.data.database.medcall.MedCall;
import com.injent.miscalls.data.database.registry.Objectively;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.domain.repositories.CallRepository;
import com.injent.miscalls.domain.repositories.DiagnosisRepository;
import com.injent.miscalls.domain.repositories.PdfRepository;
import com.injent.miscalls.domain.repositories.RegistryRepository;
import com.injent.miscalls.ui.inspection.AdditionalField;
import com.injent.miscalls.ui.overview.Field;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class CallStuffViewModel extends ViewModel {

    private RegistryRepository registryRepository;
    private CallRepository callRepository;
    private DiagnosisRepository diagnosisRepository;
    private PdfRepository pdfRepository;

    private MutableLiveData<Integer> action = new MutableLiveData<>();
    private MutableLiveData<MedCall> selectedCall = new MutableLiveData<>();
    private MutableLiveData<Registry> currentRegistry;
    private MutableLiveData<Diagnosis> selectedDiagnosis;
    private MutableLiveData<Throwable> successOperation = new MutableLiveData<>();
    private LiveData<String> html;
    private MutableLiveData<List<AdditionalField>> additionalFields = new MutableLiveData<>();

    public void init() {
        registryRepository = new RegistryRepository();
        callRepository = new CallRepository();
        diagnosisRepository = new DiagnosisRepository();
        pdfRepository = new PdfRepository();
        html = pdfRepository.getHtml();
        selectedDiagnosis = new MutableLiveData<>();
        currentRegistry = new MutableLiveData<>();
    }

    public LiveData<Diagnosis> getSelectedDiagnosis() {
        return selectedDiagnosis;
    }

    public void setSelectedDiagnosis(Diagnosis diagnosis) {
        selectedDiagnosis.setValue(diagnosis);
    }

    public LiveData<String> getHtmlLiveData() {
        return html;
    }

    public void loadHtml(Context context) {
        pdfRepository.loadFetchedHtml(context, currentRegistry.getValue());
    }

    public LiveData<Throwable> getErrorLiveData() {
        return successOperation;
    }

    public LiveData<Registry> getCurrentRegistryLiveData() {
        return currentRegistry;
    }

    public void loadRegistry(Registry registry) {
        if (registry == null) {
            String createDate = new SimpleDateFormat("dd-MM-yyyy HH:mm",Locale.getDefault()).format(new Date());
            registry = new Registry();
            registry.setCallId(Objects.requireNonNull(selectedCall.getValue()).getId());
            registry.setCallInfo(selectedCall.getValue());
            registry.setCreateDate(createDate);
            Objectively objectively = new Objectively();
            registry.setObjectively(objectively);
        }

        this.currentRegistry.setValue(registry);
    }

    public void setCurrentDiagnoses(List<Diagnosis> list) {
        if (currentRegistry.getValue() == null) return;
        currentRegistry.getValue().setDiagnoses(list);
    }

    public List<Diagnosis> deleteItemFromList(List<Diagnosis> list, Diagnosis diagnosis) {
        if (currentRegistry.getValue() == null) return Collections.emptyList();
        List<Diagnosis> diagnosisList = new ArrayList<>(list);
        int idToDelete = 0;
        for (int i = 0; i < diagnosisList.size(); i++) {
            if (diagnosisList.get(i).equals(diagnosis)) {
                idToDelete = i;
                break;
            }
        }
        diagnosisList.remove(idToDelete);
        setCurrentDiagnoses(diagnosisList);
        return diagnosisList;
    }

    public void addItemToList(List<Diagnosis> list, Diagnosis diagnosis, Consumer<List<Diagnosis>> consumer) {
        List<Diagnosis> diagnoses = new ArrayList<>(list);
        for (Diagnosis d : list) {
            if (d.equals(diagnosis)) {
                consumer.accept(null);
                return;
            }
        }
        diagnoses.add(diagnosis);
        consumer.accept(diagnoses);
        Objects.requireNonNull(currentRegistry.getValue()).setDiagnoses(diagnoses);
        currentRegistry.getValue().setDiagnosesId(Diagnosis.listToStringIds(diagnoses, ';'));
    }

    public LiveData<MedCall> getCallLiveData() {
        return selectedCall;
    }

    public void loadCall(int callId) {
        callRepository.loadCallInfoById(throwable -> {
            successOperation.postValue(throwable);
            return null;
        }, callInfo -> {
            if (callInfo != null) {
                if (callInfo.isInspected()) {
                    loadExistRegistry(callInfo.getId());
                }
                selectedCall.postValue(callInfo);
            } else {
                successOperation.postValue(new UnknownError());
            }
        }, callId);
    }

    public void loadExistRegistry(int callId) {
        registryRepository.loadRegistryByCallId(throwable -> {
            successOperation.postValue(throwable);
            throwable.printStackTrace();
            return null;
        }, registry -> currentRegistry.postValue(registry), callId);
    }

    public void saveRegistry() {
        if (currentRegistry.getValue() == null || selectedCall.getValue() == null) return;
        List<Diagnosis> list = currentRegistry.getValue().getDiagnoses();
        if (list == null || list.isEmpty()) {
            successOperation.postValue(new NullPointerException());
            return;
        }

        String diagnosisString = Diagnosis.listToStringIds(list, ';');
        currentRegistry.getValue().setDiagnosesId(diagnosisString);

        registryRepository.insertRegistry(throwable -> {
            successOperation.postValue(throwable);
            return null;
        }, currentRegistry.getValue());

        selectedCall.getValue().setInspected(true);

        callRepository.updateCall(throwable -> {
            successOperation.postValue(throwable);
            throwable.printStackTrace();
            return null;
        }, selectedCall.getValue());

        successOperation.setValue(null);
    }

    public boolean isInspectionDone() {
        return false;
    }

    public void searchDiagnosis(String s, int limit) {
        diagnosisRepository.searchNotParentDiagnoses(s, limit);
    }

    public void setObjectivelyData(int index, String s) {
        if (currentRegistry.getValue() == null) {
            successOperation.postValue(new NullPointerException());
            return;
        }
        Objectively obj = currentRegistry.getValue().getObjectively();
        if (obj == null) {
            obj = new Objectively();
        }
        switch (index) {
            case Field.GENERAL_STATE: obj.setGeneralState(s);
            break;
            case Field.BODY_BUILD: obj.setBodyBuild(s);
            break;
            case Field.SKIN: obj.setSkin(s);
            break;
            case Field.NODES: obj.setNodes(s);
            break;
            case Field.PHARYNX: obj.setPharynx(s);
            break;
            case Field.BREATHING: obj.setBreathing(s);
            break;
            case Field.ARTERIAL_PRESSURE: obj.setArterialPressure(s);
            break;
            case Field.PULSE: obj.setPulse(s);
            break;
            case Field.PENSIONER: obj.setPensioner(Boolean.parseBoolean(s));
            break;
            case Field.SICK: obj.setSick(Boolean.parseBoolean(s));
            break;
            case Field.GLANDS: obj.setGlands(s);
            break;
            case Field.COMPLAINTS: currentRegistry.getValue().setComplaints(s);
            break;
            case Field.ANAMNESIS: currentRegistry.getValue().setAnamnesis(s);
            break;
            case Field.TEMPERATURE: currentRegistry.getValue().getObjectively().setTemperature(s);
            break;
            case Field.ABDOMEN: currentRegistry.getValue().getObjectively().setAbdomen(s);
            break;
            case Field.LIVER: currentRegistry.getValue().getObjectively().setLiver(s);
            break;
            case Field.SURVEYS: currentRegistry.getValue().setSurveys(s);
            break;
            case Field.MEDICAL_THERAPY: currentRegistry.getValue().setMedicalTherapy(s);
            break;
            default: throw new IllegalStateException("Invalid field index: " + index);
        }

        currentRegistry.getValue().setObjectively(obj);
    }

    public LiveData<List<AdditionalField>> getAdditionalFieldsLiveData() {
        return additionalFields;
    }

    public void loadFieldsList() {
        registryRepository.configureAdditionalFields(throwable -> {
            successOperation.postValue(throwable);
            return null;
        }, list -> additionalFields.postValue(list));
    }

    public Geo getGeo() {
        if (selectedCall.getValue() == null) {
            successOperation.setValue(new NullPointerException());
            return null;
        }
        return selectedCall.getValue().getGeo();
    }

    /**
     * @param actionCode code from {@link CallStuffFragment}.
     */
    public void runAction(int actionCode) {
       action.setValue(actionCode);
    }

    public LiveData<Integer> getActionLiveData() {
        return action;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        selectedCall = new MutableLiveData<>();
        successOperation = new MutableLiveData<>();
        currentRegistry = null;
        html = null;
        selectedDiagnosis = null;
        action = new MutableLiveData<>();
        additionalFields = null;

        diagnosisRepository.clear();
        callRepository.clear();
        registryRepository.clear();
    }
}