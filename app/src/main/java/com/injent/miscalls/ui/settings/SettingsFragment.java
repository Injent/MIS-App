package com.injent.miscalls.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.App;
import com.injent.miscalls.ui.adapters.SettingAdapter;
import com.injent.miscalls.ui.main.MainActivity;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentSettingsBinding;
import com.injent.miscalls.util.ViewType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private NavController navController;

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

        navController = Navigation.findNavController(requireView());

        binding.backFromSettings.setOnClickListener(v -> back());

        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                back();
            }
        });

        setupSettingsRecyclerView();
    }

    private void setupSettingsRecyclerView() {
        SettingAdapter settingAdapter = new SettingAdapter();

        binding.settingsRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.settingsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.settings_divider, requireContext().getTheme())));
        binding.settingsRecyclerView.addItemDecoration(divider);
        binding.settingsRecyclerView.setAdapter(settingAdapter);
        List<ViewType> list = new ArrayList<>();
        list.add(new SectionLayout(R.string.miscellaneous));
        list.add(new SpinnerLayout(R.drawable.ic_clock, R.color.icClock, R.string.regularUpdates, App.getUserDataManager().getInt(R.string.keyMode), R.array.modes, new SpinnerLayout.OnItemClickListener() {
                    @Override
                    public void onOpen() {
                        // Nothing to do
                    }

                    @Override
                    public void onSelect(int position) {
                        App.getUserDataManager().setData(R.string.keyMode, position).write();
                    }
                }));
        list.add(new SwitchLayout(R.drawable.ic_call, R.color.green, R.string.anonCall, R.string.anonCallDesc, App.getUserDataManager().getBoolean(R.string.keyAnonCall), R.drawable.switch_thumb_phone, R.drawable.switch_track, new SwitchLayout.OnFlickListener() {
            @Override
            public void onFlick(boolean state) {
                App.getUserDataManager().setData(R.string.keyAnonCall, state).write();
            }
        }));
        list.add(new SectionLayout(R.string.interfaceName));
        list.add(new ButtonLayout(R.drawable.ic_folder, R.color.icFolder, R.string.openDocFolder, R.string.open, view -> ((MainActivity) requireContext()).openExplorer(App.getUserDataManager().getString(R.string.keyPdfFilePath))));
        settingAdapter.submitList(list);
    }


    private void back() {
        navController.navigate(R.id.homeFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navController = null;
        binding = null;
    }
}