package com.injent.miscalls.ui.home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.database.medcall.MedCall;
import com.injent.miscalls.data.database.user.User;
import com.injent.miscalls.domain.repositories.AuthRepository;
import com.injent.miscalls.domain.repositories.CallRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private CallRepository callRepository;
    private AuthRepository authRepository;

    private LiveData<List<MedCall>> callList;
    private LiveData<Throwable> error;

    public void init(Context context) {
        callRepository = new CallRepository();
        authRepository = new AuthRepository(context);
        callList = callRepository.getCallsLiveData();
        error = callRepository.getErrorLiveData();
    }

    public LiveData<List<MedCall>> getCallListLiveData() {
        return callList;
    }

    public LiveData<Throwable> getCallListError(){
        return error;
    }

    public void loadCallList(int userId) {
        callRepository.loadAllCallsInfo(userId);
    }

    public void logout() {
        App.getEncryptedUserDataManager().setData(R.string.keyAuthed, false).write();
        App.getUser().setAuthed(false);
        authRepository.updateUser(App.getUser());
    }

    public void downloadCallsDb(Context context, User user) {
        callRepository.downloadCallList(context, user);
    }

    @Override
    protected void onCleared() {
        callList = null;
        error = null;

        authRepository.clear();
        callRepository.clear();
        super.onCleared();
    }
}
