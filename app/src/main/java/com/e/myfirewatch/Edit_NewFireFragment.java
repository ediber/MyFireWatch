package com.e.myfirewatch;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Edit_NewFireFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Edit_NewFireFragment extends Fragment {


    private EditText searchEdit;
    private TextView searchText;
    private Address address;

    private String reporter = "reporter0";
    private EditText severityEdit;

    public Edit_NewFireFragment() {
        // Required empty public constructor
    }


/*
    public static Edit_NewFireFragment newInstance(String param1, String param2) {
        Edit_NewFireFragment fragment = new Edit_NewFireFragment();
        Bundle args = new Bundle();
        return fragment;
    }
*/



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit__new_fire, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchEdit = (EditText) view.findViewById(R.id.search_edit);
        searchText =  view.findViewById(R.id.search_text);
        severityEdit =  view.findViewById(R.id.sevirity_edit);


        view.findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Address> addresses = getLocationFromAddress(searchEdit.getText().toString());

                String show = "";
                if (addresses != null && addresses.size() > 0) {
                     address = addresses.get(0);
                    show = parseAddress(address);
                }

                searchEdit.setText("");
                searchText.setText(show);
            }
        });

        String s = getArguments().getString("lat_lng_string");

        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Repository.getInstance().save(address.getLatitude(), address.getLongitude(), severityEdit.getText().toString(), reporter);
            }
        });
    }


    private String parseAddress(Address location) {
        String locality = location.getLocality();
        if (locality == null) {
            locality = "";
        }

        String thoroughfare = location.getThoroughfare();
        if (thoroughfare == null) {
            thoroughfare = "";
        }

        String subThoroughfare = location.getSubThoroughfare();
        if (subThoroughfare == null) {
            subThoroughfare = "";
        }

        return location.getCountryName() + " " + locality + " "
                + thoroughfare + " " + subThoroughfare;
    }


    private List<Address> getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getContext());
        List<Address> address;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            // LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            return address;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}