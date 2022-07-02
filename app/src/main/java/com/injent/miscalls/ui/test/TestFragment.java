package com.injent.miscalls.ui.test;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentTestBinding;
import com.injent.miscalls.ui.main.MainActivity;
import com.injent.miscalls.util.CustomOnBackPressedFragment;

import java.io.IOException;
import java.text.ParseException;

public class TestFragment extends Fragment implements CustomOnBackPressedFragment {

    private FragmentTestBinding binding;
    private TestViewModel viewModel;

    public TestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_test, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(TestViewModel.class);
        viewModel.init();

        binding.testDelete.setOnClickListener(v -> {
            int id;
            try {
                id = Integer.parseInt(binding.testPatientDelete.getText().toString().trim());
            } catch (NumberFormatException e) {
                id = 1;
            }
            viewModel.deletePatient(requireContext(), id);
        });

        binding.testAdd.setOnClickListener(v -> {
            String name = binding.testPatientName.getText().toString().trim();
            viewModel.addPatient(requireContext(), name, App.getUser().getId());
        });

        viewModel.getError().observe(getViewLifecycleOwner(), throwable -> Toast.makeText(requireContext(), throwable.getMessage(), Toast.LENGTH_LONG).show());

        binding.testBack.setOnClickListener(v -> ((MainActivity) requireActivity()).onBackPressed());
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
        binding = null;
    }
}