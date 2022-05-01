package com.injent.miscalls.ui.protocol;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.savedprotocols.Protocol;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.databinding.FragmentProtocolBinding;
import com.injent.miscalls.domain.repositories.HomeRepository;
import com.injent.miscalls.data.Keys;
import com.injent.miscalls.domain.ProtocolFletcher;
import com.injent.miscalls.domain.repositories.ProtocolRepository;
import com.injent.miscalls.domain.repositories.ProtocolTempRepository;
import com.injent.miscalls.ui.protocoltemp.ProtocolTempViewModel;

public class ProtocolFragment extends Fragment {

    private FragmentProtocolBinding binding;
    private ProtocolTempViewModel protocolTempViewModel;
    private Patient patient;
    private NavController navController;

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

        protocolTempViewModel = new ViewModelProvider(this).get(ProtocolTempViewModel.class);
        navController = Navigation.findNavController(requireView());

        int protocolTempId;

        if (getArguments() != null) {
            patient = new HomeRepository().getPatientById(getArguments().getInt(Keys.PATIENT_ID));
            protocolTempId = getArguments().getInt(Keys.PROTOCOL_ID, -1);
        } else {
            back();
            return;
        }

        binding.fullnameText.setText(patient.getShortInfo() + " " + patient.getMiddleName());

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        confirmAction();
                    }
        });

        //Listeners
        binding.backFromProtocol.setOnClickListener(view0 -> confirmAction());

        binding.applyTemp.setOnClickListener(view1 -> navigateToProtocolSelect());

        binding.saveProtocolButton.setOnClickListener(view2 -> saveProtocol(protocolFieldsToProtocol()));

        binding.saveAsPdfCard.setOnClickListener(view3 -> generatePdf());

        //Observers
        protocolTempViewModel.getAppliedProtocolLiveData().observe(this, protocolTemp -> {
            applyProtocolTemp(protocolTemp);
            Log.e("A","AX");
        });

        if (protocolTempId > -1) {
            protocolTempViewModel.applyProtocolTemp(new ProtocolTempRepository().getProtocolTempById(protocolTempId), patient);
        }

    }

    private void back() {
        Bundle bundle = new Bundle();
        bundle.putInt(Keys.PATIENT_ID, patient.getId());
        navController.navigate(R.id.action_protocolFragment_to_patientCardFragment, bundle);
    }

    private void navigateToProtocolSelect() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Keys.MODE_EDIT, false);
        bundle.putInt(Keys.PATIENT_ID, patient.getId());
        navController.navigate(R.id.action_protocolFragment_to_protocolTempFragment, bundle);
    }

    private void applyProtocolTemp(ProtocolTemp protocolTemp) {
        binding.treatmentField.setText(protocolTemp.getTreatment());
        binding.conclusionField.setText(protocolTemp.getConclusion());
        binding.resultOfInspectionField.setText(protocolTemp.getInspection());
        Toast.makeText(requireContext(), getText(R.string.appliedTemp) + " \"" + protocolTemp.getName() + "\"", Toast.LENGTH_SHORT).show();
    }

    private void confirmAction() {
        if (binding.treatmentField.getText().toString().isEmpty() && binding.resultOfInspectionField.getText().toString().isEmpty() && binding.conclusionField.getText().toString().isEmpty()) {
            back();
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.exitFromEditing)
                .setMessage(R.string.protocolWontSave)
                .setPositiveButton(R.string.yes, (dialog, whichButton) -> back())
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    private void saveProtocol(Protocol protocol) {
        ProtocolRepository repository = new ProtocolRepository();

        Protocol duplicateProtocol = repository.getProtocolById(patient.getId());
        if (duplicateProtocol != null) {
            protocol.setId(duplicateProtocol.getId());
        }

        repository.insertProtocol(protocol);
        back();
    }

    private Protocol protocolFieldsToProtocol() {
        Protocol protocol = new Protocol();
        protocol.setName(binding.fullnameText.getText().toString());
        protocol.setDescription("");
        ProtocolTemp protocolTemp = protocolTempViewModel.getAppliedProtocolLiveData().getValue();
        if (protocolTemp != null)
            protocol.setDescription(protocolTemp.getDescription());
        protocol.setInspection(binding.resultOfInspectionField.getText().toString());
        protocol.setTreatment(binding.treatmentField.getText().toString());
        protocol.setConclusion(binding.conclusionField.getText().toString());
        return protocol;
    }

    private void generatePdf() {
        new ProtocolFletcher().fletchPdfFile(requireContext(), protocolFieldsToProtocol(), App.getUser(), patient);
        Toast.makeText(requireContext(),"Генерация",Toast.LENGTH_SHORT).show();
    }
}