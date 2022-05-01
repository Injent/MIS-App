package com.injent.miscalls.ui.patientcard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.domain.repositories.HomeRepository;

public class PatientCardViewModel extends ViewModel {

    private final HomeRepository repository;

    public PatientCardViewModel() {
        repository = new HomeRepository();
    }

    private final MutableLiveData<Patient> selectedPatient = new MutableLiveData<>();

    public LiveData<Patient> getPatientLiveData() {
        return selectedPatient;
    }

    public Patient getPatient(int patientId) {
        if (selectedPatient.getValue() == null) {
            selectedPatient.setValue(repository.getPatientById(patientId));
        }
        return selectedPatient.getValue();
    }
}
