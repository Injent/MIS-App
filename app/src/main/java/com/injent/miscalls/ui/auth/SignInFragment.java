package com.injent.miscalls.ui.auth;

import android.accounts.NetworkErrorException;
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

import com.injent.miscalls.MainActivity;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentSignInBinding;
import com.injent.miscalls.data.Keys;

public class SignInFragment extends Fragment {

    private FragmentSignInBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in,
                container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        if (viewModel.isAuthed()) {
            navigateToHome();
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
        viewModel.setAuthed(true);

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
        Bundle bundle = new Bundle();
        bundle.putBoolean(Keys.DOWNLOAD_DB, true);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_signInFragment_to_homeFragment, bundle);
    }
}
