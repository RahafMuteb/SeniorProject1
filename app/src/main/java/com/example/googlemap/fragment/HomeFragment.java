package com.example.googlemap.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemap.Model;
import com.example.googlemap.R;
import com.example.googlemap.databinding.FragmentHomeBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements OnMapReadyCallback{

    FragmentHomeBinding binding;

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker;
    private MarkerOptions markerOptions;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;



    public HomeFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       binding = FragmentHomeBinding.inflate(inflater, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        databaseReference= FirebaseDatabase.getInstance().getReference("post");

        mapInitialize();


       return binding.getRoot();
    }


    private void mapInitialize() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(16);
        locationRequest.setFastestInterval(3000);

        binding.searchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_SEARCH
                        ||i == EditorInfo.IME_ACTION_SEARCH
                        ||keyEvent.getAction()==keyEvent.ACTION_DOWN
                        ||keyEvent.getAction()==keyEvent.KEYCODE_ENTER
                ){
                    goToSearchLocation();
                }
                return false;
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

    }

    private void goToSearchLocation() {
        String searchLocation= binding.searchEdt.getText().toString();

        Geocoder geocoder= new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try {
            list= geocoder.getFromLocationName(searchLocation,1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.size()>0){
            Address address = list.get(0);
            String location= address.getSubAdminArea();
            double latitude =address.getLatitude();
            double longitude= address.getLongitude();
            gotoLatLng(latitude,longitude, 17f);


        }
    }

    private void gotoLatLng(double latitude, double longitude, float v) {

        LatLng latLng = new LatLng(latitude, longitude);
      //  binding.Latitude.setText(String.valueOf(latLng.latitude));
      ///  binding.Longitude.setText(String.valueOf(latLng.longitude));
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(latLng,17f);
        mMap.animateCamera(update);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                              for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                  Model model  = dataSnapshot.getValue(Model.class);
                                  LatLng latLng = new LatLng(model.getLatitude(), model.getLongitude());

                                  markerOptions= new MarkerOptions();
                                  markerOptions.position(latLng)
                                          .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                                          );
                                  marker = mMap.addMarker(markerOptions
                                          .title(model.getAddress()));
                              }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        mMap.setMyLocationEnabled(true);
                        fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"error"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                            }
                        });

                    }
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse){
                        Toast.makeText(getContext(),"Permission"+
                                permissionDeniedResponse.getPermissionName()+""+"was denied!", Toast.LENGTH_SHORT).show();

                    }
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest , PermissionToken permissionToken){
                        permissionToken.continuePermissionRequest();

                    }
                }).check();

     //  mMap.setOnInfoWindowClickListener(this);


    }

/*
    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

        binding.delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(marker!=null){
                    marker.remove();
                }
                Toast.makeText(getContext(), "deleted",Toast.LENGTH_LONG).show();
            }
        });
    }*/


}