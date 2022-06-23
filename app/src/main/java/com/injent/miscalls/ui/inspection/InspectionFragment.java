package com.injent.miscalls.ui.inspection;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.calls.MedCall;
import com.injent.miscalls.data.database.registry.Objectively;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.databinding.FragmentInspectionBinding;
import com.injent.miscalls.ui.adapters.AdditionalField;
import com.injent.miscalls.ui.adapters.Field;
import com.injent.miscalls.ui.adapters.FieldAdapter;
import com.injent.miscalls.ui.adapters.ViewType;
import com.injent.miscalls.ui.callstuff.CallStuffViewModel;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;
import java.util.List;

public class InspectionFragment extends Fragment {

    private FragmentInspectionBinding binding;
    private CallStuffViewModel viewModel;
    private FieldAdapter adapter;

    public InspectionFragment(ViewModel viewModel) {
        this.viewModel = (CallStuffViewModel) viewModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inspection,
                container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getCallLiveData().observe(getViewLifecycleOwner(), this::setInfo);

        viewModel.getCurrentRegistryLiveData().observe(getViewLifecycleOwner(), this::setupRecyclerView);
    }

    @SuppressLint("SetTextI18n")
    private void setInfo(MedCall medCall) {
        binding.fullnameText.setText(medCall.getFullName());

    }

    private void setupRecyclerView(Registry registry) {
        Objectively obj;
        if (registry == null) {
            registry = new Registry();
            obj = new Objectively();
        } else {
            obj = registry.getObjectively();
        }

        adapter = new FieldAdapter((index, value) -> viewModel.setObjectivelyData(index, value));
                binding.fieldsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.fieldsRecyclerView.setItemAnimator(null);
        binding.fieldsRecyclerView.setAdapter(adapter);

        // Adding fields
        List<ViewType> items = new ArrayList<>();

        items.add(new AdditionalField(R.string.complaints,Field.COMPLAINTS,ViewType.FIELD_ADDITIONAL_TEXT, R.string.patientsComplaints, registry.getComplaints()));
        items.add(new AdditionalField(R.string.anamnesis, Field.ANAMNESIS, ViewType.FIELD_ADDITIONAL_TEXT, R.string.text, registry.getAnamnesis()));
        items.add(new AdditionalField(R.string.generalState, Field.GENERAL_STATE, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.stateTypes, obj.getGeneralState()));
        items.add(new AdditionalField(R.string.bodyBuild, Field.BODY_BUILD, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.bodyBuildTypes, obj.getBodyBuild()));
        items.add(new AdditionalField(R.string.skin, Field.SKIN, ViewType.FIELD_ADDITIONAL_TEXT, R.string.stateOfSkin, obj.getSkin()));
        items.add(new AdditionalField(R.string.nodes, Field.NODES, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.nodesTypes, obj.getNodes()));
        items.add(new AdditionalField(R.string.glands, Field.GLANDS, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.glandsTypes, obj.getGlands()));
        items.add(new AdditionalField(R.string.temperature, Field.TEMPERATURE, ViewType.FIELD_ADDITIONAL_DECIMAL, obj.getTemperature()));
        items.add(new AdditionalField(R.string.pharynx, Field.PHARYNX, ViewType.FIELD_ADDITIONAL_TEXT, R.string.text, obj.getPharynx()));
        items.add(new AdditionalField(R.string.breathing, Field.BREATHING, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.breathingTypes, obj.getBreathing()));
        items.add(new AdditionalField(R.string.arterialPressure, Field.ARTERIAL_PRESSURE, ViewType.FIELD_ADDITIONAL_DOUBLE_DECIMAL, obj.getArterialPressure()));
        items.add(new AdditionalField(R.string.pulse, Field.PULSE, ViewType.FIELD_ADDITIONAL_DECIMAL, obj.getPulse()));
        items.add(new AdditionalField(R.string.pensioner,Field.PENSIONER,ViewType.FIELD_ADDITIONAL_CHECKBOX, String.valueOf(obj.isPensioner())));
        items.add(new AdditionalField(R.string.sick,Field.SICK,ViewType.FIELD_ADDITIONAL_CHECKBOX, String.valueOf(obj.getSick())));
        items.add(new AdditionalField(R.string.heartTones, Field.ABDOMEN, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.heartTonesTypes, obj.getHeartTones()));
        items.add(new AdditionalField(R.string.abdomen, Field.ABDOMEN, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.abdomenTypes, obj.getAbdomen()));
        items.add(new AdditionalField(R.string.liver, Field.LIVER, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.liverTypes, obj.getLiver()));
        items.add(new AdditionalField(R.string.surveys, Field.SURVEYS, ViewType.FIELD_ADDITIONAL_TEXT, R.string.text, registry.getSurveys()));
        items.add(new AdditionalField(R.string.medicationTherapy, Field.MEDICAL_THERAPY, ViewType.FIELD_ADDITIONAL_TEXT, R.string.text, registry.getMedicalTherapy()));
        items.add(new AdditionalField(R.dimen.space));

        adapter.submitList(items);

        KeyboardVisibilityEvent.setEventListener(requireActivity(), getViewLifecycleOwner(),
                isOpen -> {
                    if (isOpen) return;
                    View focusedChild = binding.fieldsRecyclerView.getFocusedChild();
                    if (focusedChild != null) {
                        focusedChild.clearFocus();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
        binding = null;
    }
}