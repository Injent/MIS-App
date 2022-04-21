package com.injent.miscalls.ui.auth;

import android.accounts.NetworkErrorException;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.injent.miscalls.App;
import com.injent.miscalls.MainActivity;
import com.injent.miscalls.R;
import com.injent.miscalls.data.AuthModelIn;
import com.injent.miscalls.data.User;
import com.injent.miscalls.databinding.FragmentSignInBinding;
import com.injent.miscalls.domain.AuthRepository;

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
        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();
                actionAuth();
            }
        });

        //Observers
        viewModel.getAuthorized().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean authed) {
                hideLoading();
                successfulAuth();
            }
        });

        viewModel.getErrorUser().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                hideLoading();
                displayError(throwable);
            }
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
        bundle.putBoolean("downloadDb", true);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_signInFragment_to_homeFragment, bundle);
    }
}
