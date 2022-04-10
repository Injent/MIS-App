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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("auth",false);
                editor.apply();
            }
        });

        binding.backHomeFromSettings.setOnClickListener(new View.OnClickListener() {
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
    }

    private void back() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("updateList", true);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_settingsFragment_to_homeFragment, bundle);
    }
}