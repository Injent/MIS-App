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

import com.injent.miscalls.MainActivity;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentSettingsBinding;
import com.injent.miscalls.domain.AuthRepository;
import com.injent.miscalls.domain.ProtocolTempRepository;
import com.injent.miscalls.domain.SettingsRepository;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private static final float TEXTVIEW_FONT = 14;
    private SettingsRepository repository;
    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings,
                container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new SettingsRepository();

        MainActivity.getInstance().disableFullScreen();

        binding.logoutAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AuthRepository().setAuthed(false);
                navigateToAuth();
            }
        });

        binding.backFromSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                back();
            }
        });

        spinner = binding.spinner;
        spinner.setAdapter(repository.getAdapter(requireContext()));

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

        binding.clearProtocolTempsAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ProtocolTempRepository().clearDb();
                Toast.makeText(requireContext(),R.string.protocolTempsCleared, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void back() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("updateList", true);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_settingsFragment_to_homeFragment, bundle);
    }

    private void navigateToAuth() {
        Navigation.findNavController(requireView()).navigate(R.id.action_settingsFragment_to_signInFragment);
    }
}