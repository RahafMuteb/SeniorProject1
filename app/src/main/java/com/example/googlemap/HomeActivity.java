package com.example.googlemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.googlemap.databinding.ActivityHomeBinding;
import com.example.googlemap.fragment.HomeFragment;

import com.example.googlemap.fragment.ManageFragment;
import com.example.googlemap.fragment.ProfileFragment;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ReplaceFragment(new HomeFragment());


        binding.navBottomBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.home:
                        ReplaceFragment(new HomeFragment());
                        break;


                    case R.id.manage:
                        ReplaceFragment(new ManageFragment());
                        break;


                    case R.id.Profile:
                        ReplaceFragment(new ProfileFragment());
                        break;

                }
                return true;
            }
        });
    }

    private void ReplaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

    }

}