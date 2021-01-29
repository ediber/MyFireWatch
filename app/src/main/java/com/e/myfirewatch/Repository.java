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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class Repository {

    private static final String TAG = "my_tag";
    // static variable single_instance of type Singleton
    private static Repository single_instance = null;
    private FirebaseFirestore db;

    private MutableLiveData<List<Fire>> localFiresLive;



    private String reporterMail;


    private Repository() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        localFiresLive = new MutableLiveData<>();
        localFiresLive.postValue(new ArrayList<>());

        listenToData();
    }

    public String getReporterMail() {
        return reporterMail;
    }

    public LiveData<List<Fire>> getLocalFiresLive() {
        return localFiresLive;
    }


    private void listenToData() {
        db.collection("fires")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            List<Fire> localFires = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                        //        Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> data = document.getData();
                                int severity = Integer.parseInt(data.get("severity").toString());
                                double lat = Double.parseDouble(data.get("lat").toString());
                                double lng = Double.parseDouble(data.get("lng").toString());
                                LatLng latlng = new LatLng(lat, lng);
                                String activeString = data.get("is_active").toString();
                                boolean isActive;
                                if(activeString.equals("true")){
                                    isActive = true;
                                } else {
                                    isActive = false;
                                }

                                Fire fire = new Fire(data.get("reporter").toString(), severity,latlng,
                                        document.getId(), isActive);

                                localFires.add(fire);
                            }

                            localFiresLive.setValue(localFires);

                            Log.d(TAG, "post value: " + task.getResult().size());

                        } else {
                            Log.d(TAG, "Error getting documents.", task.getException());
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


    public void save(double latitude, double longitude, String severity, boolean active, String id) {

        Map<String, Object> fire = new HashMap<>();
        fire.put("lat", latitude);
        fire.put("lng", longitude);
        fire.put("reporter", reporterMail);
        fire.put("severity", severity);
        fire.put("is_active", active);

        // we add a new fire to firebase
        if(id.equals("")){
            db.collection("fires")
                    .add(fire)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            listenToData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error adding document");
                        }
                    });
        }

        // update existing fire
        else {
            db.collection("fires").document(id)
                    .set(fire)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            listenToData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
        }




    }

    public Fire getFireByLatitude(double latitude) {
        List<Fire> fires = localFiresLive.getValue();
        for (int i = 0; i < fires.size(); i++) {
            if(fires.get(i).getLatLng().latitude == latitude){
                return fires.get(i);
            }
        }
        return null;
    }

    public void setReporter(String email) {
        this.reporterMail = email;
    }
}
