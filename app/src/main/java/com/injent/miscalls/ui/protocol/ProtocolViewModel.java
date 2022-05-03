package com.injent.miscalls.ui.protocol;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.savedprotocols.Protocol;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.domain.ProtocolFletcher;
import com.injent.miscalls.domain.repositories.HomeRepository;
import com.injent.miscalls.domain.repositories.ProtocolRepository;
import com.injent.miscalls.domain.repositories.ProtocolTempRepository;

import java.util.function.Consumer;
import java.util.function.Function;

public class ProtocolViewModel extends ViewModel {

    private final ProtocolTempRepository repository;
    private final ProtocolRepository protocolRepository;
    private final HomeRepository homeRepository;

    private final MutableLiveData<Patient> selectedPatient = new MutableLiveData<>();
    private final MutableLiveData<ProtocolTemp> selectedProtocol = new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();

    public ProtocolViewModel() {
        this.protocolRepository = new ProtocolRepository();
        this.repository = new ProtocolTempRepository();
        this.homeRepository = new HomeRepository();
    }

    public void loadProtocolTemp(int protocolTempId, Patient patient) {
        repository.getProtocolTempById(throwable -> {
            error.postValue(new UnknownError());
            return null;
        }, protocolTemp -> {
            if (protocolTemp == null) {
                error.postValue(new UnknownError());
            } else {
                selectedProtocol.postValue(new ProtocolFletcher(patient).fletchProtocol(protocolTemp));
            }
        }, protocolTempId);
    }

    public void loadPatient(int patientId) {
        homeRepository.getPatientById(throwable -> {
            error.postValue(new UnknownError());
            return null;
        }, patient -> {
            if (patient == null) {
                error.postValue(new UnknownError());
            } else
                selectedPatient.postValue(patient);
        }, patientId);
    }

    public LiveData<ProtocolTemp> getProtocolLiveData() {
        return selectedProtocol;
    }

    public LiveData<Patient> getPatientLiveData() {
        return selectedPatient;
    }

    public void saveProtocol(Protocol protocol, Patient patient) {
        protocol.setPatientId(patient.getId());
        boolean duplication = protocolRepository.checkDuplication(protocol, patient.getId());
        if (duplication) {
            protocol.setId(patient.getId());
        }
        patient.setInspected(true);
        homeRepository.insertPatient(patient);
        protocolRepository.insertProtocol(protocol);
    }
}
