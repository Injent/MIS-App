package com.injent.miscalls.ui.protocoltemp;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.injent.miscalls.App;
import com.injent.miscalls.MainActivity;
import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.FailedDownloadDb;
import com.injent.miscalls.data.patientlist.ListEmptyException;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.databinding.FragmentProtocolTempBinding;

import java.util.List;

public class ProtocolTempFragment extends Fragment {

    private ProtocolTempViewModel viewModel;
    private FragmentProtocolTempBinding binding;
    private int downloadProgress;

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

        binding.protocolRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        ProtocolAdapter adapter = new ProtocolAdapter(new ProtocolAdapter.OnItemClickListener() {
            @Override
            public void onClick(int protocolId) {
                navigateToProtocolEdit(protocolId);
            }
        });

        binding.protocolRecycler.setAdapter(adapter);

        //Observers
        viewModel.getProtocolsLiveDate().observe(this, new Observer<List<ProtocolTemp>>() {
            @Override
            public void onChanged(List<ProtocolTemp> protocolTemps) {
                adapter.submitList(protocolTemps);
            }
        });

        viewModel.getProtocolErrorLiveData().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                displayError(throwable);
            }
        });

        //Listeners
        binding.protocolDownloadAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.downloadProtocolTemps(App.getInstance().getUser().getToken());
                hideProtocolThings();
            }
        });

        binding.backFromProtocolTemps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHome();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });

        int size = viewModel.getProtocolTemps().size();

        if (size == 0) {
            viewModel.setProtocolError(new ListEmptyException());
        }
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

    private void navigateToProtocolEdit(int protocolId) {
        Bundle bundle = new Bundle();
        bundle.putInt("protocolId", protocolId);
        Navigation.findNavController(requireView()).navigate(R.id.action_protocolTempFragment_to_protocolEditFragment, bundle);
    }

    private void navigateToHome() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("updateList", true);
        Navigation.findNavController(requireView()).navigate(R.id.action_protocolTempFragment_to_homeFragment, bundle);
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

    private void showProgress() {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        binding.progressBarLayout.setVisibility(View.INVISIBLE);
    }

    private void showError(CharSequence msg) {
        hideProtocolThings();
        hideProgress();
        binding.errorLayout.setVisibility(View.VISIBLE);
        binding.errorTextProtocols.setText(msg);
    }

    private void hideError() {
        binding.errorLayout.setVisibility(View.INVISIBLE);
    }
}