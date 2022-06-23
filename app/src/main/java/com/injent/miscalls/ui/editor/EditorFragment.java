package com.injent.miscalls.ui.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.databinding.FragmentEditorBinding;
import com.injent.miscalls.ui.callinfo.InfoAdapter;
import com.injent.miscalls.ui.overview.OverviewViewModel;

public class EditorFragment extends Fragment {

    private FragmentEditorBinding binding;
    private OverviewViewModel viewModel;

    private InfoAdapter adapter;

    public EditorFragment(ViewModel viewModel) {
        this.viewModel = (OverviewViewModel) viewModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_editor, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListeners();
    }

    private void setListeners() {
        // Listeners
        binding.editorSaveDoc.setOnClickListener(v -> sendRegistry());
        binding.editorCard.setOnClickListener(view -> viewModel.setClickPreviewPdf(true));

        // Observers
        viewModel.getSelectedRegistryLiveData().observe(getViewLifecycleOwner(), this::loadRegistryData);
    }

    private void loadRegistryData(Registry registry) {
        if (registry == null) return;
        setupRecyclerView(registry);
        binding.editorCard.setEnabled(true);
    }

    private void setupRecyclerView(Registry registry) {
        adapter = new InfoAdapter();
        binding.extraInfoRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.extraInfoRecyclerView.setItemAnimator(null);
        binding.extraInfoRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.registry_info_divider));
        binding.extraInfoRecyclerView.addItemDecoration(dividerItemDecoration);
        binding.extraInfoRecyclerView.setAdapter(adapter);
        adapter.submitList(registry.getData());
    }

    private void sendRegistry() {
        Toast.makeText(requireContext(), R.string.documentSending, Toast.LENGTH_SHORT).show();
        viewModel.sendDocument(() -> {
            Toast.makeText(requireContext(), R.string.docsSent, Toast.LENGTH_SHORT).show();
            return null;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}