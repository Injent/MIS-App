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

import com.injent.miscalls.App;
import com.injent.miscalls.BuildConfig;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentAuthBinding;
import com.injent.miscalls.network.AuthorizationException;

import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;

public class AuthFragment extends Fragment {

    private FragmentAuthBinding binding;
    private AuthViewModel viewModel;
    private NavController navController;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

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
        navController = Navigation.findNavController(requireView());

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
                successfulAuth();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(requireContext(), R.string.unknownError, Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Биометрическая авторизация")
                .setSubtitle("Приложите палец")
                .setNegativeButtonText("Отмена")
                .build();

//        if (App.getEncryptedUserDataManager().getBoolean(R.string.keyAuthed)) {
//            if (getArguments() != null && getArguments().getBoolean(getString(R.string.keyOffUpdates), false)) {
//                navigateToSettings();
//                return;
//            }
//            navigateToHome(false);
//            return;
//        }

        //Listeners
        binding.signInButton.setOnClickListener(v -> {
            showLoading();
            actionAuth();
        });

        binding.authFingerprint.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));

        //Observers
        viewModel.getAuthorized().observe(this, authed -> {
            hideLoading();
            successfulAuth();
        });

        viewModel.getErrorUser().observe(this, throwable -> {
            hideLoading();
            displayError(throwable);
        });

        //On back pressed action
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                        confirmExit();
                }
        });

        binding.copyrightText.setText(getString(R.string.app_name));
        binding.version.setText(BuildConfig.VERSION_NAME);
    }

    private void actionAuth() {
        binding.error.setVisibility(View.INVISIBLE);
        String email = binding.emailField.getText().toString().trim();
        String password = binding.passwordField.getText().toString().trim();
        viewModel.auth(email, password);
    }

    private void displayError(Throwable throwable) {
        binding.error.setVisibility(View.VISIBLE);
        if (throwable instanceof AuthorizationException) {
            binding.error.setText(throwable.getMessage());
        } else if (throwable instanceof NetworkErrorException) {
            binding.error.setText(R.string.noInternetConnection);
        } else if (throwable instanceof SocketTimeoutException) {
            binding.error.setText(R.string.socketTimeOut);
        } else {
            binding.error.setText(R.string.unknownError);
            throwable.printStackTrace();
        }
    }

    private void successfulAuth() {
        Toast.makeText(requireContext(), R.string.successfulAuth, Toast.LENGTH_SHORT).show();
        navigateToHome(true);
    }

    private void hideLoading() {
        binding.authLoading.setVisibility(View.INVISIBLE);
    }
    private void showLoading() {
        binding.authLoading.setVisibility(View.VISIBLE);
    }

    private void navigateToHome(boolean downloadDb) {
        Bundle args = new Bundle();
        if (downloadDb)
            args.putBoolean(getString(R.string.keyDownloadDb), true);
        else
            args.putBoolean(getString(R.string.keyUpdateList), true);
        navController.navigate(R.id.homeFragment, args);
    }

    private void navigateToSettings() {
        navController.navigate(R.id.settingsFragment);
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
