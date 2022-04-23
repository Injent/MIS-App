package com.injent.miscalls.ui.protocoltemp;

import android.accounts.NetworkErrorException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.API.HttpManager;
import com.injent.miscalls.data.patientlist.FailedDownloadDb;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.patientlist.QueryToken;
import com.injent.miscalls.data.templates.ProtocolDatabase;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.domain.ProtocolTempFletcher;
import com.injent.miscalls.domain.ProtocolTempRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProtocolTempViewModel extends ViewModel {

    private ProtocolTempRepository repository;
    private MutableLiveData<List<ProtocolTemp>> protocols = new MutableLiveData<>();
    private MutableLiveData<Throwable> protocolError = new MutableLiveData<>();
    private MutableLiveData<ProtocolTemp> selectedProtocol = new MutableLiveData<>();
    private MutableLiveData<ProtocolTemp> appliedProtocol = new MutableLiveData<>();

    public LiveData<ProtocolTemp> getSelectedProtocolLiveData() {
        return selectedProtocol;
    }

    public ProtocolTemp getSelectedProtocol(int id) {
        if (selectedProtocol.getValue() == null)
            selectedProtocol.setValue(repository.getProtocolTempById(id));
        return selectedProtocol.getValue();
    }

    public void saveSelectedProtocol(ProtocolTemp protocolTemp) {
        repository.insertProtocol(protocolTemp);
    }

    public void applyProtocolTemp(ProtocolTemp protocolTemp, Patient patient) {
        appliedProtocol.setValue(new ProtocolTempFletcher().fletch(protocolTemp, patient));
    }

    public LiveData<ProtocolTemp> getAppliedProtocolLiveData() {
        return appliedProtocol;
    }

    public LiveData<List<ProtocolTemp>> getProtocolsLiveDate() {
        return protocols;
    }

    public void setProtocols(List<ProtocolTemp> protocols) { this.protocols.setValue(protocols); }

    public LiveData<Throwable> getProtocolErrorLiveData() {
        return protocolError;
    }

    public ProtocolTempViewModel() {
        super();
        repository = new ProtocolTempRepository();
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
        repository.getProtocolsFromServer(token).enqueue(new Callback<List<ProtocolTemp>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProtocolTemp>> call, @NonNull Response<List<ProtocolTemp>> response) {
                if (response.isSuccessful() && response.body() != null){
                    repository.insertProtocolTempWithDropDb(response.body());
                    protocols.postValue(response.body());
                }
                else
                    protocolError.postValue(new UnknownError());
            }

            @Override
            public void onFailure(@NonNull Call<List<ProtocolTemp>> call, @NonNull Throwable t) {
                protocolError.postValue(new FailedDownloadDb(ProtocolDatabase.DB_NAME));
            }
        });
    }

}