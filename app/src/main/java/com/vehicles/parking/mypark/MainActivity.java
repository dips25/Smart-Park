package com.vehicles.parking.mypark;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import com.vehicles.parking.mypark.Utils.CustomAlertDialog;
import com.vehicles.parking.mypark.Utils.CustomDialog;
import com.vehicles.parking.mypark.Utils.MyUndoView;
import com.vehicles.parking.mypark.fragments.MapsFragment;
import com.vehicles.parking.mypark.fragments.ParksFragment;
import com.vehicles.parking.mypark.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity  {

    public static final int PERMISSIONS_CODE = 100;

    //Views
    BottomNavigationView bottomNavigationView;

    //Location
    LocationManager locationManager;

    Configuration config;

    ActivityResultLauncher activityResultLauncher;

    ActivityResultLauncher locationPerms;
    boolean isPerms = false;
    boolean hasLocPerms = false;

    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.INTERNET};
    String[] camPerms = {Manifest.permission.CAMERA , Manifest.permission.READ_MEDIA_IMAGES};

    String[] camOldPerms = {Manifest.permission.CAMERA ,  Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationPerms = registerForActivityResult(

                new ActivityResultContracts.RequestMultiplePermissions(),
                (isGranted)->{

                    if (isGranted.containsValue(false)) {

                        CustomDialog customDialog = new CustomDialog(MainActivity.this);
                        customDialog.setView(R.layout.locationperms_dialog);
                        customDialog.setSize(300 , 300 , 3);
                        customDialog.setCanceledOnTouchOutside(false);
                        customDialog.show();

                        LinearLayout proceed = customDialog.findViewById(R.id.proceed);
                        LinearLayout cancel = customDialog.findViewById(R.id.cancel);

                        proceed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package" , getPackageName() , null));
                                startActivity(intent);

                                //locationPerms.launch(perms);
                                customDialog.dismiss();



                            }
                        });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                customDialog.dismiss();

                            }
                        });


                    } else {

                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                            checkGPS();

                        }

                    }

                }
        );

        activityResultLauncher = registerForActivityResult(

                new ActivityResultContracts.RequestMultiplePermissions(),
                (isGranted)->{

                    if (isGranted.containsValue(false)) {

                        CustomDialog customDialog = new CustomDialog(MainActivity.this);
                        customDialog.setView(R.layout.extenalstorage_perms_dialog);
                        customDialog.setSize(300 , 300 , 3);
                        customDialog.setCanceledOnTouchOutside(false);
                        customDialog.show();

                        LinearLayout proceed = customDialog.findViewById(R.id.proceed);
                        LinearLayout cancel = customDialog.findViewById(R.id.cancel);

                        proceed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package" , getPackageName() , null));
                                startActivity(intent);

                                //locationPerms.launch(perms);
                                customDialog.dismiss();



                            }
                        });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                customDialog.dismiss();

                            }
                        });

                    }





                }
        );





        if (checkPermissions()) {

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                //checkGPS();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame , new MapsFragment() , "MapsFragment")
                        .addToBackStack(null)
                        .commit();

            } else {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame , new MapsFragment() , "MapsFragment")
                        .addToBackStack(null)
                        .commit();

            }
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);

        Menu menuView = bottomNavigationView.getMenu();


        //menuView.findItem(R.id.home).setEnabled(false);
        menuView.findItem(R.id.parks).setEnabled(false);
        menuView.findItem(R.id.profile).setEnabled(false);



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.home) {

                    if (checkPermissions()) {

                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                           checkGPS();

                        } else {

                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_frame , new MapsFragment() , "MapsFragment")
                                    .addToBackStack(null)
                                    .commit();


                        }

                    }


                    return true;

                } else if (item.getItemId() == R.id.parks) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_frame , new ParksFragment(), "ParksFragment")
                            .addToBackStack(null)
                            .commit();


                    return true;

                } else if (item.getItemId() == R.id.profile) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_frame , new ProfileFragment(), "ProfileFragment")
                            .addToBackStack(null)
                            .commit();

                    return true;
                }

                return false;

            }
        });
    }

    private boolean checkPermissions() {

        for (String s : perms) {

            if (ContextCompat.checkSelfPermission(this , s) == PackageManager.PERMISSION_GRANTED) {

                hasLocPerms = true;

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this , s)) {

//                CustomDialog customDialog = new CustomDialog(MainActivity.this);
//                customDialog.setView(R.layout.permissions_dialog);
//                customDialog.setSize(300 , 300 , 3);
//                customDialog.setCanceledOnTouchOutside(false);
//                customDialog.show();
//
//                LinearLayout proceed = customDialog.findViewById(R.id.proceed);
//                LinearLayout cancel = customDialog.findViewById(R.id.cancel);
//
//                proceed.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        locationPerms.launch(perms);
//                        customDialog.dismiss();
//
//
//
//                    }
//                });
//
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        customDialog.dismiss();
//
//                    }
//                });
                locationPerms.launch(perms);
                return false;

            } else {

                locationPerms.launch(perms);
                return false;
            }


        }

        return hasLocPerms;

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//
//            boolean islocPerms = false;
//
//            for (String s : perms) {
//
//                if (ContextCompat.checkSelfPermission(this , s) != PackageManager.PERMISSION_GRANTED) {
//
//                    locationPerms.launch(perms);
//                    return;
//
//                } else {
//
//                    islocPerms = true;
//
//                }
//            }
//
//            if (islocPerms) {
//
//                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//
//                    checkGPS();
//
//                } else {
//
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.main_frame , new MapsFragment() , "MapsFragment")
//                            .addToBackStack(null)
//                            .commit();
//
//
//                }
//            }
//
//
//
////            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
////                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////
//////                ActivityResultLauncher accessFineLocation = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
//////
//////                        (isGranted) -> {
//////
//////                            if (isGranted) {
//////
//////                                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//////
//////                                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(MainActivity.this);
//////                                    customAlertDialog.setLayout(R.layout.location_dialog);
//////                                    customAlertDialog.setImage(R.drawable.ic_launcher_foreground);
//////                                    customAlertDialog.setTitle("Alert");
//////                                    customAlertDialog.setCancelable(false);
//////
//////                                    customAlertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//////                                        @Override
//////                                        public void onClick(DialogInterface dialog, int which) {
//////
//////                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//////                                            startActivity(intent);
//////
//////                                        }
//////                                    });
//////
//////                                    customAlertDialog.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
//////                                        @Override
//////                                        public void onClick(DialogInterface dialog, int which) {
//////
//////
//////
//////                                        }
//////                                    });
//////                                    AlertDialog dialog = customAlertDialog.build();
//////                                    customAlertDialog.setSize(300 , 300);
//////                                    dialog.show();
//////
//////
//////
//////
//////
//////                                } else {
//////
//////                                    getSupportFragmentManager().beginTransaction()
//////                                            .replace(R.id.main_frame , new MapsFragment() , "MapsFragment")
//////                                            .addToBackStack(null)
//////                                            .commit();
//////
//////
//////                                }
//////
//////                            }
//////                        }
//////
//////                );
//////
//////
//////                ActivityResultLauncher accessCoarseLocation = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
//////
//////                        (isGranted) -> {
//////
//////                            if (isGranted) {
//////
//////                                accessFineLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION);
//////                            }
//////
//////                        }
//////
//////                );
//////
//////                accessCoarseLocation.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
////
////            } else {
////
////                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
////
////                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(MainActivity.this);
////                    customAlertDialog.setLayout(R.layout.location_dialog);
////                    customAlertDialog.setTitle("Alert");
////                    customAlertDialog.setImage(R.drawable.ic_launcher_background);
////
////                    customAlertDialog.setCancelable(false);
////
////                    customAlertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialog, int which) {
////
////                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////                            startActivity(intent);
////
////                        }
////                    });
////
////                    customAlertDialog.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialog, int which) {
////
////
////
////                        }
////                    });
////                    AlertDialog dialog = customAlertDialog.build();
////                    dialog.show();
////                    customAlertDialog.setSize(300 , 300);
////
////
////
////
////
////
////                } else {
////
////                    getSupportFragmentManager().beginTransaction()
////                            .replace(R.id.main_frame , new MapsFragment() , "MapsFragment")
////                            .addToBackStack(null)
////                            .commit();
////
////
////                }
////
////
////            }
//
//
//        }
//        else {
//
//            for (String s : perms) {
//
//                if (ContextCompat.checkSelfPermission(this , s) != PackageManager.PERMISSION_GRANTED) {
//
//                    ActivityCompat.requestPermissions(this , perms , PERMISSIONS_CODE);
//                    break;
//                }
//
//
//            }
//
////            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
////                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////
////                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_CODE);
////
////            } else {
////
//////                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//////
//////                    checkGPS();
//////
//////                } else {
//////
////                    getSupportFragmentManager().beginTransaction()
////                            .replace(R.id.main_frame , new MapsFragment() , "MapsFragment")
////                            .addToBackStack(null)
////                            .commit();
//////
//////
//////                }
////            }
//        }
    }

    private void checkGPS() {

        CustomAlertDialog customAlertDialog = new CustomAlertDialog(MainActivity.this);
        customAlertDialog.setLayout(R.layout.location_dialog);
        customAlertDialog.setImage(R.drawable.ic_launcher_foreground);
        customAlertDialog.setCancelable(false);

        customAlertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        });

        customAlertDialog.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



            }
        });
        AlertDialog dialog = customAlertDialog.build();
        customAlertDialog.setSize(300 , 300);
        dialog.show();
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PERMISSIONS_CODE) {
//
//            if (grantResults.length > 0) {
//
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//
//                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//
//                        checkGPS();
//
//                    } else {
//
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.main_frame , new MapsFragment() , "MapsFragment")
//                                .addToBackStack(null)
//                                .commit();
//
//
//                    }
//                } else {
//
//                    checkPermissions();
//                }
//            }
//        } else if (requestCode ==300) {
//
//
//                if (grantResults.length>0) {
//
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//
//                        isPerms = true;
//                    }
//                }
//
//        }
//
//
//    }

    public boolean requestPerms() {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)

        for (String s : camPerms) {

            if (ContextCompat.checkSelfPermission(this, s) == PackageManager.PERMISSION_GRANTED) {

                isPerms = true;

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, s)) {

//                CustomDialog customDialog = new CustomDialog(MainActivity.this);
//                customDialog.setView(R.layout.permissions_dialog);
//                customDialog.setSize(300 , 300 , 3);
//                customDialog.setCanceledOnTouchOutside(false);
//                customDialog.show();
//
//                LinearLayout proceed = customDialog.findViewById(R.id.proceed);
//                LinearLayout cancel = customDialog.findViewById(R.id.cancel);
//
//                proceed.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        locationPerms.launch(perms);
//                        customDialog.dismiss();
//
//
//
//                    }
//                });
//
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        customDialog.dismiss();
//
//                    }
//                });
                activityResultLauncher.launch(camPerms);
                return false;

            } else {

                activityResultLauncher.launch(camPerms);


                return false;
            }

        } else {

            for (String s : camOldPerms) {

                if (ContextCompat.checkSelfPermission(this, s) == PackageManager.PERMISSION_GRANTED) {

                    isPerms = true;

                } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, s)) {

//                CustomDialog customDialog = new CustomDialog(MainActivity.this);
//                customDialog.setView(R.layout.permissions_dialog);
//                customDialog.setSize(300 , 300 , 3);
//                customDialog.setCanceledOnTouchOutside(false);
//                customDialog.show();
//
//                LinearLayout proceed = customDialog.findViewById(R.id.proceed);
//                LinearLayout cancel = customDialog.findViewById(R.id.cancel);
//
//                proceed.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        locationPerms.launch(perms);
//                        customDialog.dismiss();
//
//
//
//                    }
//                });
//
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        customDialog.dismiss();
//
//                    }
//                });
                    activityResultLauncher.launch(camOldPerms);
                    return false;

                } else {

                    activityResultLauncher.launch(camOldPerms);
                    return false;
                }

            }


        }

        return isPerms;
    }



    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        this.config = newConfig;
    }

