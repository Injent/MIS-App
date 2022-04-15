package com.injent.miscalls.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentSettingsBinding;
import com.injent.miscalls.domain.HomeRepository;
import com.injent.miscalls.domain.SettingsRepository;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private static final float TEXTVIEW_FONT = 14;
    private SettingsRepository repository;

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

        binding.backFromSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back(true);
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                back(true);
            }
        });

        Spinner spinner = binding.spinner;

        spinner.setAdapter(repository.getAdapter(requireContext()));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                repository.setMode(position);
                TextView textView = (TextView) parent.getChildAt(position);
                textView.setTextColor(R.color.grayText);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,TEXTVIEW_FONT);
                textView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.clear_sans_medium));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing
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