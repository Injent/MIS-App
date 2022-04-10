package com.injent.miscalls.ui.patientcard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.databinding.FragmentPatientCardBinding;

import java.util.Arrays;

public class PatientCardFragment extends Fragment {

    private FragmentPatientCardBinding binding;

    public PatientCardFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_patient_card,
                container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.complaintField.setMovementMethod(new ScrollingMovementMethod());

        Patient patient = getArguments().getParcelable("patient");

        InfoAdapter adapter = new InfoAdapter(patient,
                Arrays.asList(getResources().getStringArray(R.array.fieldTypes)));

        binding.infoList.setAdapter(adapter);

        binding.cardNumber.setText("№" + patient.cardNumber);
        binding.complaintField.setText(patient.complaints);


        //Navigation
        TextView back = binding.backHomeFromPatientCard;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        back();
                    }
                });

        binding.protocolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("patient",patient);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_patientCardFragment_to_protocolFragment,bundle);
            }
        });
    }

    private void back() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("updateList",true);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_patientCardFragment_to_homeFragment, bundle);
    }
}