//    public class ViewHolder<T extends Boolean> extends RecyclerView.ViewHolder  {
//
//        public boolean flag;
//        public boolean hasSwiped;
//        public RelativeLayout viewHolderRoot;
//        public RelativeLayout linearroot;
//        public LinearLayout undoHolder;
//
//        boolean isSwiped = false;
//
//
//
//        public View itemView;
//
//        public TextView name , address , date , time , historytext;
//
//        public MyUndoView myUndoView;
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(300 , 300);
//
//        public ViewHolder(@NonNull View itemView , T flag) {
//            super(itemView);
//
////            myUndoView = new MyUndoView(context);
////            myUndoView.setLayoutParams(lp);
////            lp.addRule(RelativeLayout.ALIGN_PARENT_START);
////            myUndoView.setVisibility(View.GONE);
//
//
//
//
//
//            this.flag = flag;
//            this.itemView = itemView;
//
//            viewHolderRoot = (SwipeLayout) itemView.findViewById(R.id.removeLayout);
//            linearroot = (RelativeLayout) itemView.findViewById(R.id.linearroot);
//            name = (TextView) itemView.findViewById(R.id.single_item_name);
//            address = (TextView) itemView.findViewById(R.id.single_item_address);
//            date = (TextView) itemView.findViewById(R.id.single_item_date);
//            time = (TextView) itemView.findViewById(R.id.single_item_time);
//            historytext = (TextView) itemView.findViewById(R.id.historytext);
//
////            viewHolderRoot.addView(myUndoView);
//
//
//        }
//
//        public boolean getFlag() {
//
//            return flag;
//        }
//
//        public void setFlag(boolean flag) {
//
//            this.flag = flag;
//        }
//
//        public boolean isHasSwiped() {
//            return hasSwiped;
//        }
//
//        public void setHasSwiped(boolean hasSwiped) {
//            this.hasSwiped = hasSwiped;
//        }
//
//        public void setUndoView(int dx) {
//
//            linearroot.setTranslationX(dx);
//
//
//
//
//        }
//
//        public void setUndoView() {
//
//            //linearroot.setTranslationX(300);
//
//
//
//
//        }
//
//
//    }

}

