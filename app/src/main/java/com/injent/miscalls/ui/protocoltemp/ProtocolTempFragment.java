package com.injent.miscalls.ui.protocoltemp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentProtocolTempBinding;

public class ProtocolTempFragment extends Fragment {

    private ProtocolTempViewModel protocolTempViewModel;
    private FragmentProtocolTempBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,
                container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        protocolTempViewModel = new ViewModelProvider(this).get(ProtocolTempViewModel.class);


    }
}