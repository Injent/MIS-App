package com.injent.miscalls.ui.home;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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

import com.google.android.material.navigation.NavigationView;
import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.User;
import com.injent.miscalls.data.database.FailedDownloadDb;
import com.injent.miscalls.data.database.calls.ListEmptyException;
import com.injent.miscalls.databinding.FragmentHomeBinding;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private CallAdapter adapter;

    private FragmentHomeBinding binding;
    private NavController navController;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }

    @SuppressLint({"CommitPrefEdits", "NonConstantResourceId"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        navController = Navigation.findNavController(requireView());

        //Listeners
        binding.patientListSection.setOnClickListener(view0 -> downloadNewDb());

        binding.moreButton.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        //Observers
        viewModel.getCallListLiveData().observe(getViewLifecycleOwner(), callList -> {
            hideLoading();
            adapter.submitList(callList);
        });

        viewModel.getCallListError().observe(getViewLifecycleOwner(), throwable -> {
            hideLoading();
            failed(throwable);
        });

        viewModel.getDbDateLiveData().observe(getViewLifecycleOwner(), this::displayDbDate);
        viewModel.loadDbDate();

        //On back pressed action
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmExit();
            }
        });

        menuSetup();
        recyclerViewSetup();
    }

    public void displayList() {
        hideLoading();
        hideErrorMessage();
        viewModel.loadCallList();
    }

    private void failed(Throwable t) {
        if (t instanceof FailedDownloadDb) {
            Toast.makeText(requireContext(), R.string.unknownError, Toast.LENGTH_SHORT).show();
        } else if (t instanceof NetworkErrorException) {
            displayList();
            Toast.makeText(requireContext(), R.string.noInternetConnection, Toast.LENGTH_SHORT).show();
        } else if (t instanceof ListEmptyException) {
            showErrorMessage(getResources().getString(R.string.emptyDbError));
        }
    }

    @SuppressLint("SetTextI18n")
    public void displayDbDate(String date) {
        binding.dbDateUpdateText.setText(String.format(getString(R.string.updated),date));
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

    public DividerItemDecoration getDivider() {
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.divider_layer, requireContext().getTheme())));

        return divider;
    }

    private void navigateToCallStuff(int callId) {
        if (notMatchingDestination()) return;
        Bundle args = new Bundle();
        args.putInt(getString(R.string.keyCallId), callId);
        navController.navigate(R.id.callStuffFragment, args);
    }

    private void navigateToSettings() {
        if (notMatchingDestination()) return;
        closeNavigationMenu();
        navController.navigate(R.id.settingsFragment);
    }

    private void moveToSite(String link) {
        closeNavigationMenu();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        requireActivity().startActivity(intent);
    }

    private void downloadNewDb() {
        showLoading();
        viewModel.downloadCallsDb();
    }

    private void navigateToAuth() {
        closeNavigationMenu();
        if (notMatchingDestination()) return;
        navController.navigate(R.id.authFragment);
    }

    private void navigateToRegistry() {
        if (notMatchingDestination()) return;
        navController.navigate(R.id.registryFragment);
    }

    private void navigateToHandbook() {
        if (notMatchingDestination()) return;
        navController.navigate(R.id.handBookFragment);
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
                case R.id.about: moveToSite(getString(R.string.aboutLink));
                    break;
                case R.id.help: moveToSite(getString(R.string.helpLink));
                    break;
                case R.id.registry: navigateToRegistry();
                    break;
                case R.id.handbookMKB: navigateToHandbook();
                    break;
                case R.id.logoutMenu: {
                    App.getUserSettings().setAuthed(false).write();
                    navigateToAuth();
                }
                break;
                default: closeNavigationMenu();
            }
            return false;
        });

        User user = App.getUser();
        TextView fullName = navigationView.getHeaderView(0).findViewById(R.id.fullname);
        fullName.setText(user.getFullName());
        TextView position = navigationView.getHeaderView(0).findViewById(R.id.proffesion);
        position.setText(user.getWorkingPosition());
    }

    private void closeNavigationMenu() {
        binding.drawerLayout.closeDrawer(GravityCompat.START,false);
    }

    private void recyclerViewSetup() {
        //Divider
        if (binding.patientsRecycler.getItemDecorationCount() == 0)
            binding.patientsRecycler.addItemDecoration(getDivider());

        //RecyclerView
        binding.patientsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CallAdapter(this::navigateToCallStuff);

        binding.patientsRecycler.setAdapter(adapter);

        //Display list
        if (getArguments() == null) return;

        if (getArguments().containsKey(getString(R.string.keyUpdateList))) {
            displayList();
        } else if (getArguments().containsKey(getString(R.string.keyDownloadDb)))
            downloadNewDb();
    }

    public void confirmExit() {
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.exit)
                .setPositiveButton(R.string.yes, (dialog, button0) -> closeApp())
                .setNegativeButton(R.string.no, (dialog, button1) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    public void closeApp() {
        requireActivity().finish();
        System.exit(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
        binding = null;
    }
}