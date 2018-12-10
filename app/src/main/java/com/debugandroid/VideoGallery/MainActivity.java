package com.debugandroid.VideoGallery;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.design.widget.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    ActionBar actionBar;
    String s,s2="",email;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.Wifi:
                    actionBar.setTitle("Wifi Demo");
                    setWifiFragment(s2);
                    return true;

            }
            return false;
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
        email = intent.getStringExtra("email");
        actionBar=getSupportActionBar();
        BottomNavigationView bottom_navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        checkandAskPermission();
        setWifiFragment(value);

    }
    public void setWifiFragment(String s1){
        FragmentManager fm= getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("params",s1);
        bundle.putString("email",email);
        WifiFragment wifiFragment = new WifiFragment();
        wifiFragment.setArguments(bundle);
        ft.replace(R.id.main_container,wifiFragment);
        ft.commit();
    }


    private void checkandAskPermission() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Storage");
        if (!addPermission(permissionsList, Manifest.permission.CHANGE_WIFI_STATE))
            permissionsNeeded.add("Network");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 0; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }

            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        //initVideo();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CHANGE_NETWORK_STATE,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION,PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.CHANGE_NETWORK_STATE)==PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {

                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

}
