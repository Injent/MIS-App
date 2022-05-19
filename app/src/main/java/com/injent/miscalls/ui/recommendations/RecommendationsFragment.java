package com.injent.miscalls.ui.recommendations;

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
import com.injent.miscalls.databinding.FragmentRecommendationsBinding;
import com.injent.miscalls.ui.callstuff.CallStuffViewModel;

public class RecommendationsFragment extends Fragment {

    private FragmentRecommendationsBinding binding;
    private CallStuffViewModel viewModel;
    private RecommendationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recommendations,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CallStuffViewModel.class);

        setupRecyclerView();

        binding.searchRecommendations.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Nothing to do
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new RecommendationAdapter(recommendationId -> viewModel.setCurrentRecommendation(recommendationId));
        binding.recommendationRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recommendationRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.recommendationRecycler.setAdapter(adapter);
        binding.recommendationRecycler.setItemAnimator(null);
    }
}
