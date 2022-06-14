package com.injent.miscalls.ui.inspection;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.calls.CallInfo;
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

        viewModel = new ViewModelProvider(requireActivity()).get(CallStuffViewModel.class);

        viewModel.getCallLiveData().observe(getViewLifecycleOwner(), this::setInfo);

        setupRecyclerView();
    }

    @SuppressLint("SetTextI18n")
    private void setInfo(CallInfo callInfo) {
        binding.fullnameText.setText(callInfo.getFullName());
    }

    private void setupRecyclerView() {
        adapter = new FieldAdapter((index, value) -> viewModel.setObjectivelyData(index, value));
                binding.fieldsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.fieldsRecyclerView.setItemAnimator(null);
        binding.fieldsRecyclerView.setAdapter(adapter);

        // Adding fields
        List<ViewType> items = new ArrayList<>();

        items.add(new AdditionalField(R.string.complaints,Field.COMPLAINTS,ViewType.FIELD_ADDITIONAL_TEXT, R.string.patientsComplaints));
        items.add(new AdditionalField(R.string.anamnesis, Field.ANAMNESIS, ViewType.FIELD_ADDITIONAL_TEXT, R.string.loading));
        items.add(new AdditionalField(R.string.generalState, Field.GENERAL_STATE, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.stateTypes));
        items.add(new AdditionalField(R.string.bodyBuild, Field.BODY_BUILD, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.bodyBuildTypes));
        items.add(new AdditionalField(R.string.skin, Field.SKIN, ViewType.FIELD_ADDITIONAL_TEXT, R.string.stateOfSkin));
        items.add(new AdditionalField(R.string.nodes, Field.NODES, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.nodesTypes));
        items.add(new AdditionalField(R.string.glands, Field.GLANDS, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.glandsTypes));
        items.add(new AdditionalField(R.string.temperature, Field.TEMPERATURE, ViewType.FIELD_ADDITIONAL_DECIMAL));
        items.add(new AdditionalField(R.string.pharynx, Field.PHARYNX, ViewType.FIELD_ADDITIONAL_TEXT, R.string.loading));
        items.add(new AdditionalField(R.string.breathing, Field.BREATHING, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.breathingTypes));
        items.add(new AdditionalField(R.string.arterialPressure, Field.ARTERIAL_PRESSURE, ViewType.FIELD_ADDITIONAL_DOUBLE_DECIMAL));
        items.add(new AdditionalField(R.string.pulse, Field.PULSE, ViewType.FIELD_ADDITIONAL_DECIMAL));
        items.add(new AdditionalField(R.string.pensioner,Field.PENSIONER,ViewType.FIELD_ADDITIONAL_CHECKBOX));
        items.add(new AdditionalField(R.string.sick,Field.SICK,ViewType.FIELD_ADDITIONAL_CHECKBOX));
        items.add(new AdditionalField(R.string.heartTones, Field.ABDOMEN, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.heartTonesTypes));
        items.add(new AdditionalField(R.string.abdomen, Field.ABDOMEN, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.abdomenTypes));
        items.add(new AdditionalField(R.string.liver, Field.LIVER, ViewType.FIELD_ADDITIONAL_SPINNER, R.array.liverTypes));
        items.add(new AdditionalField(R.dimen.space));

        adapter.submitList(items);
        KeyboardVisibilityEvent.setEventListener(
                requireActivity(),
                getViewLifecycleOwner(),
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
        binding = null;
    }
}