package com.injent.miscalls.ui.callstuff;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.registry.Objectively;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.domain.repositories.CallRepository;
import com.injent.miscalls.domain.repositories.DiagnosisRepository;
import com.injent.miscalls.domain.repositories.PdfRepository;
import com.injent.miscalls.domain.repositories.RegistryRepository;
import com.injent.miscalls.ui.adapters.Field;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class CallStuffViewModel extends ViewModel {

    private final RegistryRepository registryRepository;
    private final CallRepository homeRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final PdfRepository pdfRepository;

    private MutableLiveData<CallInfo> selectedCall = new MutableLiveData<>();
    private MutableLiveData<Registry> currentRegistry = new MutableLiveData<>();
    private MutableLiveData<Throwable> successOperation = new MutableLiveData<>();
    private MutableLiveData<String> html = new MutableLiveData<>();
    private MutableLiveData<List<Diagnosis>> searchDiagnoses = new MutableLiveData<>();

    public CallStuffViewModel() {
        registryRepository = new RegistryRepository();
        homeRepository = new CallRepository();
        diagnosisRepository = new DiagnosisRepository();
        pdfRepository = new PdfRepository();
    }

    public LiveData<String> getHtmlLiveData() {
        return html;
    }

    public LiveData<List<Diagnosis>> getSearchDiagnoses() {
        return searchDiagnoses;
    }

    public void loadHtml(Context context) {
        pdfRepository.getFetchedHtml(throwable -> {
            throwable.printStackTrace();
            return null;
                },
                s -> html.postValue(s),
                context,
                currentRegistry.getValue());
    }

    public LiveData<Throwable> getErrorLiveData() {
        return successOperation;
    }

    public LiveData<Registry> getCurrentRegistryLiveData() {
        return currentRegistry;
    }

    public void loadRegistry(Registry registry) {
        if (registry == null) {
            String createDate = new SimpleDateFormat("dd-MM-yyyy\nhh:mm",Locale.getDefault()).format(new Date(Instant.now().toEpochMilli()));
            registry = new Registry();
            registry.setCallId(selectedCall.getValue().getId());
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
        currentRegistry.getValue().setDiagnoses(diagnoses);
        currentRegistry.getValue().setDiagnosesId(Diagnosis.listToStringIds(diagnoses, ';'));
    }

    public LiveData<CallInfo> getCallLiveData() {
        return selectedCall;
    }

    public void loadCall(int callId) {
        homeRepository.loadCallInfoById(throwable -> {
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
        if (currentRegistry.getValue() == null || selectedCall == null) return;
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

        homeRepository.updateCall(throwable -> {
            successOperation.postValue(throwable);
            return null;
        }, selectedCall.getValue());

        successOperation.setValue(null);
    }

    public boolean isInspectionDone() {
        return false;
    }

    public void searchDiagnosis(String s) {
        diagnosisRepository.searchDiagnoses(s, list -> searchDiagnoses.postValue(list));
    }

    public void setObjectivelyData(int index, String s) {
        if (currentRegistry.getValue() == null) return;
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
            default: throw new IllegalStateException();
        }

        currentRegistry.getValue().setObjectively(obj);
    }

    @Override
    protected void onCleared() {
        selectedCall = new MutableLiveData<>();
        successOperation = new MutableLiveData<>();
        currentRegistry = new MutableLiveData<>();
        html = new MutableLiveData<>();
        searchDiagnoses = new MutableLiveData<>();

        diagnosisRepository.cancelFutures();
        homeRepository.cancelFutures();
        registryRepository.cancelFutures();
        super.onCleared();
    }
}
