package com.injent.miscalls.ui.callinfo;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.injent.miscalls.R;
import com.injent.miscalls.data.calllist.CallInfo;
import com.injent.miscalls.databinding.FragmentCallInfoBinding;
import com.injent.miscalls.ui.callstuff.CallStuffViewModel;

import java.util.Objects;

public class CallInfoFragment extends Fragment {

    private FragmentCallInfoBinding binding;
    private InfoAdapter adapter;
    private CallStuffViewModel viewModel;

    public CallInfoFragment() {
        //Need for working
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

        viewModel = new ViewModelProvider(requireActivity()).get(CallStuffViewModel.class);

        setupRecyclerViewInfo();

        viewModel.getCallLiveData().observe(getViewLifecycleOwner(), this::setInfo);
    }

    private void call(String number) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL); // Action for what intent called for
        intent.setData(Uri.parse("tel: " + number)); // Data with intent respective action on intent
        startActivity(intent);
    }

    private void setupRecyclerViewInfo() {
        adapter = new InfoAdapter(getResources().getStringArray(R.array.infoType));
        binding.infoList.setAdapter(adapter);
        binding.complaintField.setMovementMethod(new ScrollingMovementMethod());
    }

    private void copyText(String text) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("clip", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext(), R.string.textCopied,Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void setInfo(CallInfo callInfo) {
        binding.complaintField.setText(callInfo.getComplaints());

        //Listeners
        binding.copyComplaints.setOnClickListener(view1 -> copyText(callInfo.getComplaints()));
        binding.callButton.setOnClickListener(view1 -> call(callInfo.getPhoneNumber()));

        if (callInfo.isInspected())
            binding.statusText.setVisibility(View.VISIBLE);

        adapter.submitList(callInfo.getData());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
