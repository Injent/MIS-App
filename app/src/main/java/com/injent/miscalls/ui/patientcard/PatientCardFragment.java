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

import com.injent.miscalls.App;
import com.injent.miscalls.MainActivity;
import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.databinding.FragmentPatientCardBinding;
import com.injent.miscalls.domain.HomeRepository;

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

        MainActivity.getInstance().disableFullScreen();

        Patient patient = new HomeRepository().getPatientById(getArguments().getInt("patientId",0));

        InfoAdapter adapter = new InfoAdapter(getResources().getStringArray(R.array.fieldTypes));
        adapter.submitList(patient.getData());
        binding.infoList.setAdapter(adapter);

        binding.cardNumber.setText("№" + patient.getCardNumber());
        binding.complaintField.setText(patient.getComplaints());


        //Listeners
        binding.protocolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToProtocol(patient.getId());
            }
        });

        binding.backFromCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        //On back pressed action
        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        back();
                    }
        });
    }

    private void back() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("updateList",true);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_patientCardFragment_to_homeFragment, bundle);
    }

    private void navigateToProtocol(int patientId) {
        Bundle bundle = new Bundle();
        bundle.putInt("patientId", patientId);
        Navigation.findNavController(requireView()).navigate(R.id.action_patientCardFragment_to_protocolFragment, bundle);
    }
}
