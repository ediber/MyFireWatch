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
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;


public class Edit_NewFireFragment extends Fragment {


    private EditText searchEdit;
    private TextView searchText;
    private Address address;

    //private String reporter = "reporter0";
    private EditText severityEdit;
    private View searchButton;
    private Switch activeSwitch;
    private Fire fire;

    public Edit_NewFireFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit__new_fire, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        double latitude = getArguments().getDouble("lat", -99);

        searchEdit = (EditText) view.findViewById(R.id.search_edit);
        searchText =  view.findViewById(R.id.search_text);
        severityEdit =  view.findViewById(R.id.sevirity_edit);
        searchButton = view.findViewById(R.id.search_btn);
        activeSwitch = view.findViewById(R.id.active_switch);


        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
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




        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // case of new fire
                String id;
                if(fire == null){
                    id = "";
                } else {
                    id = fire.getId();
                }

                Repository.getInstance().save(address.getLatitude(), address.getLongitude(),
                        severityEdit.getText().toString(), activeSwitch.isChecked(), id);

                getActivity().onBackPressed();
            }
        });

        if(latitude != -99){
            existingLocation(latitude);
        }


    }

    private void existingLocation(double latitude) {
        showData(latitude);
        updateExistingUI();
    }

    private void updateExistingUI() {
        searchEdit.setEnabled(false);
        searchButton.setEnabled(false);
    }

    private void showData(double latitude) {
        fire = Repository.getInstance().getFireByLatitude(latitude);

        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, fire.getLatLng().longitude, 1);
            address = addresses.get(0);
            String addressToShow = parseAddress(address);
            searchText.setText(addressToShow);

        } catch (IOException e) {
            e.printStackTrace();
        }

        severityEdit.setText(fire.getSeverity()+"");
        activeSwitch.setChecked(fire.isActive());
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