package com.injent.miscalls.ui.home;

import android.accounts.NetworkErrorException;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
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

import com.google.android.material.divider.MaterialDividerItemDecoration;
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
    private PatientAdapter patientAdapter;

    public static HomeFragment getInstance() {
        return instance.get();
    }

    private FragmentHomeBinding binding;
    private NavController navController;
    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        instance = new WeakReference<>(HomeFragment.this);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        //
        MainActivity.getInstance().enableFullScreen();
        navController = Navigation.findNavController(requireView());

        //Listeners
        binding.patientListSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setArguments(null);
                showLoading();
                homeViewModel.downloadPatientsDb();
            }
        });

        ImageView moreButton = requireView().findViewById(R.id.moreButton);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //Observers
        homeViewModel.getPatientListLiveData().observe(this, new Observer<List<Patient>>() {
            @Override
            public void onChanged(List<Patient> patients) {
                patientAdapter.submitList(patients);
                if (getArguments() == null) displayList();
            }
        });

        homeViewModel.getPatientListError().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                hideLoading();
                failed(throwable);
            }
        });

        homeViewModel.getDbDateLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayDbDate();
            }
        });

        //On back pressed action
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        MainActivity.getInstance().confirmExit();
                    }
        });

        //Navigation view
        NavigationView navigationView = binding.navigationView;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settingsMenu: navigateToSettings();
                    break;
                    case R.id.about: moveToSite(getText(R.string.aboutLink));
                    break;
                    case R.id.help: moveToSite(getText(R.string.helpLink));
                    break;
                    case R.id.protocolTempsMenu: navigateToProtocolTemp();
                    break;
                }
                return false;
            }
        });

        //Display of full name and working position in Navigation view
        User user = App.getInstance().getUser();
        TextView fullName = navigationView.getHeaderView(0).findViewById(R.id.fullname);
        fullName.setText(user.getFullName());
        TextView position = navigationView.getHeaderView(0).findViewById(R.id.proffesion);
        position.setText(user.getWorkingPosition());

        //Display list?
        if (getArguments() != null && getArguments().containsKey("updateList")) {
            displayList();
        }

        //RecycleView Material Divider
        if (binding.patientsRecycler.getItemDecorationCount() == 0)
            binding.patientsRecycler.addItemDecoration(getDivider());

        //RecycleView
        binding.patientsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        patientAdapter = new PatientAdapter(new PatientAdapter.OnItemClickListener() {
            @Override
            public void onClick(int patientId) {
                navigateToPatientCard(patientId);
            }
        });

        binding.patientsRecycler.setAdapter(patientAdapter);
    }

    public void displayList() {
        Log.d("HomeFragment","LIST UPDATED");
        hideLoading();
        List<Patient> patients = homeViewModel.getPatientList();

        if (patients.isEmpty()) {
            failed(new ListEmptyException());
        } else {
            displayDbDate();
            hideErrorMessage();
        }
    }

    private void failed(Throwable t){
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
        binding.dbDateUpdateText.setText(getResources().getString(R.string.updated) + " " + homeViewModel.getDbDate());
    }

    private void showLoading() {
        binding.loadingBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        binding.loadingBar.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage(String message) {
        binding.errorText.setText(message);
        binding.errorText.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        binding.errorText.setVisibility(View.INVISIBLE);
    }

    public MaterialDividerItemDecoration getDivider() {
        MaterialDividerItemDecoration divider
                = new MaterialDividerItemDecoration(requireContext(), MaterialDividerItemDecoration.VERTICAL);
        divider.setDividerInsetStartResource(requireContext(),R.dimen.dividerInset);
        divider.setDividerInsetEndResource(requireContext(), R.dimen.dividerInset);
        divider.setDividerColorResource(requireContext(), R.color.line);
        return divider;
    }

    private void navigateToPatientCard(int patientId) {
        Bundle bundle = new Bundle();
        bundle.putInt("patientId", patientId);
        navController.navigate(R.id.action_homeFragment_to_patientCardFragment, bundle);
    }

    private void navigateToSettings() {
        navController.navigate(R.id.action_homeFragment_to_settingsFragment);
    }

    private void navigateToProtocolTemp() {
        navController.navigate(R.id.action_homeFragment_to_protocolTempFragment);
    }

    private void moveToSite(CharSequence link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) link));
        requireActivity().startActivity(intent);
    }
}