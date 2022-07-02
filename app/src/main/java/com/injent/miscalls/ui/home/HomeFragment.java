package com.injent.miscalls.ui.home;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentHomeBinding;
import com.injent.miscalls.ui.adapters.CallAdapter;
import com.injent.miscalls.util.AppBarStateChangeListener;
import com.injent.miscalls.util.CustomOnBackPressedFragment;
import com.injent.miscalls.util.FailedDownloadDb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment implements CustomOnBackPressedFragment {

    private CallAdapter adapter;

    private FragmentHomeBinding binding;
    private NavController navController;
    private HomeViewModel viewModel;
    private boolean downloadDb;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            downloadDb = getArguments().getBoolean(getString(R.string.keyDownloadDb));
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }

    @SuppressLint({"CommitPrefEdits", "NonConstantResourceId"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        viewModel.init(requireContext());
        navController = Navigation.findNavController(requireView());

        menuSetup();
        setupRecyclerView();
        setListeners();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void setListeners() {
        //Listeners
        binding.patientListSection.setOnClickListener(v -> downloadNewDb());

        binding.moreButton.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        binding.homeAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {
                    binding.patientListSection.animate().setDuration(300L).alpha(0f).start();
                } else {
                    binding.patientListSection.animate().setDuration(300L).alpha(1f).start();
                }
            }
        });

        //Observers
        viewModel.getCallListLiveData().observe(getViewLifecycleOwner(), callList -> {
            hideLoading();
            adapter.submitList(callList);
            displayDbDate();
        });

        viewModel.getCallListError().observe(getViewLifecycleOwner(), throwable -> {
            hideLoading();
            failed(throwable);
        });
    }

    private void displayList() {
        hideLoading();
        hideErrorMessage();
        viewModel.loadCallList(App.getUser().getId());
    }

    private void failed(Throwable t) {
        if (t instanceof FailedDownloadDb) {
            Toast.makeText(requireContext(), R.string.unknownError, Toast.LENGTH_SHORT).show();
        } else if (t instanceof NetworkErrorException) {
            displayList();
            Toast.makeText(requireContext(), R.string.noInternetConnection, Toast.LENGTH_SHORT).show();
        } else if (t instanceof ArrayStoreException) {
            showErrorMessage(getResources().getString(R.string.emptyDbError));
        }
    }

    @SuppressLint("SetTextI18n")
    public void displayDbDate() {
        Date date = App.getUser().getDbUpdateTime();
        String dateString = new SimpleDateFormat("dd.MM.yyyy / HH:mm", Locale.getDefault()).format(date);
        binding.dbDateUpdateText.setText(String.format(getString(R.string.updated), dateString));
    }

    private void showLoading() {
        binding.loadingBar.setVisibility(View.VISIBLE);
        binding.patientsRecycler.setVisibility(View.GONE);
        binding.errorText.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.loadingBar.setVisibility(View.GONE);
        binding.patientsRecycler.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String message) {
        binding.errorText.setText(message);
        binding.errorText.setVisibility(View.VISIBLE);
        binding.patientsRecycler.setVisibility(View.GONE);
    }

    private void hideErrorMessage() {
        binding.errorText.setVisibility(View.GONE);
        binding.patientsRecycler.setVisibility(View.VISIBLE);
        binding.loadingBar.setVisibility(View.GONE);
    }

    private void navigateToCallStuff(int callId) {
        if (notMatchingDestination()) return;
        Bundle args = new Bundle();
        args.putInt(getString(R.string.keyCallId), callId);
        navController.navigate(R.id.callStuffFragment, args);
    }

    private void navigateToTest() {
        closeNavigationMenu();
        navController.navigate(R.id.testFragment);
    }

    private void navigateToSettings() {
        closeNavigationMenu();
        navController.navigate(R.id.settingsFragment);
    }

    private void downloadNewDb() {
        showLoading();
        viewModel.downloadCallsDb(requireContext(), App.getUser());
    }

    private void navigateToAuth() {
        closeNavigationMenu();
        navController.navigate(R.id.authFragment);
    }

    private void navigateToRegistry() {
        closeNavigationMenu();
        navController.navigate(R.id.registryFragment);
    }

    private void navigateToHandbook() {
        closeNavigationMenu();
        Bundle args = new Bundle();
        args.putInt(getString(R.string.keyDiagnosisId), -1);
        navController.navigate(R.id.handBookFragment, args);
    }

    private boolean notMatchingDestination() {
        return Objects.requireNonNull(navController.getCurrentDestination(), "CURRENT DESTINATION EMPTY").getId() != R.id.homeFragment;
    }

    @SuppressLint("NonConstantResourceId")
    private void menuSetup() {
        NavigationView navigationView = binding.navigationView;
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.settingsMenu: navigateToSettings();
                break;
                case R.id.registry: navigateToRegistry();
                break;
                case R.id.handbookMKB: navigateToHandbook();
                break;
                case R.id.logoutMenu: {
                    viewModel.logout();
                    navigateToAuth();
                }
                break;
                default: closeNavigationMenu();
            }
            return false;
        });

        TextView fullName = navigationView.getHeaderView(0).findViewById(R.id.fullname);
        fullName.setText(App.getUser().getFullName());
        TextView position = navigationView.getHeaderView(0).findViewById(R.id.proffesion);
        position.setText(App.getUser().getWorkingPosition());
    }

    private void closeNavigationMenu() {
        binding.drawerLayout.closeDrawer(GravityCompat.START,false);
    }

    private void setupRecyclerView() {
        //Divider
        if (binding.patientsRecycler.getItemDecorationCount() == 0) {
            DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.divider_layer, requireContext().getTheme())));
            binding.patientsRecycler.addItemDecoration(divider);
        }

        //RecyclerView
        binding.patientsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CallAdapter(this::navigateToCallStuff);

        binding.patientsRecycler.setAdapter(adapter);

        //Display list
        if (downloadDb) downloadNewDb();
        else displayList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
        navController = null;
        adapter = null;
        binding = null;
    }
}