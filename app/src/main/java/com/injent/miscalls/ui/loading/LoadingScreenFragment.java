package com.injent.miscalls.ui.loading;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.injent.miscalls.R;
import com.injent.miscalls.ui.main.MainViewModel;

public class LoadingScreenFragment extends Fragment {

    private NavController navController;
    private MainViewModel viewModel;

    public LoadingScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(requireView());

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        viewModel.getActiveSession().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                navController.navigate(R.id.action_loadingScreenFragment_to_homeFragment);
            } else {
                navController.navigate(R.id.action_loadingScreenFragment_to_authFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
    }
}