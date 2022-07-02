package com.injent.miscalls.ui.auth;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.injent.miscalls.util.CustomOnBackPressedFragment;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;

public class AuthFragment extends Fragment implements CustomOnBackPressedFragment {

    private FragmentAuthBinding binding;
    private AuthViewModel viewModel;
    private NavController navController;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private boolean downloadDb = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth,
                container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        viewModel.init(requireContext());
        navController = Navigation.findNavController(requireView());

        binding.loading.setVisibility(View.GONE);
        binding.authLayout.setVisibility(View.VISIBLE);
        setListeners();
        setupBiometricAuthorization();

        binding.copyrightText.setText(getString(R.string.app_name));
        binding.version.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void setListeners() {
        // Observers
        viewModel.getActiveUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                successfulAuth();
            } else {
                setupBiometricAuthorization();
            }
        });

        viewModel.getErrorUser().observe(this, throwable -> {
            hideLoading();
            displayError(throwable);
        });

        // Listeners
        binding.signInButton.setOnClickListener(v -> {
            showLoading();
            auth();
        });

        binding.authFingerprint.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));
    }

    private void auth() {
        binding.error.setVisibility(View.INVISIBLE);
        String login = binding.emailField.getText().toString().trim();
        String password = binding.passwordField.getText().toString().trim();

        viewModel.auth(login, password);
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
        downloadDb = true;
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
                Toast.makeText(requireContext(), R.string.fingerprint_not_recognized, Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometricAuth))
                .setNegativeButtonText(getString(R.string.cancel))
                .build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
        biometricPrompt = null;
        promptInfo = null;
        navController = null;
        binding = null;
    }
}
