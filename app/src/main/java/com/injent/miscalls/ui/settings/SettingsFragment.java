package com.injent.miscalls.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentSettingsBinding;
import com.injent.miscalls.domain.HomeRepository;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

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

        binding.clearBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeRepository repository = new HomeRepository();
                repository.deleteAll();
                Toast.makeText(requireContext(),"Очищено",Toast.LENGTH_SHORT).show();
            }
        });

        binding.logoutAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = requireActivity()
                        .getSharedPreferences(App.PREFERENCES_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("auth",false);
                editor.apply();
                back(false);
            }
        });


        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                back(true);
            }
        });

        SwitchCompat regularUpdatesSwitch = binding.regularUpdatesSwitch;

        regularUpdatesSwitch.setChecked(App.getInstance().isAutoUpdate());

        regularUpdatesSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean update = regularUpdatesSwitch.isChecked();
                SharedPreferences sp = requireActivity()
                        .getSharedPreferences(App.PREFERENCES_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("autoUpdate",update);
                App.getInstance().setAutoUpdate(update);
                editor.apply();
            }
        });
    }

    private void back(boolean update) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("updateList", update);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_settingsFragment_to_homeFragment, bundle);
    }
}