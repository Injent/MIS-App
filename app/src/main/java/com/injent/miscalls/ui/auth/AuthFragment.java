package com.injent.miscalls.ui.auth;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.injent.miscalls.BuildConfig;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentAuthBinding;
import com.injent.miscalls.network.AuthorizationException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;

public class AuthFragment extends Fragment {

    private FragmentAuthBinding binding;
    private AuthViewModel viewModel;
    private NavController navController;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private boolean downloadDb = false;
    private boolean moveToSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            moveToSettings = getArguments().getBoolean(getString(R.string.keyMoveToSettings));
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth,
                container, false);

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        navController = Navigation.findNavController(requireView());

        binding.loading.setVisibility(View.GONE);
        binding.authLayout.setVisibility(View.VISIBLE);
        setListeners();
        setupBiometricAuthorization();

        binding.copyrightText.setText(getString(R.string.app_name));
        binding.version.setText(BuildConfig.VERSION_NAME);
    }

    private void setListeners() {
        // Live Data observers
        viewModel.getActiveUser().observe(getViewLifecycleOwner(), hasActiveUser -> {
            downloadDb = !hasActiveUser;
            if (hasActiveUser)
                successfulAuth();
            else
                setupBiometricAuthorization();
        });

        viewModel.getAuthOfflineLiveData().observe(getViewLifecycleOwner(), authed -> {
            if (authed) {
                successfulAuth();
            } else {
                actionAuth(true);
            }
        });

        viewModel.getAuthorized().observe(this, authed -> {
            hideLoading();
            if (authed) {
                successfulAuth();
            }
        });

        viewModel.getErrorUser().observe(this, throwable -> {
            hideLoading();
            displayError(throwable);
        });

        // UI Listeners
        binding.signInButton.setOnClickListener(v -> {
            showLoading();
            actionAuth(false);
        });

        binding.authFingerprint.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmExit();
            }
        });

        viewModel.findActiveUser();
    }

    private void actionAuth(boolean fromServer) {
        binding.error.setVisibility(View.INVISIBLE);
        String login = binding.emailField.getText().toString().trim();
        String password = binding.passwordField.getText().toString().trim();

        if (fromServer)
            viewModel.auth(login, password);
        else
            viewModel.authFromDb(login, password);
    }

    private void displayError(Throwable throwable) {
        binding.error.setVisibility(View.VISIBLE);
        if (throwable instanceof AuthorizationException) {
            binding.error.setText(throwable.getMessage());
        } else if (throwable instanceof NetworkErrorException) {
            binding.error.setText(R.string.noInternetConnection);
        } else if (throwable instanceof SocketTimeoutException) {
            binding.error.setText(R.string.socketTimeOut);
        } else if (throwable instanceof ConnectException) {
            binding.error.setText(R.string.serverDoesNotRespond);
        } else {
            binding.error.setText(R.string.unknownError);
            throwable.printStackTrace();
        }
    }

    private void successfulAuth() {
        Toast.makeText(requireContext(), R.string.successfulAuth, Toast.LENGTH_SHORT).show();
        navigateToHome();
    }

    private void hideLoading() {
        binding.authLoading.setVisibility(View.INVISIBLE);
    }
    private void showLoading() {
        binding.authLoading.setVisibility(View.VISIBLE);
    }

    private void navigateToHome() {
        Bundle args = new Bundle();
        args.putBoolean(getString(R.string.keyDownloadDb), downloadDb);
        navController.navigate(R.id.homeFragment, args);
    }

    private void navigateToSettings() {
        navController.navigate(R.id.settingsFragment);
    }

    public void confirmExit() {
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.exit)
                .setPositiveButton(R.string.yes, (dialog, b) -> closeApp())
                .setNegativeButton(R.string.no, (dialog, b) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    private void setupBiometricAuthorization() {
        Executor executor = ContextCompat.getMainExecutor(requireContext());

        biometricPrompt = new BiometricPrompt(AuthFragment.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(requireContext(), errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                viewModel.authWithBiometric();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(requireContext(), R.string.unknownError, Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometricAuth))
                .setNegativeButtonText(getString(R.string.cancel))
                .build();
    }

    public void closeApp() {
        requireActivity().finish();
        System.exit(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
        biometricPrompt = null;
        promptInfo = null;
        binding = null;
    }
}
