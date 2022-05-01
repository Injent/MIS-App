package com.injent.miscalls.ui.protocoltemp;

import android.accounts.NetworkErrorException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.api.HttpManager;
import com.injent.miscalls.data.patientlist.FailedDownloadDb;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.patientlist.QueryToken;
import com.injent.miscalls.data.templates.ProtocolTempDatabase;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.domain.ProtocolFletcher;
import com.injent.miscalls.domain.repositories.ProtocolTempRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProtocolTempViewModel extends ViewModel {

    private final ProtocolTempRepository repository;
    private final MutableLiveData<List<ProtocolTemp>> protocols = new MutableLiveData<>();
    private final MutableLiveData<Throwable> protocolError = new MutableLiveData<>();
    private final MutableLiveData<ProtocolTemp> editingProtocol = new MutableLiveData<>();
    private final MutableLiveData<ProtocolTemp> appliedProtocol = new MutableLiveData<>();

    public ProtocolTempViewModel() {
        repository = new ProtocolTempRepository();
    }

    public LiveData<ProtocolTemp> getSelectedProtocolLiveData() {
        return editingProtocol;
    }

    public ProtocolTemp getSelectedProtocol(int id) {
        if (editingProtocol.getValue() == null)
            editingProtocol.setValue(repository.getProtocolTempById(id));
        return editingProtocol.getValue();
    }

    public void saveSelectedProtocol(ProtocolTemp protocolTemp) {
        repository.insertProtocol(protocolTemp);
    }

    public void applyProtocolTemp(ProtocolTemp protocolTemp, Patient patient) {
        appliedProtocol.setValue(new ProtocolFletcher().fletchProtocol(protocolTemp, patient));
    }

    public LiveData<ProtocolTemp> getAppliedProtocolLiveData() {
        return appliedProtocol;
    }

    public LiveData<List<ProtocolTemp>> getProtocolsLiveDate() {
        return protocols;
    }

    public LiveData<Throwable> getProtocolErrorLiveData() {
        return protocolError;
    }

    public List<ProtocolTemp> getProtocolTemps() {
        if (protocols.getValue() == null) {
            protocols.setValue(repository.getProtocolTemps());
        }
        return protocols.getValue();
    }

    public void setProtocolError(Throwable error) {
        protocolError.setValue(error);
    }

    public void downloadProtocolTemps(QueryToken token) {
        if (!HttpManager.isInternetAvailable()) {
            protocolError.setValue(new NetworkErrorException());
            return;
        }
        repository.getProtocolsFromServer(token).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ProtocolTemp>> call, @NonNull Response<List<ProtocolTemp>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    repository.insertProtocolTempWithDropDb(response.body());
                    protocols.postValue(response.body());
                } else
                    protocolError.postValue(new UnknownError());
            }

            @Override
            public void onFailure(@NonNull Call<List<ProtocolTemp>> call, @NonNull Throwable t) {
                protocolError.postValue(new FailedDownloadDb(ProtocolTempDatabase.DB_NAME));
            }
        });
    }

}