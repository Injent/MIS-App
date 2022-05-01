package com.injent.miscalls.ui.patientcard;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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

import com.injent.miscalls.MainActivity;
import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.databinding.FragmentPatientCardBinding;
import com.injent.miscalls.data.Keys;

public class PatientCardFragment extends Fragment {

    private FragmentPatientCardBinding binding;
    private NavController navController;
    private PatientCardViewModel viewModel;

    public PatientCardFragment() {
        //Need for working
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_patient_card,
                container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PatientCardViewModel.class);
        binding.complaintField.setMovementMethod(new ScrollingMovementMethod());
        navController = Navigation.findNavController(requireView());

        MainActivity.getInstance().disableFullScreen();

        InfoAdapter adapter = new InfoAdapter(getResources().getStringArray(R.array.fieldTypes));
        binding.infoList.setAdapter(adapter);

        binding.backFromCard.setOnClickListener(view0 -> back());

        //On back pressed action
        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        back();
                    }
        });

        //Observers
        viewModel.getPatientLiveData().observe(this, patient -> {
            adapter.submitList(patient.getData());
            setInfo(patient);
        });

        viewModel.getPatient(getArguments() != null ? getArguments().getInt(Keys.PATIENT_ID, 0) : 0);
    }

    private void back() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Keys.UPDATE_LIST,true);
        navController.navigate(R.id.action_patientCardFragment_to_homeFragment, bundle);
    }

    private void navigateToProtocol(int patientId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Keys.PATIENT_ID, patientId);
        navController.navigate(R.id.action_patientCardFragment_to_protocolFragment, bundle);
    }

    private void copyText(String text) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("clip", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext(), R.string.textCopied,Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void setInfo(Patient patient) {
        binding.cardNumber.setText("№" + patient.getCardNumber());
        binding.complaintField.setText(patient.getComplaints());
        //Listeners
        binding.protocolButton.setOnClickListener(view0 -> navigateToProtocol(patient.getId()));

        binding.copyComplaints.setOnClickListener(view1 -> copyText(patient.getComplaints()));
    }
}
