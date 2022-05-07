package com.injent.miscalls.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentSettingsBinding;
import com.injent.miscalls.domain.repositories.SettingsRepository;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsRepository repository;
    private Spinner spinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings,
                container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new SettingsRepository();

        binding.backFromSettings.setOnClickListener(view0 -> back());

        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                back();
            }
        });

        spinner = binding.spinner;
        spinner.setAdapter(repository.getAdapter(requireContext()));
        spinner.setSelection(App.getInstance().getMode());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                repository.setMode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing
            }
        });

        binding.clearProtocolTempsAction.setOnClickListener(view1 -> {
            Toast.makeText(requireContext(),R.string.protocolTempsCleared, Toast.LENGTH_SHORT).show();
        });
    }

    private void back() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("updateList", true);
        Navigation.findNavController(requireView())
                .navigate(R.id.homeFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}