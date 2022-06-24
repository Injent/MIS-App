package com.injent.miscalls.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.injent.miscalls.ui.callinfo.CallInfoFragment;
import com.injent.miscalls.ui.callstuff.CallStuffFragment;
import com.injent.miscalls.ui.diagnosis.DiagnosisFragment;
import com.injent.miscalls.ui.editor.EditorFragment;
import com.injent.miscalls.ui.inspection.InspectionFragment;
import com.injent.miscalls.ui.maps.MapsFragment;
import com.injent.miscalls.ui.overview.OverviewFragment;
import com.injent.miscalls.ui.pdfviewer.PdfViewerFragment;
import com.injent.miscalls.util.ViewType;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private int viewType;
    private final int itemCount;
    private ViewModel viewModel;

    public ViewPagerAdapter(@NonNull Fragment fragment, int itemCount, ViewModel viewModel) {
        super(fragment);
        if (fragment instanceof CallStuffFragment) {
            viewType = ViewType.VIEW_PAGER_CALL;
        } else if (fragment instanceof OverviewFragment) {
            viewType = ViewType.VIEW_PAGER_OVERVIEW;
        }
        this.itemCount = itemCount;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        if (viewType == ViewType.VIEW_PAGER_CALL) {
            switch (position) {
                case 0: fragment = new CallInfoFragment(viewModel);
                    break;
                case 1: fragment = new InspectionFragment(viewModel);
                    break;
                case 2: fragment = new DiagnosisFragment(viewModel);
                    break;
                case 3: fragment = new MapsFragment();
                    break;
                default: throw new IllegalStateException();
            }
        } else if (viewType == ViewType.VIEW_PAGER_OVERVIEW) {
            switch (position) {
                case 0: fragment = new EditorFragment(viewModel);
                    break;
                case 1: fragment = new PdfViewerFragment(viewModel);
                    break;
                default: throw new IllegalStateException();
            }
        }
        if (fragment == null) {
            throw new NullPointerException();
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }
}