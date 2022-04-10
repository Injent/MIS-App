package com.injent.miscalls.ui.home;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.FailedDownloadDb;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.databinding.FragmentHomeBinding;

import java.lang.ref.WeakReference;
import java.util.List;

public class HomeFragment extends Fragment {

    public static WeakReference<HomeFragment> instance;

    public static HomeFragment getInstance() {
        return instance.get();
    }

    private FragmentHomeBinding binding;

    private HomeViewModel homeViewModel;

    public HomeFragment() {
//        App application = (App) getActivity().getApplication();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,
                container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        instance = new WeakReference<>(HomeFragment.this);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        authCheck();

        if (!App.getInstance().isInitialized()) {
            showLoading();
            homeViewModel.downloadDb();
            App.getInstance().setInitialized(true);
        }

        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_homeFragment_to_settingsFragment);
            }
        });

        homeViewModel.getPatientListLiveData().observe(this, new Observer<List<Patient>>() {
            @Override
            public void onChanged(List<Patient> patients) {
                if (getArguments() == null)
                    displayList();
            }
        });

        homeViewModel.getPatientListError().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                failed(throwable);
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Do nothing
            }
        });

        if (getArguments() != null) {
            if (getArguments().getBoolean("updateList")) {
                displayList();
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void displayList() {
        Log.d("HomeFragment","LIST UPDATED");
        hideLoading();
        List<Patient> patients = homeViewModel.getPatientList();

        binding.updateListText.setText(homeViewModel.getDbDate());

        PatientAdapter adapter = new PatientAdapter(patients);
        if (binding.patientsList.getAdapter() == null)
            binding.patientsList.setAdapter(adapter);
        else
            binding.patientsList.getAdapter().notifyDataSetChanged();
    }

    public void failed(Throwable t){
        if (t instanceof FailedDownloadDb) {
            Toast.makeText(requireContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();
        } else if (t instanceof NetworkErrorException){
            Toast.makeText(requireContext(),R.string.noInternetConnection, Toast.LENGTH_SHORT).show();
        }
    }

    public void setDbDate() {
        SharedPreferences sp = requireActivity()
                .getSharedPreferences(App.PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("dbDate",homeViewModel.getDbDate());
        editor.apply();
    }

    public String getDbDate() {
        SharedPreferences sp = requireActivity()
                .getSharedPreferences(App.PREFERENCES_NAME, Context.MODE_PRIVATE);
        String date = sp.getString("dbDate","Лист пациентов не обновлялся");
        return date;

    }

    public void authCheck() {
        SharedPreferences sp = requireActivity()
                .getSharedPreferences(App.PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean signed = sp.getBoolean("auth", false);
        if (!signed) {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_signInFragment);
        }
    }

    public void showLoading() {
        binding.loadingBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        binding.loadingBar.setVisibility(View.INVISIBLE);
    }
}