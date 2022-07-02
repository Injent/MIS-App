package com.injent.miscalls.ui.treatment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.databinding.FragmentTreatmentBinding;
import com.injent.miscalls.ui.adapters.FieldAdapter;
import com.injent.miscalls.ui.callstuff.CallStuffViewModel;
import com.injent.miscalls.ui.inspection.AdditionalField;
import com.injent.miscalls.ui.overview.Field;
import com.injent.miscalls.util.ViewType;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;
import java.util.List;

public class TreatmentFragment extends Fragment {

    private FragmentTreatmentBinding binding;
    private CallStuffViewModel viewModel;
    private FieldAdapter adapter;

    public TreatmentFragment(ViewModel viewModel) {
        this.viewModel = (CallStuffViewModel) viewModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_treatment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListeners();
    }

    private void setListeners() {
        // Listeners
        binding.treatmentDone.setOnClickListener(view -> viewModel.saveRegistry());

        // Observers
        viewModel.getCurrentRegistryLiveData().observe(getViewLifecycleOwner(), this::setupRecyclerView);
    }

    private void setupRecyclerView(Registry registry) {
        adapter = new FieldAdapter((index, value) -> viewModel.setObjectivelyData(index, value));
        binding.treatmentFieldsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.treatmentFieldsRecycler.setItemAnimator(null);
        binding.treatmentFieldsRecycler.setAdapter(adapter);

        if (registry == null)
            registry = new Registry();

        List<ViewType> items = new ArrayList<>();
        items.add(new AdditionalField(R.string.surveys, Field.SURVEYS, ViewType.FIELD_ADDITIONAL_TEXT, R.string.text, registry.getSurveys()));
        items.add(new AdditionalField(R.string.medicationTherapy, Field.MEDICATION_THERAPY, ViewType.FIELD_ADDITIONAL_TEXT, R.string.text, registry.getMedicationTherapy()));
        items.add(new AdditionalField(R.string.recommendations, Field.RECOMMENDATIONS, ViewType.FIELD_ADDITIONAL_TEXT, R.string.text, registry.getRecommendation()));
        items.add(new AdditionalField(R.dimen.space));

        adapter.submitList(items);

        KeyboardVisibilityEvent.setEventListener(requireActivity(), getViewLifecycleOwner(),
                isOpen -> {
                    if (isOpen) return;
                    View focusedChild = binding.treatmentFieldsRecycler.getFocusedChild();
                    if (focusedChild != null) {
                        focusedChild.clearFocus();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        adapter = null;
    }
}