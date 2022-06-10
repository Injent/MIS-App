package com.injent.miscalls.ui.recommendations;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentRecommendationsBinding;
import com.injent.miscalls.ui.callstuff.CallStuffViewModel;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

public class RecommendationsFragment extends Fragment {

    private FragmentRecommendationsBinding binding;
    private CallStuffViewModel viewModel;
    private RecommendationAdapter adapter;
    private TextWatcher textWatcher;

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
        setListeners();

        viewModel.getRecommendationsListLiveData().observe(getViewLifecycleOwner(), list -> adapter.submitList(list, true));

        viewModel.getCurrentRecommendationLiveData().observe(getViewLifecycleOwner(), s -> {
            binding.recommendationsText.removeTextChangedListener(textWatcher);
            binding.recommendationsText.setText(s);
            binding.recommendationsText.addTextChangedListener(textWatcher);
            setSelectMode(false);
        });

        binding.map.setTileSource(TileSourceFactory.MAPNIK);
    }

    private void setupRecyclerView() {
        adapter = new RecommendationAdapter(recommendationId -> viewModel.findRecommendation(recommendationId));
        binding.recommendationRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recommendationRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.recommendationRecycler.setAdapter(adapter);
        binding.recommendationRecycler.setItemAnimator(null);
        viewModel.loadRecommendationsList();
    }

    private void setListeners() {
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

        binding.searchRecommendations.setOnFocusChangeListener((view, focused) -> {
            setSelectMode(focused);
        });

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setCurrentRecommendation(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Nothing to do
            }
        };

        binding.recommendationsText.addTextChangedListener(textWatcher);

        binding.recSearchCancel.setOnClickListener(view -> setSelectMode(false));
    }

    private void setSelectMode(boolean visible) {
        if (visible) {
            binding.recSearchCancel.setVisibility(View.VISIBLE);
            binding.recommendationRecycler.setVisibility(View.VISIBLE);
            binding.recommendationsText.setVisibility(View.GONE);
            binding.recHeader.setVisibility(View.GONE);
        }
        else {
            binding.recSearchCancel.setVisibility(View.GONE);
            binding.recommendationRecycler.setVisibility(View.GONE);
            binding.recommendationsText.setVisibility(View.VISIBLE);
            binding.recHeader.setVisibility(View.VISIBLE);
            binding.searchRecommendations.clearFocus();
            App.hideKeyBoard(requireView());
            Toast.makeText(requireContext(),R.string.recommendationSelected, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
