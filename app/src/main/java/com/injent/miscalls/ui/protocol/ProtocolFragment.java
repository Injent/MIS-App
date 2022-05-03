package com.injent.miscalls.ui.protocol;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.Keys;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.savedprotocols.Protocol;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.databinding.FragmentProtocolBinding;
import com.injent.miscalls.domain.ProtocolFletcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProtocolFragment extends Fragment {

    private FragmentProtocolBinding binding;
    private ProtocolViewModel viewModel;
    private Patient patient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_protocol,
                container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProtocolViewModel.class);

        //Listeners

        //Observers
        viewModel.getPatientLiveData().observe(getViewLifecycleOwner(), receivedPatient -> {
            if (getArguments() != null && getArguments().getInt(Keys.PROTOCOL_ID, -1) != -1)
                viewModel.loadProtocolTemp(getArguments().getInt(Keys.PROTOCOL_ID), patient);
            setInfo(receivedPatient);
        });

        if (getArguments() != null && getArguments().getInt(Keys.PATIENT_ID, -1) != -1)
            viewModel.loadPatient(getArguments().getInt(Keys.PATIENT_ID));

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        FieldAdapter fieldAdapter = new FieldAdapter();
        List<Field> fieldList = new ArrayList<>();
        String[] names = getResources().getStringArray(R.array.fieldType);
        String[] hints = getResources().getStringArray(R.array.fieldHint);
        for (int i = 0; i < names.length; i++) {
            fieldList.add(new Field(names[i], hints[i]));
        }
        binding.fieldRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.fieldRecyclerView.setAdapter(fieldAdapter);
        fieldAdapter.submitList(fieldList);
    }

    private void saveProtocol(Protocol protocol) {
        viewModel.saveProtocol(protocol, patient);
    }

    private Protocol protocolFieldsToProtocol() {
        return null;
    }

    private void generatePdf() {
        try {
            new ProtocolFletcher(requireContext(),patient,App.getUser()).fletchPdfFile(protocolFieldsToProtocol());
            Toast.makeText(requireContext(),getText(R.string.pdfGeneration),Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(),getText(R.string.pdfGenerationFailed),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setInfo(Patient patient) {
        this.patient = patient;
        binding.fullnameText.setText(patient.getShortInfo() + " " + patient.getMiddleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}