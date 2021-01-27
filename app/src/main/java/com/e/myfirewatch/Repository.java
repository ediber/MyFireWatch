package com.e.myfirewatch;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class Repository {

    private static final String TAG = "repository_tag";
    // static variable single_instance of type Singleton
    private static Repository single_instance = null;
    private FirebaseFirestore db;

    private List<Fire> localFires;


    private Repository() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        localFires = new ArrayList<>();

        listenToData();
    }

    private void listenToData() {
        db.collection("fires")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> data = document.getData();
                             //   data.get("reporter");
                                int severity = Integer.parseInt(data.get("severity").toString());
                                double lat = Double.parseDouble(data.get("lat").toString());
                                double lng = Double.parseDouble(data.get("lng").toString());
                                LatLng latlng = new LatLng(lat, lng);
                                Fire fire = new Fire(data.get("reporter").toString(), severity,latlng);
                                localFires.add(fire);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    // static method to create instance of Singleton class
    public static Repository getInstance()
    {
        if (single_instance == null)
            single_instance = new Repository();

        return single_instance;
    }


    public void save(double latitude, double longitude, String severity, String reporter) {

        Map<String, Object> fire = new HashMap<>();
        fire.put("lat", latitude);
        fire.put("lng", longitude);
        fire.put("reporter", reporter);
        fire.put("severity", severity);

// Add a new document with a generated ID
        db.collection("fires")
                .add(fire)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


    }
}
