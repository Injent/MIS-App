package com.injent.miscalls.domain;

import android.util.Log;

import com.injent.miscalls.API.HttpManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.patientlist.QueryToken;
import com.injent.miscalls.data.templates.ProtocolDao;
import com.injent.miscalls.data.templates.ProtocolTemp;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;

public class ProtocolTempRepository {

    private final ProtocolDao dao;
    private final ExecutorService es;

    public ProtocolTempRepository() {
        this.dao = App.getInstance().getProtocolDao();
        this.es = Executors.newSingleThreadExecutor();
    }

    public Call<List<ProtocolTemp>> getProtocolsFromServer(QueryToken token) {
        return HttpManager.getMisAPI().protocolTemps(token);
    }

    public void clearDb() {
        es.submit(new Runnable() {
            @Override
            public void run() {
                App.getInstance().getProtocolDatabase().clearAllTables();
            }
        });
    }

    public ProtocolTemp getProtocolTempById(int id) {
        Future<ProtocolTemp> protocolTempFuture = es.submit(new Callable<ProtocolTemp>() {
            @Override
            public ProtocolTemp call() {
                return dao.getProtocolById(id);
            }
        });
        try {
            return protocolTempFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertProtocol(ProtocolTemp protocolTemp) {
        es.submit(new Runnable() {
            @Override
            public void run() {
                dao.insert(protocolTemp);
            }
        });
    }

    public Boolean insertProtocolTempWithDropDb(List<ProtocolTemp> list) {
        Future<Boolean> booleanFuture = es.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                App.getInstance().getProtocolDatabase().clearAllTables();
                for (ProtocolTemp protocol : list) {
                    dao.insert(protocol);
                }
                return true;
            }
        });
        try {
            return booleanFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ProtocolTemp> getProtocolTemps() {
        Future<List<ProtocolTemp>> listFuture = es.submit(new Callable<List<ProtocolTemp>>() {
            @Override
            public List<ProtocolTemp> call(){
                for (ProtocolTemp p : dao.getAll()) {
                }
                return dao.getAll();
            }
        });
        try {
            return listFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
