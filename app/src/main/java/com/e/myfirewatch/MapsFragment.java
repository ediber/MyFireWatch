package com.e.myfirewatch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class MapsFragment extends Fragment {

    private static final String TAG = "my_tag";
    private GoogleMap googleMap;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {



        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            /*LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

            MapsFragment.this.googleMap = googleMap;

            Log.d(TAG, "onMapready");


            observeData();

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    LatLng latLng = marker.getPosition();
                    //marker.getTag()
                    double lat = latLng.latitude;

                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat", lat);

                    findNavController(MapsFragment.this).navigate(R.id.action_mapsFragment_to_edit_NewFireFragment, bundle);

                    return false;
                }
            });

        }
    };


    private void observeData() {
        Repository.getInstance().getLocalFiresLive().observe(getViewLifecycleOwner(), new Observer<List<Fire>>() {
            @Override
            public void onChanged(List<Fire> fires) {

                Log.d(TAG, "on data changed");

                for (int i = 0; i < fires.size(); i++) {
                    Fire fire = fires.get(i);
                    LatLng latlng = fire.getLatLng();
                    int severity = fire.getSeverity();
                    googleMap.addMarker(new MarkerOptions().position(latlng).title("severity: " + severity));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                }

            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        view.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("lat_lng_string", "aaaa");

                findNavController(MapsFragment.this).navigate(R.id.action_mapsFragment_to_edit_NewFireFragment, bundle);

            }
        });

    }

/*    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "on resume");

        observeData();
    }*/
}