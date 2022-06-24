package com.injent.miscalls.ui.callstuff;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.injent.miscalls.R;
import com.injent.miscalls.data.database.medcall.Geo;
import com.injent.miscalls.databinding.FragmentCallStuffBinding;
import com.injent.miscalls.util.CustomOnBackPressedFragment;
import com.injent.miscalls.ui.adapters.ViewPagerAdapter;

public class CallStuffFragment extends Fragment implements CustomOnBackPressedFragment {

    private FragmentCallStuffBinding binding;
    private CallStuffViewModel viewModel;
    private ViewPagerAdapter adapter;
    private NavController navController;
    private int callId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            callId = getArguments().getInt(getString(R.string.keyCallId));
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_call_stuff, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(requireView());
        viewModel = new ViewModelProvider(requireActivity()).get(CallStuffViewModel.class);
        viewModel.init();

        viewModel.loadCall(callId);

        binding.callInfopdfWebView.setInitialScale(150);
        binding.titleCallStuff.setText(R.string.medCall);
        setListeners();
        setupViewPager2();
    }

    @Override
    public boolean onBackPressed() {
        confirmExitAction();
        return false;
    }

    private void setListeners() {
        // Listeners
        binding.darkBgPdf.setOnClickListener(v -> {
            if (binding.darkBgPdf.getVisibility() == View.VISIBLE) {
                previewPdf("");
            }
        });

        binding.doneButton.setOnClickListener(v -> viewModel.saveRegistry());

        binding.previewPdfButton.setOnClickListener(v -> viewModel.loadHtml(requireContext()));

        binding.closeButton.setOnClickListener(v -> confirmExitAction());

        binding.doneButton.setOnClickListener(v -> save());

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                View child = binding.viewPager.getChildAt(0);
                if (child instanceof RecyclerView)
                    child.setOverScrollMode(View.OVER_SCROLL_NEVER);
            }
        });

        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: {
                        binding.titleCallStuff.setText(R.string.medCall);
                    }
                    break;
                    case 1: {
                        binding.titleCallStuff.setText(R.string.inspection);
                    }
                    break;
                    case 2: {
                        binding.titleCallStuff.setText(R.string.diagnosis);
                    }
                    break;
                    default: throw new IllegalStateException();
                }

                @SuppressLint("InflateParams")
                TabLayout.TabView tabView = tab.view;
                ImageView tabIcon = tabView.findViewById(R.id.tabIcon);
                tabIcon.animate()
                        .setDuration(200L)
                        .scaleX(1.05f)
                        .scaleY(1.05f)
                        .start();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                @SuppressLint("InflateParams")
                TabLayout.TabView tabView = tab.view;
                ImageView tabIcon = tabView.findViewById(R.id.tabIcon);
                tabIcon.animate()
                        .setDuration(200L)
                        .scaleX(1f)
                        .scaleY(1f)
                        .start();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Nothing to do
            }
        });

        // Observers
        viewModel.getHtmlLiveData().observe(getViewLifecycleOwner(), this::previewPdf);

        viewModel.getCallLiveData().observe(getViewLifecycleOwner(), callInfo -> viewModel.loadRegistry(null));

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), throwable -> {
            if (throwable == null) {
                Toast.makeText(requireContext(),getString(R.string.docMovedTo) + " " + getString(R.string.registry),Toast.LENGTH_LONG).show();
                navigateToHome();
                return;
            }
            if (throwable instanceof NullPointerException) {
                Toast.makeText(requireContext(), R.string.diagnosesIsEmpty, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getActionLiveData().observe(getViewLifecycleOwner(), action -> {
            switch (action) {
                case 1: openMap();
                break;
            }
        });
    }

    private void previewPdf(String s) {
        if (binding.darkBgPdf.getVisibility() == View.VISIBLE) {
            binding.darkBgPdf.setVisibility(View.INVISIBLE);
            binding.callInfopdfWebView.setVisibility(View.INVISIBLE);
        } else {
            binding.darkBgPdf.setVisibility(View.VISIBLE);
            binding.callInfopdfWebView.loadDataWithBaseURL(null,s,"text/html","utf-8",null);
            binding.callInfopdfWebView.setVisibility(View.VISIBLE);
        }
    }

    private void confirmExitAction() {
        if (!viewModel.isInspectionDone()) {
            AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.exitFromEditing)
                    .setMessage(R.string.dataWontSave)
                    .setPositiveButton(R.string.cancel, (dialog, b) -> dialog.dismiss())
                    .setNegativeButton(R.string.ok, (dialog, b) -> navigateToHome())
                    .create();
            alertDialog.show();
        } else {
            navigateToHome();
        }
    }

    private void save() {
        viewModel.saveRegistry();
    }

    private void setupViewPager2() {
        adapter = new ViewPagerAdapter(CallStuffFragment.this, 3, viewModel);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setUserInputEnabled(false);
        new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) -> tab.setCustomView(configureTab(position))).attach();
        binding.viewPager.setVisibility(View.VISIBLE);
    }

    private View configureTab(int position) {
        @SuppressLint("InflateParams")
        View tabView = getLayoutInflater().inflate(R.layout.tab_view, null);
        ImageView tabIcon = tabView.findViewById(R.id.tabIcon);
        TextView tabText = tabView.findViewById(R.id.tabText);

        switch (position) {
            case 0: {
                tabIcon.setImageResource(R.drawable.ic_medical_suitcase);
                tabText.setText(R.string.medCall);
            }
            break;
            case 1: {
                tabIcon.setImageResource(R.drawable.ic_inspection);
                tabText.setText(R.string.inspectionShort);
            }
            break;
            case 2: {
                tabIcon.setImageResource(R.drawable.ic_diagnosis);
                tabText.setText(R.string.diagnosis);
            }
            break;
            default: throw new IllegalStateException();
        }

        return tabView;
    }

    private void navigateToHome() {
        Bundle args = new Bundle();
        args.putBoolean(getString(R.string.keyDownloadDb), false);
        navController.navigate(R.id.homeFragment, args);
    }

    private void openMap() {
        Geo geo = viewModel.getGeo();
        if (geo == null) return;
        Bundle args = new Bundle();
        args.putDouble(getString(R.string.keyLatitude), geo.getLatitude());
        args.putDouble(getString(R.string.keyLongitude), geo.getLongitude());
        navController.navigate(R.id.mapsFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
        adapter = null;
        navController = null;
        binding = null;
    }
}