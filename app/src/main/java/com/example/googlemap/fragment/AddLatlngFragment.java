package com.example.googlemap.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.googlemap.Model;
import com.example.googlemap.R;
import com.example.googlemap.databinding.FragmentAddLatlngBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;


public class AddLatlngFragment extends Fragment {

    FragmentAddLatlngBinding binding;

    FirebaseDatabase firebaseDatabase;
    ProgressDialog progressDialog;




    public AddLatlngFragment() {


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentAddLatlngBinding.inflate(inflater, container, false);


       progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
       progressDialog.setTitle("Uploading..");
       progressDialog.setCanceledOnTouchOutside(false);
       progressDialog.setMessage("Pleasw Wait..");
       progressDialog.setCancelable(false);

       Bundle bundle = this.getArguments();
       String latValue = bundle.getString("latitude");
        String longValue = bundle.getString("longitude");
        binding.Latitude2.setText(latValue);
        binding.Longitude2.setText(longValue);

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progressDialog.show();
                Model post = new Model();
                post.setLatitude(Double.parseDouble(binding.Latitude2.getText().toString()));
                post.setLongitude(Double.parseDouble(binding.Longitude2.getText().toString().trim()));
                post.setAddress(binding.Address.getText().toString().trim());
                post.setAddressTwo(binding.AddressTwo.getText().toString().trim());
                post.setSize(binding.Size.getText().toString().trim());
                post.setDiscription(binding.discription.getText().toString().trim());


                firebaseDatabase.getReference().child("post").push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Upload Successfully", Toast.LENGTH_SHORT).show();
                        Fragment fragment= new HomeFragment();
                        FragmentTransaction transaction= getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, fragment).commit();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getContext(), "ERROR"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                });


            }
        });


       return binding.getRoot();
    }
}