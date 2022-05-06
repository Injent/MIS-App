package com.injent.miscalls.ui.patientstuff;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.injent.miscalls.R;
import com.injent.miscalls.data.calllist.CallInfo;
import com.injent.miscalls.databinding.FragmentCallStuffBinding;
import com.injent.miscalls.ui.diagnosis.DiagnosisFragment;
import com.injent.miscalls.ui.patientcard.PatientCardFragment;
import com.injent.miscalls.ui.protocol.InspectionFragment;
import com.injent.miscalls.ui.recommendations.RecommendationsFragment;

public class CallStuffFragment extends Fragment {

    private FragmentCallStuffBinding binding;
    private CallStuffViewModel viewModel;
    private ViewPagerAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_call_stuff,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CallStuffViewModel.class);

        viewModel.getCallLiveData().observe(getViewLifecycleOwner(), this::setupView);

        viewModel.getCallErrorLiveData().observe(getViewLifecycleOwner(), throwable -> {
            if (getArguments() != null) {
                viewModel.loadCall(getArguments().getInt(getString(R.string.keyCallId)));
            }
        });

        if (getArguments() != null) {
            viewModel.loadCall(getArguments().getInt(getString(R.string.keyCallId)));
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });

        binding.titlePatientStuff.setText(R.string.callInfo);

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
                    case 0: binding.titlePatientStuff.setText(R.string.callInfo);
                    break;
                    case 1: binding.titlePatientStuff.setText(R.string.inspection);
                    break;
                    case 2: binding.titlePatientStuff.setText(R.string.diagnosis);
                    break;
                    case 3: binding.titlePatientStuff.setText(R.string.recommendations);
                    break;
                    default: throw new IllegalStateException();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //
            }
        });
    }

    private void setupView(CallInfo callInfo) {
        adapter = new ViewPagerAdapter(CallStuffFragment.this, callInfo, getString(R.string.keyCallParcelable));
        binding.viewPager.setAdapter(adapter);
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
                tabText.setText(R.string.callInfo);
            }
            break;
            case 1: {
                tabIcon.setImageResource(R.drawable.ic_inspection);
                tabText.setText(R.string.inspection);
            }
            break;
            case 2: {
                tabIcon.setImageResource(R.drawable.ic_diagnosis);
                tabText.setText(R.string.diagnosis);
            }
            break;
            case 3: {
                tabIcon.setImageResource(R.drawable.ic_receipt);
                tabText.setText(R.string.recommendationsShort);
            }
            break;
            default: throw new IllegalStateException();
        }

        return tabView;
    }

    private void navigateToHome() {
        Bundle args = new Bundle();
        args.putBoolean(getString(R.string.keyUpdateList), true);
        Navigation.findNavController(requireView()).navigate(R.id.homeFragment, args);
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {

        private final CallInfo callInfo;
        private final String key;

        public ViewPagerAdapter(@NonNull Fragment fragment, CallInfo callInfo, String key) {
            super(fragment);
            this.callInfo = callInfo;
            this.key = key;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;
            switch (position) {
                case 0: fragment = new PatientCardFragment();
                break;
                case 1: fragment = new InspectionFragment();
                break;
                case 2: fragment = new DiagnosisFragment();
                break;
                case 3: fragment = new RecommendationsFragment();
                break;
                default: throw new IllegalStateException();
            }
            Bundle args = new Bundle();
            args.putParcelable(key, callInfo);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}