package com.injent.miscalls.ui.patientcard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.domain.repositories.HomeRepository;

import java.util.function.Consumer;
import java.util.function.Function;

public class PatientCardViewModel extends ViewModel {

    private final HomeRepository repository;

    public PatientCardViewModel() {
        repository = new HomeRepository();
    }

    private final MutableLiveData<Patient> selectedPatient = new MutableLiveData<>();
    private final MutableLiveData<Throwable> patientError = new MutableLiveData<>();

    public LiveData<Patient> getPatientLiveData() {
        return selectedPatient;
    }

    public void loadPatient(int patientId) {
        repository.getPatientById(throwable -> {
            patientError.postValue(throwable);
            return null;
        }, patient -> {
            if (patient == null) {
                patientError.postValue(new UnknownError());
            } else
                selectedPatient.postValue(patient);
        }, patientId);
    }
}
