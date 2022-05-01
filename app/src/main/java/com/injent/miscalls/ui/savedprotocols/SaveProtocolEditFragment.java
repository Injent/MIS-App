package com.injent.miscalls.ui.savedprotocols;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.injent.miscalls.R;
import com.injent.miscalls.data.savedprotocols.Protocol;
import com.injent.miscalls.databinding.SaveProtocolEditFragmentBinding;

public class SaveProtocolEditFragment extends Fragment {

    private SaveProtocolEditViewModel viewModel;
    private SaveProtocolEditFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.save_protocol_edit_fragment,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SaveProtocolEditViewModel.class);

        //Observers
        viewModel.getProtocolLiveData().observe(getViewLifecycleOwner(), this::setFieldsData);
    }

    private void setFieldsData(Protocol protocol) {

    }
}