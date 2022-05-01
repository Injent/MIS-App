package com.injent.miscalls.ui.savedprotocols;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.injent.miscalls.MainActivity;
import com.injent.miscalls.R;
import com.injent.miscalls.databinding.FragmentSavedProtocolsBinding;
import com.injent.miscalls.ui.Section;
import com.injent.miscalls.ui.SectionAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SavedProtocolsFragment extends Fragment {

    private SavedProtocolAdapter protocolAdapter;
    private SectionAdapter sectionAdapter;
    private SavedProtocolsViewModel viewModel;
    private FragmentSavedProtocolsBinding binding;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_saved_protocols, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SavedProtocolsViewModel.class);
        navController = Navigation.findNavController(requireView());
        MainActivity.getInstance().disableFullScreen();


        //Observers
        viewModel.getProtocolsLiveData().observe(this, protocols -> protocolAdapter.submitList(protocols));

        //RecyclerView
        setupProtocolRecyclerView();
        setupSectionRecyclerView();

        //Listeners
        binding.backFromSavedProtocols.setOnClickListener(view0 -> navigateToHome());
    }

    private void undoDeletingSnackbar() {
        @SuppressLint("ShowToast")
        Snackbar snackbar = Snackbar.make(requireView(),R.string.protocolDeleted, BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setAction(R.string.undoDeleting, view0 -> {

        });
        snackbar.show();
    }

    private void setupProtocolRecyclerView() {
        protocolAdapter = new SavedProtocolAdapter(this::navigateToProtocolEditing);

        binding.protocolRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.protocolRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.protocolRecyclerView.setAdapter(protocolAdapter);

        viewModel.loadProtocols();
    }

    private void setupSectionRecyclerView() {
        sectionAdapter = new SectionAdapter(type -> {

        });

        binding.protocolSectionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.protocolSectionsRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.protocolSectionsRecyclerView.setAdapter(sectionAdapter);

        List<Section> sectionList = new ArrayList<>();
        Section section = new Section();
        section.setSectionName("Отправить все");
        section.setSectionType(Section.Type.SUBMIT_ALL);
        sectionList.add(section);
        sectionAdapter.submitList(sectionList);
    }

    private void navigateToHome() {
        if (notMatchingDestination()) return;
        Bundle bundle = new Bundle();
        bundle.putBoolean("updateList", true);
        navController.navigate(R.id.homeFragment, bundle);
    }

    private void navigateToProtocolEditing(int protocolId) {
        if (notMatchingDestination()) return;
        Bundle bundle = new Bundle();
        bundle.putInt("protocolId", protocolId);
        navController.navigate(R.id.protocolEditFragment, bundle);
    }

    private boolean notMatchingDestination() {
        return Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.savedProtocolsFragment;
    }
}