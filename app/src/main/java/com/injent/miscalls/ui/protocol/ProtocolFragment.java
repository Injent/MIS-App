package com.injent.miscalls.ui.protocol;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.databinding.FragmentProtocolBinding;

public class ProtocolFragment extends Fragment {

    private FragmentProtocolBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_protocol,
                container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Patient patient = null;

        if (getArguments() != null) {
            patient = getArguments().getParcelable("patient");
        }

        Patient finalPatient = patient;

        binding.fullnameText.setText(patient.getShortInfo());

        binding.backToPatientCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back(finalPatient);
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        back(finalPatient);
                    }
                });
    }

    private void back(Patient patient) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("patient", patient);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_protocolFragment_to_patientCardFragment, bundle);
    }
}