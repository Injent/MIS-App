package com.injent.miscalls.ui.protocoltemp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.injent.miscalls.R;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.databinding.FragmentProtocolEditBinding;
import com.injent.miscalls.domain.ProtocolTempRepository;

public class ProtocolEditFragment extends Fragment {

    private FragmentProtocolEditBinding binding;
    private ProtocolTempViewModel viewModel;
    private ProtocolTemp protocolTemp;

    @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_protocol_edit, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProtocolTempViewModel.class);

        viewModel.getSelectedProtocolLiveData().observe(this, new Observer<ProtocolTemp>() {
            @Override
            public void onChanged(ProtocolTemp protocolTemp) {
                ProtocolEditFragment.this.protocolTemp = protocolTemp;
                loadData();
            }
        });

        if (getArguments() != null)
            viewModel.getSelectedProtocol(getArguments().getInt("protocolId"));
        else back();

        binding.saveProtocolTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(),R.string.protocolTempSaved,Toast.LENGTH_SHORT).show();
                protocolTemp.setInspection(binding.editInspection.getText().toString());
                protocolTemp.setConclusion(binding.editConclusion.getText().toString());
                protocolTemp.setTreatment(binding.editTreatment.getText().toString());
                protocolTemp.setName(binding.protocolName.getText().toString());
                protocolTemp.setDescription(binding.protocolDesc.getText().toString());
                viewModel.saveSelectedProtocol(protocolTemp);
                back();
            }
        });

        binding.backFromProtocolTempEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmAction();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmAction();
            }
        });
    }

    private void loadData() {
        binding.protocolName.setText(protocolTemp.getName());
        binding.protocolDesc.setText(protocolTemp.getDescription());
        binding.editTreatment.setText(protocolTemp.getTreatment());
        binding.editInspection.setText(protocolTemp.getInspection());
        binding.editConclusion.setText(protocolTemp.getConclusion());
    }

    private void back() {
        Navigation.findNavController(requireView()).navigate(R.id.action_protocolEditFragment_to_protocolTempFragment);
    }

    private void confirmAction() {
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.exitFromEditing)
                .setMessage(R.string.dataWontSave)

                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        back();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }
}