package com.vehicles.parking.mypark;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.vehicles.parking.mypark.fragments.MapsFragment;

import android.location.LocationListener;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

public class MyLocationProvider {

    private static final String TAG = MyLocationProvider.class.getName();

    Context context;

    Thread t1;

    Thread MAIN_THREAD = Thread.currentThread();
    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;

    Location mLocation;

    LocationListener locationListener;

    private static final long MIN_UPDATE_DISTANCE = 10;

    // The minimum time between updates in milliseconds
    private static final long MIN_UPDATE_TIME = 5000;

    public MyLocationProvider(Context context) {

        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (fusedLocationProviderClient == null) {

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        }

    }

    public void getLocation(OnCompleteListener<Location> networkLocationListener) {


                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken()).addOnCompleteListener(networkLocationListener);


                }

    }

    public void getGPSLocation() {

  //      locationManager.requestLocationUpdates(
//                        LocationManager.NETWORK_PROVIDER,
//                        Long.MAX_VALUE,
//                        0, locationListener);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            boolean isGPS = false;
            boolean isNetwork = false;


            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                isGPS = true;
            }

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                isNetwork = true;
            }

//            if (isGPS && !isNetwork) {
//
//                lGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                return lGps;
//
//
//            } else if (!isGPS && isNetwork) {
//
//                lNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                return lNetwork;
//
//            } else

                if (isGPS || isNetwork) {

                Location lGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location lNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (lGps == null && lNetwork != null) {

                     locationListener.onLocationChanged(lNetwork);

//

                } else if (lGps != null && lNetwork == null) {

                    locationListener.onLocationChanged(lGps);

//


                } else  if (lGps != null && lNetwork != null) {

                    if (lGps.getAccuracy()>lNetwork.getAccuracy()) {

                        locationListener.onLocationChanged(lGps);


                    } else {

                        locationListener.onLocationChanged(lNetwork);

//

                    }
                }

            }

        }


    }

    public String getFullAddress(double latitude , double longitude) {

        if (Geocoder.isPresent()) {

            if (latitude != 0 && longitude != 0) {

                Geocoder geocoder = new Geocoder(context);

                try{

                    ArrayList<Address> addresses = (ArrayList<Address>) geocoder.getFromLocation(latitude , longitude , 5);

                    if (addresses != null) {

                        if (addresses.size() > 0) {

                            ListIterator listIterator = addresses.listIterator();

                            while (listIterator.hasNext()) {

                                String s = ((Address)listIterator.next()).getAddressLine(0);

                                if (s!=null && !s.equalsIgnoreCase("")) {

                                    return s;
                                }

                            }


                        }


                    }




                } catch (IOException ex) {

                    Log.d(TAG, "getFullAddress: " + ex.getMessage());


                }


            }
        }

        return "";




    }

    public void setListener(LocationListener locationListener) {

        this.locationListener = locationListener;


    }
}

