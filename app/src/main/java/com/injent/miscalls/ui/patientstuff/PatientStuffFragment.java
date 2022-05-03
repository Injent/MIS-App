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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.injent.miscalls.R;
import com.injent.miscalls.data.Keys;
import com.injent.miscalls.databinding.FragmentPatientStuffBinding;
import com.injent.miscalls.ui.patientcard.PatientCardFragment;
import com.injent.miscalls.ui.protocol.ProtocolFragment;
import com.injent.miscalls.ui.protocoltemp.ProtocolTempFragment;

public class PatientStuffFragment extends Fragment {

    private FragmentPatientStuffBinding binding;
    private ViewPagerAdapter adapter;
    private int patientId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_patient_stuff,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null)
            patientId = getArguments().getInt(Keys.PATIENT_ID);

        adapter = new ViewPagerAdapter(this, patientId);
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                View child = binding.viewPager.getChildAt(0);
                if (child instanceof RecyclerView)
                    child.setOverScrollMode(View.OVER_SCROLL_NEVER);
            }
        });

        new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) -> tab.setCustomView(configureTab(position))).attach();

        binding.titlePatientStuff.setText(R.string.callInfo);

        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: binding.titlePatientStuff.setText(R.string.callInfo);
                    break;
                    case 1: binding.titlePatientStuff.setText(R.string.inspection);
                    break;
                    case 2: binding.titlePatientStuff.setText(R.string.recommendations);
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

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });
    }

    private View configureTab(int position) {
        @SuppressLint("InflateParams") View tabView = getLayoutInflater().inflate(R.layout.tab_view, null);
        ImageView tabIcon = tabView.findViewById(R.id.tabIcon);
        TextView tabText = tabView.findViewById(R.id.tabText);

        switch (position) {
            case 0: {
                tabIcon.setImageResource(R.drawable.ic_card);
                tabText.setText(R.string.callInfo);
            }
            break;
            case 1: {
                tabIcon.setImageResource(R.drawable.ic_temp_protocol);
                tabText.setText(R.string.inspection);
            }
            break;
            case 2: {
                tabIcon.setImageResource(R.drawable.ic_health_shield);
                tabText.setText(R.string.recommendationsShort);
            }
            break;
            default: throw new IllegalStateException();
        }

        return tabView;
    }

    private void navigateToHome() {
        Bundle args = new Bundle();
        args.putBoolean(Keys.UPDATE_LIST, true);
        Navigation.findNavController(requireView()).navigate(R.id.homeFragment, args);
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {

        private final int patientId;

        public ViewPagerAdapter(@NonNull Fragment fragment, int patientId) {
            super(fragment);
            this.patientId = patientId;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;
            switch (position) {
                case 0: {
                    fragment = new PatientCardFragment();
                }
                break;
                case 1: {
                    fragment = new ProtocolFragment();
                }
                break;
                case 2: fragment = new ProtocolTempFragment();
                break;
                default: throw new IllegalStateException();
            }
            Bundle args = new Bundle();
            args.putInt(Keys.PATIENT_ID, patientId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}