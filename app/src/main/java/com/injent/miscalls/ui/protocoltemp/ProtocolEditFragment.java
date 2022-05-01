package com.injent.miscalls.ui.protocoltemp;

import android.app.AlertDialog;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.injent.miscalls.R;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.databinding.FragmentProtocolEditBinding;
import com.injent.miscalls.data.Keys;
import com.injent.miscalls.domain.repositories.ProtocolTempRepository;

public class ProtocolEditFragment extends Fragment {

    private FragmentProtocolEditBinding binding;
    private ProtocolTempViewModel viewModel;
    private ProtocolTemp protocolTemp;
    private int protocolId;
    private boolean newProtocol;

    @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_protocol_edit, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProtocolTempViewModel.class);

        if (getArguments() != null) {
            newProtocol = getArguments().getBoolean(Keys.NEW_PROTOCOL);
            protocolId = getArguments().getInt(Keys.PROTOCOL_ID);
            if (newProtocol)
                protocolTemp = new ProtocolTemp();
            else
                protocolTemp = viewModel.getSelectedProtocol(protocolId);
        } else {
            back();
            return;
        }

        //Listeners
        binding.saveProtocolTemp.setOnClickListener(view0 -> saveProtocolTemp());

        binding.backFromProtocolTempEdit.setOnClickListener(view1 -> confirmExit());

        if (newProtocol) {
            hideDeleteButton();
        } else {
            binding.protocolDelete.setOnClickListener(view2 -> confirmDelete());
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmExit();
            }
        });

        //Observers
        viewModel.getSelectedProtocolLiveData().observe(this, protocolTemp0 -> {
            protocolTemp = protocolTemp0;
            loadData();
        });
    }

    private void hideDeleteButton() {
        binding.protocolDelete.setVisibility(View.INVISIBLE);
        binding.protocolDelete.setEnabled(false);
    }

    private void loadData() {
        if (newProtocol)
            return;
        binding.protocolName.setText(protocolTemp.getName());
        binding.protocolDesc.setText(protocolTemp.getDescription());
        binding.editTreatment.setText(protocolTemp.getTreatment());
        binding.editInspection.setText(protocolTemp.getInspection());
        binding.editConclusion.setText(protocolTemp.getConclusion());
    }

    private void back() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Keys.MODE_EDIT, true);
        Navigation.findNavController(requireView()).navigate(R.id.action_protocolEditFragment_to_protocolTempFragment, bundle);
    }

    private void confirmExit() {
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.editFile) + " " + getProtocolName())
                .setMessage(R.string.saveFile)

                .setPositiveButton(R.string.yes, (dialog, button0) -> saveProtocolTemp())
                .setNegativeButton(R.string.no, (dialog, button1) -> back())
                .create();
        alertDialog.show();
    }

    private void confirmDelete() {
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle(String.format(getString(R.string.deleteProtocol),getProtocolName()))
                .setMessage(R.string.dataWillBeDeletedForever)

                .setPositiveButton(R.string.cancel, (dialog, button2) -> dialog.dismiss())
                .setNegativeButton(R.string.delete, (dialog, button3) -> {
                    deleteProtocol();
                    back();
                })
                .create();
        alertDialog.show();
    }

    private void saveProtocolTemp() {
        Toast.makeText(requireContext(),String.format(getString(R.string.protocolTempSaved),getProtocolName()),Toast.LENGTH_SHORT).show();
        protocolTemp.setId(protocolId);
        protocolTemp.setInspection(binding.editInspection.getText().toString());
        protocolTemp.setConclusion(binding.editConclusion.getText().toString());
        protocolTemp.setTreatment(binding.editTreatment.getText().toString());
        protocolTemp.setName(binding.protocolName.getText().toString());
        protocolTemp.setDescription(binding.protocolDesc.getText().toString());

        protocolTemp.setName(getProtocolName());
        viewModel.saveSelectedProtocol(protocolTemp);
        back();
    }

    private void deleteProtocol() {
        new ProtocolTempRepository().deleteProtocolTemp(protocolId);
        Toast.makeText(requireContext(),String.format(getString(R.string.protocolDeleted), getProtocolName()),Toast.LENGTH_SHORT).show();
    }

    private String getProtocolName() {
        String fileName = binding.protocolName.getText().toString();
        if (fileName.isEmpty()) {
            fileName = getString(R.string.noName) + " " + protocolId;
        }
        return fileName;
    }
}