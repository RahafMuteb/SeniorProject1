package com.example.googlemap.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.tech.TagTechnology;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemap.R;
import com.example.googlemap.databinding.FragmentMapsBinding;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    FragmentMapsBinding binding;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker;
    private MarkerOptions markerOptions;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapInitialize();


        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String latValue = binding.Latitude.getText().toString();
                String longValue = binding.Longitude.getText().toString();

                Bundle bundle = new Bundle();

                bundle.putString("latitude", latValue);
                bundle.putString("longitude", longValue);

                Fragment fragment = new AddLatlngFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.container,fragment)
                        .addToBackStack("name")
                        .setReorderingAllowed(true)
                        .commit();

            }
        });


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

        binding.next.setVisibility(View.VISIBLE);


        if (list.size()>0){
            Address address = list.get(0);
            String location= address.getSubAdminArea();
            double latitude =address.getLatitude();
            double longitude= address.getLongitude();
            gotoLatLng(latitude,longitude, 17f);

            if(marker!=null){
                marker.remove();
            }
            markerOptions = new MarkerOptions();
            markerOptions.title(location);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markerOptions.position(new LatLng(latitude,longitude));
            marker = mMap.addMarker(markerOptions);
            //markerOptions.isDraggable();

        }
    }

    private void gotoLatLng(double latitude, double longitude, float v) {

        LatLng latLng = new LatLng(latitude, longitude);
        binding.Latitude.setText(String.valueOf(latLng.latitude));
        binding.Longitude.setText(String.valueOf(latLng.longitude));
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

                        mMap.setMyLocationEnabled(true);
                        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(@NonNull LatLng latLng) {
                                int LayoutSearch = binding.layoutSearch.getVisibility();
                                if(LayoutSearch== View.VISIBLE){
                                    binding.layoutSearch.setVisibility(View.VISIBLE);

                                }else{
                                    if(LayoutSearch== View.GONE){
                                        binding.layoutSearch.setVisibility(View.VISIBLE);

                                    }

                                }

                            }
                        });
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
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest ,PermissionToken permissionToken){
                        permissionToken.continuePermissionRequest();

                    }
                }).check();

        if(mMap !=null){
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(@NonNull Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {

                    Geocoder geocoder= new Geocoder(getContext());
                    List<Address> list = null;
                    try {
                      LatLng markerPostion = marker.getPosition();
                      list = geocoder.getFromLocation(markerPostion.latitude,markerPostion.longitude,1);
                      binding.Latitude.setText(String.valueOf(markerPostion.latitude));
                      binding.Longitude.setText(String.valueOf(markerPostion.longitude));
                      Address address= list.get(0);
                      marker.setTitle(address.getAdminArea());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {

                }
            });
        }

    }


}