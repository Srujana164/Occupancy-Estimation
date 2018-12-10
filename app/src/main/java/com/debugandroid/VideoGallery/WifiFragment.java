package com.debugandroid.VideoGallery;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.net.wifi.WifiManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WifiFragment extends Fragment {
  //  String[] BssidArr = {"a0:d3:c1:b4:8e:40","a0:d3:c1:b4:be:40","58:20:b1:10:e2:40","58:20:b1:10:e2:50","76:33:cb:37:cb:fc","9a:54:1b:4d:9f:86"};
  //  String[] BssidArr_ground ={"58:20:b1:10:e2:40","58:20:b1:10:e2:50","9a:54:1b:4d:9f:86","ae:b6:d0:e8:f9:cf"};
  //  String[] BssidArr_first ={"a0:d3:c1:b4:8e:40","a0:d3:c1:b4:be:40"};
    String[] testing = {"9a:54:1b:4d:9f:86","ae:b6:d0:e8:f9:cf","76:33:cb:37:cb:fc","f0:d7:aa:aa:e6:3d","be:d1:b8:62:23:57"};
    int len =  testing.length;
    String location;
    public class near_router{
        double distance1;
        CharSequence Bssid1;
        public void setBssid1(CharSequence bssid) {
            this.Bssid1 = bssid;
        }

        public CharSequence getBssid1 (){
            return Bssid1;
        }

        public void setDistance1(double distance){this.distance1 = distance;}
        public double getDistance1(){return distance1;}

    }
    public class device{
        CharSequence name;
        CharSequence Frequency;
        CharSequence Bssid;
        CharSequence Rssi;
        double distance;


        public void setName(CharSequence name) {
            this.name = name;
        }

        public CharSequence getName (){
            return name;
        }

        public void setFrequency(CharSequence frequency) {
            this.Frequency = frequency;
        }

        public CharSequence getFrequency (){
            return Frequency;
        }
        public void setBssid(CharSequence bssid) {
            this.Bssid = bssid;
        }

        public CharSequence getBssid (){
            return Bssid;
        }
        public void setRssi(CharSequence rssi) {
            this.Rssi= rssi;
        }

        public CharSequence getRssi (){
            return Rssi;
        }
        public void setDistance(double distance){this.distance = distance;}
        public double getDistance(){return distance;}

    }
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 125;
    List<ScanResult> wifiList;
    private WifiManager wifi;
    private WifiInfo wifiinfo;
    List<device> values = new ArrayList<device>();
   List<near_router> values1 = new ArrayList<>();
    int netCount=0;
    RecyclerView recyclerView;
    WifiScanAdapter wifiScanAdapter;
    double wifidistance;
    String mParam1,mParam2;

    public WifiFragment() {
    }

    public static WifiFragment newInstance() {
        WifiFragment fragment = new WifiFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("params");
            mParam2 = getArguments().getString("email");

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        Button btnScan= (Button) getActivity().findViewById(R.id.wifiScan);
        Button btn = (Button) getActivity().findViewById(R.id.proceed);
        wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //Check wifi enabled or not
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getActivity(), "Wifi is disabled enabling...", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        //register Broadcast receiver
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wifiList=wifi.getScanResults();
                netCount=wifiList.size();
                Log.d("netcount","netcount"+netCount);
               wifiScanAdapter.notifyDataSetChanged();
            }
        },new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiScanAdapter=new WifiScanAdapter(values,getContext());
        recyclerView= (RecyclerView) getActivity().findViewById(R.id.wifiRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(wifiScanAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkandAskPermission();
        }
        else {
            wifi.startScan();
            values.clear();
            try {
                netCount = netCount - 1;
                while (netCount >= 0) {
                 String temp = wifiList.get(netCount).BSSID.toString();

                 if (mParam1.equals("Testing")){
                     for (int i = 0; i < testing.length; i++) {
                         if (temp.equals(testing[i])) {
                             device d = new device();
                             d.setName(wifiList.get(netCount).SSID.toString());
                             d.setBssid(wifiList.get(netCount).BSSID.toString());
                             d.setFrequency(Integer.toString(wifiList.get(netCount).frequency));
                             d.setRssi(Integer.toString(wifiList.get(netCount).level));
                             double wifidistance = calculateDistance(wifiList.get(netCount).level, wifiList.get(netCount).frequency);
                             d.setDistance(wifidistance);
                             values.add(d);
                             break;

                         }
                     }

                 }
            /*     if (mParam1.equals("Ground_Floor")) {
                     for (int i = 0; i < BssidArr_ground.length; i++) {
                         if (temp.equals(BssidArr_ground[i])) {
                             device d = new device();
                             d.setName(wifiList.get(netCount).SSID.toString());
                             d.setBssid(wifiList.get(netCount).BSSID.toString());
                             d.setFrequency(Integer.toString(wifiList.get(netCount).frequency));
                             d.setRssi(Integer.toString(wifiList.get(netCount).level));
                             double wifidistance = calculateDistance(wifiList.get(netCount).level, wifiList.get(netCount).frequency);
                             d.setDistance(wifidistance);
                             values.add(d);
                             break;

                         }
                     }
                 }
                    if (mParam1.equals("First_Floor")) {
                        for (int i = 0; i < BssidArr_first.length; i++) {
                            if (temp.equals(BssidArr_first[i])) {
                                device d = new device();
                                d.setName(wifiList.get(netCount).SSID.toString());
                                d.setBssid(wifiList.get(netCount).BSSID.toString());
                                d.setFrequency(Integer.toString(wifiList.get(netCount).frequency));
                                d.setRssi(Integer.toString(wifiList.get(netCount).level));
                                double wifidistance = calculateDistance(wifiList.get(netCount).level, wifiList.get(netCount).frequency);
                                d.setDistance(wifidistance);
                                values.add(d);
                                break;

                            }
                        }
                    }*/
                    wifiScanAdapter.notifyDataSetChanged();
                    netCount=netCount -1;
                }
            }
            catch (Exception e){
                Log.d("Wifi", e.getMessage());
            }

        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent =  new Intent(getActivity(), RecordActivity.class);
                myIntent.putExtra("location",location);
                myIntent.putExtra("email",mParam2);
                startActivity(myIntent);
            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               TextView t1 = (TextView) getActivity().findViewById(R.id.textView);
               t1.setText("");
               wifi.startScan();
               values.clear();
               try {

                   netCount=netCount -1;
                   while (netCount>=0){
                       String temp = wifiList.get(netCount).BSSID;
                       Log.d("temp",temp);
                    /*   if (mParam1.equals("Ground_Floor")) {
                           for (int i = 0; i < BssidArr_ground.length; i++) {
                               if (temp.equals(BssidArr_ground[i])) {
                                   device d = new device();
                                   d.setName(wifiList.get(netCount).SSID.toString());
                                   d.setBssid(wifiList.get(netCount).BSSID.toString());
                                   d.setFrequency(Integer.toString(wifiList.get(netCount).frequency));
                                   d.setRssi(Integer.toString(wifiList.get(netCount).level));
                                   double wifidistance = calculateDistance(wifiList.get(netCount).level, wifiList.get(netCount).frequency);
                                   d.setDistance(wifidistance);
                                   values.add(d);
                                   break;

                               }
                           }
                       }
                       if (mParam1.equals("First_Floor")) {
                           for (int i = 0; i < BssidArr_first.length; i++) {
                               if (temp.equals(BssidArr_first[i])) {
                                   device d = new device();
                                   d.setName(wifiList.get(netCount).SSID.toString());
                                   d.setBssid(wifiList.get(netCount).BSSID.toString());
                                   d.setFrequency(Integer.toString(wifiList.get(netCount).frequency));
                                   d.setRssi(Integer.toString(wifiList.get(netCount).level));
                                   double wifidistance = calculateDistance(wifiList.get(netCount).level, wifiList.get(netCount).frequency);
                                   d.setDistance(wifidistance);
                                   values.add(d);
                                   break;

                               }
                           }
                       }*/
                       if (mParam1.equals("Testing")){
                           for (int i = 0; i < testing.length; i++) {
                               if (temp.equals(testing[i])) {
                                   device d = new device();
                                   near_router n = new near_router();
                                   d.setName(wifiList.get(netCount).SSID.toString());
                                   d.setBssid(wifiList.get(netCount).BSSID.toString());
                                   n.setBssid1(wifiList.get(netCount).BSSID.toString());
                                   d.setFrequency(Integer.toString(wifiList.get(netCount).frequency));
                                   d.setRssi(Integer.toString(wifiList.get(netCount).level));
                                   double wifidistance = calculateDistance(wifiList.get(netCount).level, wifiList.get(netCount).frequency);
                                   d.setDistance(wifidistance);
                                   n.setDistance1(wifidistance);
                                   values.add(d);
                                   values1.add(n);
                                   break;

                               }
                           }

                       }
                       wifiScanAdapter.notifyDataSetChanged();
                       netCount=netCount -1;
                   }
                   double min=100000;
                   int index=-1;
                   for (int u=0;u<values1.size();u++){
                        near_router n1 = values1.get(u);
                        if (n1.getDistance1()<min){
                            min = n1.getDistance1();
                            index = u;
                        }

                   }

                  if (index>=0) {


                      near_router n2 = values1.get(index);
                      CharSequence near_Bssid = n2.getBssid1();
                      Log.d("netcount","Bssid"+near_Bssid);
                       if (near_Bssid.equals("ae:b6:d0:e8:f9:cf"))
                       {
                          // Log.d("netcount","near Room 132");
                           //Toast.makeText(getActivity(), "Near Room no: 132", Toast.LENGTH_LONG).show();
                           location = "Zone 1";
                          t1.setText("Near Room no: 132");

                       }
                       else if (near_Bssid.equals("9a:54:1b:4d:9f:86" ))
                       {
                           //Log.d("netcount","near Room 102");
                           //Toast.makeText(getActivity(), "Near Room no: 133", Toast.LENGTH_LONG).show();
                           location = "Zone 2";
                           t1.setText("Near Room no: 133");

                       }
                       else if(near_Bssid.equals("f0:d7:aa:aa:e6:3d")){ // srujana laptop
                           //Log.d("netcount","near Room 134");
                           //Toast.makeText(getActivity(), "Near Room no: 134", Toast.LENGTH_LONG).show();
                           location = "Zone 3";
                           t1.setText("Near Room no: 134");
                       }

                       else if (near_Bssid.equals("be:d1:b8:62:23:57")){ //sourya old laptop
                           location = "Zone 4";
                           t1.setText("Near Room no: xxx");
                       }
//                      Intent myIntent =  new Intent(getActivity(), RecordActivity.class);
//                      startActivity(myIntent);
                   }





               }
               catch (Exception e){
                   Log.d("Wifi", e.getMessage());
               }
           }
       });
    }

    public double  calculateDistance(double levelInDb, double freqInMHz)    {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_wifi, container, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    wifi.startScan();
                } else {
                    // Permission Denied
                    Toast.makeText(getContext(), "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    private void checkandAskPermission() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("Network");


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
       // initVideo();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }
}