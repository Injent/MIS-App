package com.injent.miscalls.domain;

import android.os.AsyncTask;

import androidx.lifecycle.ViewModelProvider;

import com.injent.miscalls.API.HttpManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.patientlist.PatientDao;
import com.injent.miscalls.ui.home.HomeFragment;
import com.injent.miscalls.ui.home.HomeViewModel;

import java.util.List;

import retrofit2.Call;

public class HomeRepository {

    public HomeRepository() {
    }

    public Call<List<Patient>> getPatientList() {
        return HttpManager.getMisAPI().patients();
    }

    public void deleteAll() {
        new DeleteAllAsyncTask(App.getInstance().getPatientDao()).execute();
    }

    public void deletePatient(Patient... patients)  {
        new DeletePatientAsyncTask(App.getInstance().getPatientDao()).execute(patients);
    }

    public void insertPatient(Patient... patients) {
        new InsertPatientAsyncTask(App.getInstance().getPatientDao()).execute(patients);
    }

    private static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private PatientDao patientDao;

        DeleteAllAsyncTask(PatientDao dao) {
            patientDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            App.getInstance().getPdb().clearAllTables();
            return null;
        }
    }

    private static class DeletePatientAsyncTask extends AsyncTask<Patient, Void, Void> {
        private PatientDao patientDao;

        DeletePatientAsyncTask(PatientDao dao) {
            patientDao = dao;
        }

        @Override
        protected Void doInBackground(final Patient... params) {
            for (Patient patient : params) {
                patientDao.delete(patient);
            }
            return null;
        }
    }

    private static class InsertPatientAsyncTask extends AsyncTask<Patient, Void, Void> {
        private PatientDao patientDao;

        InsertPatientAsyncTask(PatientDao dao) {
            patientDao = dao;
        }

        @Override
        protected Void doInBackground(final Patient... params) {
            for (Patient patient : params) {
                patientDao.insert(patient);
            }
            HomeFragment.getInstance().requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    HomeViewModel homeViewModel = new ViewModelProvider(HomeFragment.getInstance())
                            .get(HomeViewModel.class);
                    homeViewModel.getPatientList();
                }
            });
            return null;
        }
    }
}


