package com.injent.miscalls.ui.auth;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.injent.miscalls.App;
import com.injent.miscalls.MainActivity;
import com.injent.miscalls.R;
import com.injent.miscalls.data.UserNotFoundException;
import com.injent.miscalls.databinding.FragmentAuthBinding;

public class AuthFragment extends Fragment {

    private FragmentAuthBinding binding;
    private AuthViewModel viewModel;

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

        if (App.getUserSettings().isAuthed()) {
            navigateToHome(false);
            return;
        }

        //Listeners
        binding.signInButton.setOnClickListener(view0 -> {
            showLoading();
            actionAuth();
        });

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
                        MainActivity.getInstance().confirmExit();
                }
        });

        binding.copyrightText.setText(getString(R.string.app_name));
        binding.version.setText(App.APP_VERSION);
    }

    private void actionAuth() {
        String email = binding.emailField.getText().toString().trim();
        String password = binding.passwordField.getText().toString().trim();
        viewModel.auth(email, password);
    }

    private void displayError(Throwable throwable) {
        binding.error.setVisibility(View.VISIBLE);
        if (throwable instanceof UserNotFoundException) {
            binding.error.setText(R.string.userNotFound);
        } else if (throwable instanceof NetworkErrorException) {
            binding.error.setText(R.string.noInternetConnection);
        } else {
            binding.error.setText(R.string.unknownError);
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
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.homeFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
