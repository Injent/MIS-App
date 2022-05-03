package com.injent.miscalls.ui.protocoltemp;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.App;
import com.injent.miscalls.MainActivity;
import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.FailedDownloadDb;
import com.injent.miscalls.data.patientlist.ListEmptyException;
import com.injent.miscalls.databinding.FragmentProtocolTempBinding;
import com.injent.miscalls.data.Keys;
import com.injent.miscalls.ui.protocol.ProtocolFragment;

public class ProtocolTempFragment extends Fragment {

    private ProtocolTempViewModel viewModel;
    private FragmentProtocolTempBinding binding;
    private ProtocolTempAdapter adapter;
    private NavController navController;
    private boolean editMode;
    private int patientId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_protocol_temp, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProtocolTempViewModel.class);

        MainActivity.getInstance().disableFullScreen();

        Log.e("a","YGY");

        if (getArguments() != null) {
            editMode = getArguments().getBoolean(Keys.MODE_EDIT);
            patientId = getArguments().getInt(Keys.PATIENT_ID);
        }

        if (!editMode) {
            binding.titleLayout.setVisibility(View.GONE);
        } else {
            navController = Navigation.findNavController(requireView());
            requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    navigateToHome();
                }
            });
        }

        binding.protocolRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ProtocolTempAdapter(protocolId -> {
            if (editMode)
                navigateToProtocolEdit(protocolId, false);
            else
                navigateToProtocol(protocolId);
        }, editMode);

        binding.protocolRecycler.setAdapter(adapter);

        //Observers
        viewModel.getProtocolTempsLiveDate().observe(this, protocolTemps -> adapter.submitList(protocolTemps, true));

        viewModel.getErrorLiveData().observe(this, this::displayError);

        createSearchView();

        //Listeners
        binding.protocolDownloadAction.setOnClickListener(view0 -> {
            viewModel.downloadProtocolTemps(App.getUser().getToken());
            hideProtocolThings();
        });

        binding.addProtocolTemp.setOnClickListener(view1 -> {
            int itemCount = 0;
            if (binding.protocolRecycler.getAdapter() != null)
                itemCount = binding.protocolRecycler.getAdapter().getItemCount();
            navigateToProtocolEdit(itemCount, true);
        });

        if (editMode) {
            binding.backFromProtocolTemps.setOnClickListener(view1 -> navigateToHome());
        } else {
            binding.backFromProtocolTemps.setOnClickListener(view2 -> navigateToProtocol());
        }

        viewModel.loadProtocolTemps();
    }

    private void displayError(Throwable t) {
        if (t instanceof ListEmptyException) {
            showProtocolThings();
        } else if (t instanceof FailedDownloadDb) {
            showError(getText(R.string.serverError));
        } else if (t instanceof NetworkErrorException) {
            showError(getText(R.string.noInternetConnection));
        } else {
            showError(getText(R.string.unknownError));
        }
    }

    private void navigateToProtocolEdit(int protocolId, boolean newProtocol) {
        Bundle bundle = new Bundle();
        bundle.putInt(Keys.PROTOCOL_ID, protocolId);
        bundle.putBoolean(Keys.NEW_PROTOCOL, newProtocol);
        navController.navigate(R.id.action_protocolTempFragment_to_protocolEditFragment, bundle);
    }

    private void navigateToHome() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Keys.UPDATE_LIST, true);
        navController.navigate(R.id.homeFragment, bundle);
    }

    private void navigateToProtocol(int protocolId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Keys.PROTOCOL_ID, protocolId);
        bundle.putInt(Keys.PATIENT_ID, patientId);
        navController.navigate(R.id.action_protocolTempFragment_to_protocolFragment, bundle);
    }

    private void navigateToProtocol() {
        Bundle bundle = new Bundle();
        bundle.putInt(Keys.PATIENT_ID, patientId);
        navController.navigate(R.id.action_protocolTempFragment_to_protocolFragment, bundle);
    }

    private void showProtocolThings() {
        binding.protocolRecycler.setVisibility(View.GONE);
        hideProgress();
        binding.protocolTextThings.setVisibility(View.VISIBLE);
        binding.protocolDownloadAction.setEnabled(true);
    }

    private void hideProtocolThings() {
        binding.protocolRecycler.setVisibility(View.VISIBLE);
        binding.protocolTextThings.setVisibility(View.GONE);
        binding.protocolDownloadAction.setEnabled(false);
    }

    private void hideProtocolCreate() {
        binding.addProtocolTemp.setVisibility(View.INVISIBLE);
        binding.addProtocolTemp.setEnabled(false);
    }

    private void showProgress() {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        binding.progressBarLayout.setVisibility(View.INVISIBLE);
    }

    private void showError(CharSequence msg) {
        hideProtocolThings();
        hideProgress();
        binding.protocolRecycler.setVisibility(View.GONE);
        binding.errorLayout.setVisibility(View.VISIBLE);
        binding.errorTextProtocols.setText(msg);
    }

    @SuppressLint("ResourceAsColor")
    private void createSearchView() {
        int searchTextId = binding.search.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchText = binding.search.findViewById(searchTextId);
        searchText.setTypeface(ResourcesCompat.getFont(requireContext(),R.font.clear_sans_medium));
        searchText.setTextColor(R.color.darkGrayText);
        searchText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);

        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}