package com.injent.miscalls.ui.protocoltemp;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
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
    private ProtocolAdapter adapter;
    private int downloadProgress;
    private NavController navController;
    private boolean editMode;
    private int patientId;
    private int protocolCount;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_protocol_temp, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProtocolTempViewModel.class);
        navController = Navigation.findNavController(requireView());

        MainActivity.getInstance().disableFullScreen();

        if (getArguments() != null) {
            editMode = getArguments().getBoolean("editMode");
            patientId = getArguments().getInt("patientId");
        }

        binding.protocolRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ProtocolAdapter(new ProtocolAdapter.OnItemClickListener() {
            @Override
            public void onClick(int protocolId) {
                if (editMode)
                    navigateToProtocolEdit(protocolId, false);
                else
                    navigateToProtocol(protocolId);
            }
        }, editMode);

        binding.protocolRecycler.setAdapter(adapter);

        //Observers
        viewModel.getProtocolsLiveDate().observe(this, new Observer<List<ProtocolTemp>>() {
            @Override
            public void onChanged(List<ProtocolTemp> protocolTemps) {
                adapter.submitList(protocolTemps, true);
            }
        });

        viewModel.getProtocolErrorLiveData().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                displayError(throwable);
            }
        });

        createSearchView();

        //Listeners
        binding.protocolDownloadAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.downloadProtocolTemps(App.getInstance().getUser().getToken());
                hideProtocolThings();
            }
        });

        binding.addProtocolTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToProtocolEdit(protocolCount, true);
            }
        });

        if (editMode) {
            binding.backFromProtocolTemps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateToHome();
                }
            });
        } else {
            binding.backFromProtocolTemps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateToProtocol();
                }
            });
        }


        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });

        protocolCount = viewModel.getProtocolTemps().size();

        if (protocolCount == 0) {
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

    private void navigateToProtocolEdit(int protocolId, boolean newProtocol) {
        Bundle bundle = new Bundle();
        bundle.putInt("protocolId", protocolId);
        bundle.putBoolean("newProtocol", newProtocol);
        navController.navigate(R.id.action_protocolTempFragment_to_protocolEditFragment, bundle);
    }

    private void navigateToHome() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("updateList", true);
        navController.navigate(R.id.action_protocolTempFragment_to_homeFragment, bundle);
    }

    private void navigateToProtocol(int protocolId) {
        Bundle bundle = new Bundle();
        bundle.putInt("protocolId", protocolId);
        bundle.putInt("patientId", patientId);
        navController.navigate(R.id.action_protocolTempFragment_to_protocolFragment, bundle);
    }

    private void navigateToProtocol() {
        Bundle bundle = new Bundle();
        bundle.putInt("patientId", patientId);
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
}