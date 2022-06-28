package com.injent.miscalls.ui.maps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.medcall.Geo;
import com.injent.miscalls.databinding.FragmentMapsBinding;
import com.injent.miscalls.util.CustomOnBackPressedFragment;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

public class MapsFragment extends Fragment implements CustomOnBackPressedFragment {

    private FragmentMapsBinding binding;
    private final double latitude;
    private final double longitude;

    public MapsFragment(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListeners();
        setupMap();
    }

    private void setupMap() {
        GeoPoint geoPoint = Geo.toGeoPoint(new Geo(latitude, longitude));
        binding.map.setTileSource(TileSourceFactory.MAPNIK);
        binding.map.getController().setCenter(geoPoint);
        binding.map.getController().setZoom(18D);
        binding.map.setMinZoomLevel(16D);
        binding.map.setMaxZoomLevel(20D);

        Marker startMarker = new Marker(binding.map);
        startMarker.setPosition(geoPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        startMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker, requireContext().getTheme()));
        binding.map.getOverlays().add(startMarker);
    }

    private void setListeners() {
        binding.mapBack.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public boolean onBackPressed() {
        getParentFragmentManager().popBackStack();
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.map.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
