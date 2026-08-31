package com.vehicles.parking.mypark.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.Scene;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.vehicles.parking.mypark.DataBase.SqliteDatabase;
import com.vehicles.parking.mypark.DataBase.UserSqliteDatabase;
import com.vehicles.parking.mypark.Models.Park;
import com.vehicles.parking.mypark.Models.Users;
import com.vehicles.parking.mypark.MyLocationProvider;
import com.vehicles.parking.mypark.R;
import com.vehicles.parking.mypark.Utils.CustomDialog;
import com.vehicles.parking.mypark.Utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MapsFragment extends Fragment implements LocationListener , OnMapReadyCallback , GoogleMap.OnCameraIdleListener
        , GoogleMap.OnCameraMoveStartedListener , View.OnClickListener {

    MyLocationProvider mlp;
    private Location gpsLocation , networkLocation;

    Context context;

    Button park;

    GoogleMap googleMap;

    public double latitude , longitude;

    SqliteDatabase db;

    BottomNavigationView bottomNavigationView;

    GetLatLong getLatLong;

    CustomDialog customDialog;
    private static final String TAG = MapsFragment.class.getName();

    FrameLayout openNav , myLocation;

    ViewGroup sceneroot;

    Scene start , end;

    boolean isFirstTransition = true;

    Transition startSlide , endSlide;

    MyNetworkLocationListener networkLocationListener;

    View view;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;

        networkLocationListener = new MyNetworkLocationListener();

        mlp = new MyLocationProvider(getActivity());
        mlp.setListener(MapsFragment.this);
        mlp.getLocation(networkLocationListener);

        bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
        sceneroot = (RelativeLayout) view.findViewById(R.id.button_root);
        openNav = (FrameLayout) sceneroot.findViewById(R.id.opennav);
        myLocation = (FrameLayout) sceneroot.findViewById(R.id.mylocation);

        start = Scene.getSceneForLayout(sceneroot , R.layout.buttons_visible , getActivity());
        end = Scene.getSceneForLayout(sceneroot , R.layout.buttons_invisible , getActivity());

        startSlide = new Slide(Gravity.END);
        startSlide.setDuration(200);
        endSlide = new Slide(Gravity.END);
        endSlide.setDuration(200);


        openNav.setVisibility(View.GONE);
        myLocation.setVisibility(View.GONE);
        openNav.setOnClickListener(this);
        myLocation.setOnClickListener(this);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        Log.d(TAG, "onLocationChanged: " + location.getLatitude());

        gpsLocation = location;


        if (gpsLocation != null) {

            if (googleMap != null) {

                LatLng latLng = new LatLng(gpsLocation.getLatitude(), gpsLocation.getLongitude());

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng , 18));


            } else {

                SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

                if (map != null) {

                    map.getMapAsync(MapsFragment.this);
                }


            }


        }



    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);


        if (networkLocation != null) {

            Log.d(TAG, "onMapReady: " + "Network");


            LatLng sydney = new LatLng(networkLocation.getLatitude(), networkLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromBitmap(loadMarker())));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney , 18));

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    return true;
                }
            });

            openNav.setVisibility(View.VISIBLE);
            myLocation.setVisibility(View.VISIBLE);

            this.googleMap.setOnCameraIdleListener(MapsFragment.this);
            this.googleMap.setOnCameraMoveStartedListener(MapsFragment.this);


            bottomNavigationView.getMenu().findItem(R.id.home).setEnabled(true);
            bottomNavigationView.getMenu().findItem(R.id.parks).setEnabled(true);
            bottomNavigationView.getMenu().findItem(R.id.profile).setEnabled(true);


        } else if (gpsLocation != null) {

            Log.d(TAG, "onMapReady: " + "GPS");

            LatLng sydney = new LatLng(gpsLocation.getLatitude(), gpsLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromBitmap(loadMarker())));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney , 18));

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    return true;
                }
            });

            openNav.setVisibility(View.VISIBLE);
            myLocation.setVisibility(View.VISIBLE);

            this.googleMap.setOnCameraIdleListener(MapsFragment.this);
            this.googleMap.setOnCameraMoveStartedListener(MapsFragment.this);


            bottomNavigationView.getMenu().findItem(R.id.home).setEnabled(true);
            bottomNavigationView.getMenu().findItem(R.id.parks).setEnabled(true);
            bottomNavigationView.getMenu().findItem(R.id.profile).setEnabled(true);

        }

    }

    @Override
    public void onCameraIdle() {

        CameraPosition cameraPosition = googleMap.getCameraPosition();

        Log.d(TAG, "onCameraIdle: " + cameraPosition.target.latitude);

        latitude = cameraPosition.target.latitude;
        longitude = cameraPosition.target.longitude;

        if (!isFirstTransition) {

            TransitionManager.go(start , startSlide);

        }

        sceneroot = (RelativeLayout) view.findViewById(R.id.button_root);
        openNav = (FrameLayout) sceneroot.findViewById(R.id.opennav);
        myLocation = (FrameLayout) sceneroot.findViewById(R.id.mylocation);

        openNav.setOnClickListener(this);
        myLocation.setOnClickListener(this);







    }



    private void setSharedPreferences() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("details" , Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putFloat("lat" , (float) latitude);
        edt.putFloat("long" , (float) longitude);
        edt.apply();

    }

    @Override
    public void onCameraMoveStarted(int i) {

        if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {

            isFirstTransition = false;

            TransitionManager.go(end , endSlide);





//            Animation animation = AnimationUtils.loadAnimation(getActivity() , R.anim.slide_out);
//            openNav.startAnimation(animation);
//            myLocation.startAnimation(animation);


        }



    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.opennav) {

            SqliteDatabase db = new SqliteDatabase(getActivity());
            //db.dropTable();

            String address = mlp.getFullAddress(latitude , longitude);

            if (address !=null) {

                customDialog = new CustomDialog(getActivity());
                customDialog.setView(R.layout.park_details);
                customDialog.setSize(300 , 300);
                customDialog.create();
                customDialog.show();

                TextView close = (TextView) customDialog.findViewById(R.id.close);
                EditText name = (EditText) customDialog.findViewById(R.id.name);
                TextView time = (TextView) customDialog.findViewById(R.id.time);
                TextView date = (TextView) customDialog.findViewById(R.id.date);
                EditText addressText = (EditText) customDialog.findViewById(R.id.address);
                MaterialButton cnfPark = (MaterialButton) customDialog.findViewById(R.id.cnfpark);

                Calendar c = Calendar.getInstance();
                Date currentDate = c.getTime();
                Log.d(TAG, "Date: " + currentDate.toString());
                Log.d(TAG, "Address: "+ address);
                String dateText = new SimpleDateFormat("dd/MM/yyyy").format(currentDate);
                String timeText = new SimpleDateFormat("hh:mm a").format(currentDate);

                date.setText(dateText);
                time.setText(timeText);
                if (!address.equalsIgnoreCase("")) {

                    addressText.setText(address);

                } else {

                    addressText.setText("Address not found.Enter manually");
                    addressText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }




                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        customDialog.dismiss();

                    }
                });

                cnfPark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SqliteDatabase db = new SqliteDatabase(getActivity());
                        long id = db.getLastIndex();


                        if ((name.getText().toString() != null && !name.getText().toString().equals(""))
                                && (date.getText().toString() != null && !date.getText().toString().equals(""))
                                && (time.getText().toString() != null && !time.getText().toString().equals(""))
                                && (addressText.getText().toString() != null && !addressText.getText().toString().equals(""))) {

                            try {

//                                Log.d(TAG, "Date: "+date.getText().toString().trim());



                                Park park1 = new Park();
                                park1.setId(id);
                                park1.setUid(getActivity().getSharedPreferences("details" , Context.MODE_PRIVATE).getString("contact" , ""));
                                park1.setName(name.getText().toString().trim());
                                park1.setTime(time.getText().toString().trim());
                                park1.setDate(date.getText().toString().trim());
                                park1.setLatitude(latitude);
                                park1.setLongitude(longitude);
                                park1.setAddress(addressText.getText().toString().trim());
                                park1.setTimeStamp(Long.toString(System.currentTimeMillis()/1000));

                                db.insert(park1);

                            } catch (Exception ex) {

                                Log.d(TAG, "onClick: " + ex.getMessage());


                            } finally {

                                db.close();
                                customDialog.dismiss();
                            }

                        } else {

                            Toast.makeText(getActivity(), "Please fill all the details", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }



        } else if (v.getId() == R.id.mylocation) {

            mlp.getLocation(networkLocationListener);

        }



    }

    public class MyNetworkLocationListener implements OnCompleteListener<Location> , OnFailureListener {


        @Override
        public void onComplete(@NonNull Task<Location> task) {

            if (task.isSuccessful()) {

                Log.d(TAG, "onComplete: " + "FusedTask");

                MapsFragment.this.networkLocation = task.getResult();

                if (networkLocation != null) {

                    if (googleMap != null) {

                        LatLng latLng = new LatLng(networkLocation.getLatitude(), networkLocation.getLongitude());

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng , 18));


                    } else {

                        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

                        if (map != null) {

                            map.getMapAsync(MapsFragment.this);
                        }


                    }


                } else {

                    mlp.getGPSLocation();

                }

            } else {

                mlp.getGPSLocation();
            }



        }


        @Override
        public void onFailure(@NonNull Exception e) {

            mlp.getGPSLocation();

        }



    }

    public void setLatLongListener(GetLatLong getLatLong) {

        this.getLatLong = getLatLong;


    }

   public interface GetLatLong{

        public void getLatLong(double latitude , double longitude);
   }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            if (customDialog != null) {

                customDialog.setSize(300 , 300);

            }



        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            if (customDialog != null) {

                customDialog.setSize(300 , 300);

            }

        }
    }

    private Bitmap loadMarker() {

        Bitmap bitmap = Bitmap.createBitmap(150 , 150 , Bitmap.Config.ARGB_8888);
        //Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap , bitmap.getWidth() , bitmap.getHeight() , true);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(android.R.color.holo_red_dark));

        try {



            Bitmap framebmp = BitmapFactory.decodeResource(getResources() , R.drawable.markerframe);
            Bitmap framebmpscaled = Bitmap.createScaledBitmap(framebmp , 120 , 120 , true);


            canvas.drawBitmap(framebmpscaled , 0, 0 , paint);

            Users users = new UserSqliteDatabase(getActivity())
                    .getUser(getActivity().getSharedPreferences("details" , Context.MODE_PRIVATE).getString("contact" , "") , 0);
            String imageName = users.getImage();

            if (imageName != null && !Utils.isBlank(imageName)) {

                File file = new File(imageName);
                Bitmap bmp = reduceSize(file , 120 , 120);
                Bitmap car = Bitmap.createScaledBitmap(bmp , 120 , 100 , true);
                canvas.drawBitmap(car , 0 , 0 , paint);

            } else {

                Bitmap bmp = BitmapFactory.decodeResource(getResources() , R.drawable.userpic);
                Bitmap car = Bitmap.createScaledBitmap(bmp , 70 , 70 , true);
                canvas.drawBitmap(car , 25 , 15 , paint);

            }

        } catch (Exception ex) {

            ex.printStackTrace();

            Bitmap bmp = BitmapFactory.decodeResource(getResources() , R.drawable.userpic);
            Bitmap car = Bitmap.createScaledBitmap(bmp , 70 , 70 , true);
            canvas.drawBitmap(car , 25 , 15 , paint);

        }

        return bitmap;
    }

    private Bitmap reduceSize(File file , int width , int height) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bmp = BitmapFactory.decodeFile(file.getPath() , options);
        int sampleSizeFactor = 1;

        int outWidth = options.outWidth;;
        int outHeight = options.outHeight;

        if (outHeight>height || outWidth>width) {

           int halfHeight = outHeight/2;
           int halfWidth = outWidth/2;

           while ((halfWidth/sampleSizeFactor)>width && (halfHeight/sampleSizeFactor)>height) {

               sampleSizeFactor*=2;
           }
        }

        options.inSampleSize = sampleSizeFactor;
        options.inJustDecodeBounds = false;

        Bitmap bmp1 = BitmapFactory.decodeFile(file.getPath() , options);
        return bmp1;


    }


}