package com.injent.miscalls.domain;

import androidx.lifecycle.ViewModelProvider;

import com.injent.miscalls.API.HttpManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.patientlist.PatientDao;
import com.injent.miscalls.data.patientlist.QueryPatients;
import com.injent.miscalls.ui.home.HomeFragment;
import com.injent.miscalls.ui.home.HomeViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class HomeRepository {

    public HomeRepository() {
    }

    public Call<List<Patient>> getPatientList(QueryPatients token) {
        return HttpManager.getMisAPI().patients(token);
    }

    public void deleteAll() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {
                App.getInstance().getPdb().clearAllTables();
                service.shutdown();
            }
        });
    }

    public void insertPatient(Patient... patients) {
        PatientDao dao = App.getInstance().getPatientDao();
        for (Patient patient : patients) {
            dao.insert(patient);
        }
        HomeViewModel homeViewModel = new ViewModelProvider(HomeFragment.getInstance())
                .get(HomeViewModel.class);
        HomeFragment.getInstance().requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                homeViewModel.setList(dao.getAll());
            }
        });
    }

    public void updatePatient(Patient... patients) {
        PatientDao dao = App.getInstance().getPatientDao();
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (Patient patient : patients) {
                    dao.update(patient);
                }
                service.shutdown();
            }
        });
    }

    public List<Patient> getAll() {
        PatientDao dao = App.getInstance().getPatientDao();
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {

            }
        });
        return null;
    }

    public void insertWithDropDb(Patient...patients) {
        PatientDao dao = App.getInstance().getPatientDao();
        App.getInstance().getPdb().clearAllTables();

        for (Patient patient : patients) {
            dao.insert(patient);
        }
        HomeViewModel homeViewModel = new ViewModelProvider(HomeFragment.getInstance())
                .get(HomeViewModel.class);
        HomeFragment.getInstance().requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                homeViewModel.setList(dao.getAll());
            }
        });
    }
}


