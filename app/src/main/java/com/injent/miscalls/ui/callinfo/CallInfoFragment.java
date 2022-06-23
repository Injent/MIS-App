package com.injent.miscalls.ui.callinfo;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.database.calls.MedCall;
import com.injent.miscalls.databinding.FragmentCallInfoBinding;
import com.injent.miscalls.ui.callstuff.CallStuffViewModel;

public class CallInfoFragment extends Fragment {

    private FragmentCallInfoBinding binding;
    private InfoAdapter adapter;
    private CallStuffViewModel viewModel;

    public CallInfoFragment(ViewModel viewModel) {
        this.viewModel = (CallStuffViewModel) viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_call_info,
                container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerViewInfo();
        setListeners();
    }

    private void setListeners() {
        // Observers
        viewModel.getCallLiveData().observe(getViewLifecycleOwner(), this::setInfo);

        // Listeners
        binding.mapButton.setOnClickListener(view -> viewModel.openMap());
    }

    private void setupRecyclerViewInfo() {
        adapter = new InfoAdapter();
        binding.infoList.setAdapter(adapter);
        binding.infoList.setItemAnimator(null);
        binding.complaintField.setMovementMethod(new ScrollingMovementMethod());
    }

    private void copyText(String text) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("clip", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext(), R.string.textCopied,Toast.LENGTH_SHORT).show();
    }

    private void callToPerson(String number) {
        if (App.getUserDataManager().getBoolean(R.string.keyAnonCall)) {
            number = number.replace("+7","#31#8");
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    private void setInfo(MedCall medCall) {
        binding.complaintField.setText(medCall.getComplaints());

        //Listeners
        binding.copyComplaints.setOnClickListener(v -> copyText(medCall.getComplaints()));
        binding.callButton.setOnClickListener(v -> callToPerson(medCall.getPhoneNumber()));

        if (medCall.isInspected())
            binding.statusText.setVisibility(View.VISIBLE);

        adapter.submitList(medCall.getData());
        playAnimations();
    }

    private void playAnimations() {
        binding.complaintField.animate()
                .translationXBy(-binding.complaintField.getWidth())
                .setDuration(0L)
                .start();
        binding.complaintField.setVisibility(View.VISIBLE);
        binding.complaintField.animate()
                .setDuration(500L)
                .translationXBy(binding.complaintField.getWidth())
                .start();

        binding.infoLayout.animate()
                .translationYBy(binding.infoLayout.getHeight())
                .setDuration(0L)
                .start();
        binding.infoLayout.setVisibility(View.VISIBLE);
        binding.infoLayout.animate()
                .setStartDelay(50L)
                .setDuration(400L)
                .translationYBy(- binding.infoLayout.getHeight())
                .start();

        binding.callButton.animate()
                .translationXBy(binding.callButton.getWidth())
                .setDuration(0L)
                .start();

        binding.callButton.animate()
                .translationXBy(-binding.callButton.getWidth())
                .setDuration(800L)
                .start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
        binding = null;
    }
}
