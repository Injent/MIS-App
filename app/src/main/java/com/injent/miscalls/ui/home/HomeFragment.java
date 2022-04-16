package com.injent.miscalls.ui.home;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView;
import com.injent.miscalls.App;
import com.injent.miscalls.MainActivity;
import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.FailedDownloadDb;
import com.injent.miscalls.data.patientlist.ListEmptyException;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.User;
import com.injent.miscalls.databinding.FragmentHomeBinding;

import java.lang.ref.WeakReference;
import java.util.List;

public class HomeFragment extends Fragment {

    public static WeakReference<HomeFragment> instance;
    private NewPatientAdapter patientAdapter;

    public static HomeFragment getInstance() {
        return instance.get();
    }

    private FragmentHomeBinding binding;

    private HomeViewModel homeViewModel;

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

        NavController navController = Navigation.findNavController(requireView());

        MainActivity.getInstance().enableFullScreen();

        instance = new WeakReference<>(HomeFragment.this);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        authCheck();

        if (!App.getInstance().isSigned()) return;

        if (!App.getInstance().isInitialized()) {
            if (App.getInstance().getMode() == 0) {
                showLoading();
                homeViewModel.downloadPatientsDb();
            } else if (getArguments() == null) {
                displayList();
            }
            App.getInstance().setInitialized(true);

        }

        binding.patientListSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setArguments(null);
                showLoading();
                homeViewModel.downloadPatientsDb();
            }
        });

        homeViewModel.getPatientListLiveData().observe(this, new Observer<List<Patient>>() {
            @Override
            public void onChanged(List<Patient> patients) {
                if (getArguments() == null){
                    displayList();
                }
            }
        });

        homeViewModel.getPatientListError().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                hideLoading();
                failed(throwable);
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MainActivity.getInstance().confirmExit();
            }
        });

        homeViewModel.getDbDateLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayDbDate();
            }
        });

        ImageView moreButton = requireView().findViewById(R.id.moreButton);

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = binding.navigationView;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settingsMenu: navController.navigate(R.id.action_homeFragment_to_settingsFragment);
                    break;
                    case R.id.about: homeViewModel.moveToSite(requireContext(), getText(R.string.aboutLink));
                    break;
                    case R.id.help: homeViewModel.moveToSite(requireContext(), getText(R.string.helpLink));
                    break;
                }
                return false;
            }
        });

        //Display of full name and working position in nav view
        User user = App.getInstance().getUser();
        TextView fullName = navigationView.getHeaderView(0).findViewById(R.id.fullname);
        fullName.setText(user.getFullName());
        TextView position = navigationView.getHeaderView(0).findViewById(R.id.proffesion);
        position.setText(user.getWorkingPosition());

        if (getArguments() != null) {
            if (getArguments().getBoolean("updateList")) {
                displayList();
            }
        }

       binding.patientsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        patientAdapter = new NewPatientAdapter(new NewPatientAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pacientId) {
                //Navigation.findNavController(requireView()).navigate(R.);
            }
        });
        binding.patientsRecycler.setAdapter(patientAdapter);

        homeViewModel.getPatientListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Patient>>() {
            @Override
            public void onChanged(List<Patient> patients) {
                patientAdapter.submitList(patients);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void displayList() {
        Log.d("HomeFragment","LIST UPDATED");
        hideLoading();
        List<Patient> patients = homeViewModel.getPatientList();

        displayDbDate();

        PatientAdapter adapter = new PatientAdapter(patients);
 //
        if (binding.patientsRecycler.getItemDecorationCount() == 0)
            binding.patientsRecycler.addItemDecoration(homeViewModel.getDivider(requireContext()));
        binding.patientsRecycler.setAdapter(adapter);

        if (patients.isEmpty()) {
            failed(new ListEmptyException());
        } else {
            hideErrorMessage();
        }
    }

    public void failed(Throwable t){
        if (t instanceof FailedDownloadDb) {
            Toast.makeText(requireContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();
        } else if (t instanceof NetworkErrorException){
            displayList();
            Toast.makeText(requireContext(),R.string.noInternetConnection, Toast.LENGTH_SHORT).show();
        } else if (t instanceof ListEmptyException) {
            showErrorMessage(getResources().getString(R.string.emptyDbError));
        }
    }

    @SuppressLint("SetTextI18n")
    public void displayDbDate() {
        binding.dbDateUpdateText.setText(getResources().getString(R.string.updated) + " "
                + homeViewModel.getDbDate());
    }

    public void authCheck() {
        SharedPreferences sp = requireActivity()
                .getSharedPreferences(App.PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean signed = sp.getBoolean("auth", false);
        if (!signed) {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_signInFragment);
            App.getInstance().setSigned(false);
        } else {
            App.getInstance().setSigned(true);
        }
    }

    public void showLoading() {
        binding.loadingBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        binding.loadingBar.setVisibility(View.INVISIBLE);
    }

    public void showErrorMessage(String message) {
        binding.errorText.setText(message);
        binding.errorText.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        binding.errorText.setVisibility(View.INVISIBLE);
    }
}