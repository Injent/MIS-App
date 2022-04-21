package com.injent.miscalls.ui.protocol;

import android.annotation.SuppressLint;
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
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.databinding.FragmentProtocolBinding;
import com.injent.miscalls.domain.HomeRepository;
import com.injent.miscalls.domain.ProtocolRepository;
import com.injent.miscalls.domain.ProtocolTempRepository;

public class ProtocolFragment extends Fragment {

    private FragmentProtocolBinding binding;

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
                        back(finalPatient.getId());
                    }
        });

        binding.backFromProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back(finalPatient.getId());
            }
        });

        binding.applyTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToProtocolSelect(finalPatient.getId());
            }
        });

        if (protocolTempId > -1)
            applyProtocolTemp(protocolTempId);
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

    private void applyProtocolTemp(int protocolTempId) {
        ProtocolTemp protocolTemp = new ProtocolTempRepository().getProtocolTempById(protocolTempId);

        binding.treatmentField.setText(protocolTemp.getTreatment());
        binding.conclusionField.setText(protocolTemp.getConclusion());
        binding.resultOfInspectionField.setText(protocolTemp.getInspection());

        Toast.makeText(requireContext(), getText(R.string.applyTemp) + " " + protocolTemp.getName(), Toast.LENGTH_SHORT).show();
    }
}