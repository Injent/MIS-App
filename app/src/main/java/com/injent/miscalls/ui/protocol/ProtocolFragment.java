package com.injent.miscalls.ui.protocol;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.databinding.FragmentProtocolBinding;
import com.injent.miscalls.domain.HomeRepository;
import com.injent.miscalls.domain.ProtocolTempRepository;
import com.injent.miscalls.ui.protocoltemp.ProtocolTempViewModel;

public class ProtocolFragment extends Fragment {

    private FragmentProtocolBinding binding;
    private ProtocolTempViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_protocol,
                container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProtocolTempViewModel.class);

        Patient patient = null;
        int protocolTempId = -1;

        if (getArguments() != null) {
            patient = new HomeRepository().getPatientById(getArguments().getInt("patientId"));
            protocolTempId = getArguments().getInt("protocolId", -1);
        }

        Patient finalPatient = patient;

        binding.fullnameText.setText(patient.getShortInfo() + " " + patient.getMiddleName());

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        confirmAction(finalPatient.getId());
                    }
        });

        binding.backFromProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmAction(finalPatient.getId());
            }
        });

        binding.applyTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToProtocolSelect(finalPatient.getId());
            }
        });

        //Observers
        viewModel.getAppliedProtocolLiveData().observe(this, new Observer<ProtocolTemp>() {
            @Override
            public void onChanged(ProtocolTemp protocolTemp) {
                applyProtocolTemp(protocolTemp);
            }
        });

        if (protocolTempId > -1)
            viewModel.applyProtocolTemp(new ProtocolTempRepository().getProtocolTempById(protocolTempId), finalPatient);
    }

    private void back(int patientId) {
        Bundle bundle = new Bundle();
        bundle.putInt("patientId", patientId);
        Navigation.findNavController(requireView()).navigate(R.id.action_protocolFragment_to_patientCardFragment, bundle);
    }

    private void navigateToProtocolSelect(int patientId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("editMode", false);
        bundle.putInt("patientId", patientId);
        Navigation.findNavController(requireView()).navigate(R.id.action_protocolFragment_to_protocolTempFragment, bundle);
    }

    private void applyProtocolTemp(ProtocolTemp protocolTemp) {
        binding.treatmentField.setText(protocolTemp.getTreatment());
        binding.conclusionField.setText(protocolTemp.getConclusion());
        binding.resultOfInspectionField.setText(protocolTemp.getInspection());

        Toast.makeText(requireContext(), getText(R.string.appliedTemp) + " \"" + protocolTemp.getName() + "\"", Toast.LENGTH_SHORT).show();
    }

    private void confirmAction(int patientId) {
        if (binding.treatmentField.getText().toString().isEmpty() && binding.resultOfInspectionField.getText().toString().isEmpty() && binding.conclusionField.getText().toString().isEmpty()) {
            back(patientId);
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.editFile)
                .setMessage(R.string.saveFile)

                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        back(patientId);
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