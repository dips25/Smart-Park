package com.vehicles.parking.mypark.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.transition.Scene;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vehicles.parking.mypark.DataBase.SqliteDatabase;
import com.vehicles.parking.mypark.Models.Park;
import com.vehicles.parking.mypark.MyLocationProvider;
import com.vehicles.parking.mypark.R;
import com.vehicles.parking.mypark.databinding.ActivityParkMapsBinding;
import com.vehicles.parking.mypark.fragments.MapsFragment;

import java.io.File;

public class ParkMapsActivity extends FragmentActivity implements OnMapReadyCallback
        , GoogleMap.OnCameraIdleListener , LocationListener , GoogleMap.OnCameraMoveStartedListener , View.OnClickListener{

    private GoogleMap mMap;

    SqliteDatabase db;
    Intent intent;
    Park park;
    double latitude , longitude;

    Location networkLocation , gpsLocation;

    MyLocationProvider mlp;

    FrameLayout openNav , mylocation;

    ViewGroup sceneroot;

    Scene start , end;

    boolean isFirstTransition = true;

    Transition startSlide , endSlide;
    MyNetworkLocationListener networkLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_maps);

        sceneroot = (RelativeLayout) findViewById(R.id.button_root);
        openNav = (FrameLayout) sceneroot.findViewById(R.id.opennav);
        mylocation = (FrameLayout) sceneroot.findViewById(R.id.mylocation);
        openNav.setOnClickListener(this);
        mylocation.setOnClickListener(this);
        openNav.setClickable(false);
        mylocation.setClickable(false);

        networkLocationListener = new MyNetworkLocationListener();

        mlp = new MyLocationProvider(ParkMapsActivity.this);
        mlp.setListener(this);
        mlp.getLocation(networkLocationListener);




        start = Scene.getSceneForLayout(sceneroot , R.layout.buttons_visible , ParkMapsActivity.this);
        end = Scene.getSceneForLayout(sceneroot , R.layout.buttons_invisible , ParkMapsActivity.this);

        startSlide = new Slide(Gravity.END);
        startSlide.setDuration(200);
        endSlide = new Slide(Gravity.END);
        endSlide.setDuration(200);

        intent = getIntent();
        if (intent != null) {

            park =(Park) intent.getSerializableExtra("park");

            if (park != null) {

                latitude= park.getLatitude();
                longitude = park.getLongitude();

            }

        }

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        if (networkLocation != null) {

            if (latitude != 0 && longitude != 0) {

                LatLng latlng = new LatLng(latitude, longitude);
                LatLng myLatLng = new LatLng(networkLocation.getLatitude() , networkLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng , 12));
                mMap.addMarker(new MarkerOptions().position(latlng)
                        .icon(BitmapDescriptorFactory.fromBitmap(loadMarker())));

                Bitmap bitmap = BitmapFactory.decodeResource(getResources() , R.drawable.flag);
                Bitmap sBitmap = Bitmap.createScaledBitmap(bitmap , 80 , 80 , true);

                mMap.addMarker(new MarkerOptions().position(myLatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(sBitmap)));
                mMap.setOnCameraIdleListener(this);
                mMap.setOnCameraMoveStartedListener(this);
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        return true;
                    }
                });
                openNav.setClickable(true);
                mylocation.setClickable(true);


            }


        } else if (gpsLocation != null) {

            if (latitude != 0 && longitude != 0) {

                LatLng latlng = new LatLng(latitude, longitude);
                LatLng myLatLng = new LatLng(gpsLocation.getLatitude() , gpsLocation.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng , 12));
                mMap.addMarker(new MarkerOptions().position(latlng)
                        .icon(BitmapDescriptorFactory.fromBitmap(loadMarker())));

                Bitmap bitmap = BitmapFactory.decodeResource(getResources() , R.drawable.flag);
                Bitmap sBitmap = Bitmap.createScaledBitmap(bitmap , 80 , 80 , true);

                mMap.addMarker(new MarkerOptions().position(myLatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(sBitmap)));
                mMap.setOnCameraIdleListener(this);
                mMap.setOnCameraMoveStartedListener(this);
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        return true;
                    }
                });
                openNav.setClickable(true);
                mylocation.setClickable(true);


            }

        }

    }

    @Override
    public void onCameraIdle() {

        if (!isFirstTransition) {

            TransitionManager.go(start , startSlide);

        }

        openNav = (FrameLayout) sceneroot.findViewById(R.id.opennav);
        mylocation = (FrameLayout) sceneroot.findViewById(R.id.mylocation);

        openNav.setOnClickListener(this);
        mylocation.setOnClickListener(this);

    }

    private Bitmap loadMarker() {

        Bitmap bitmap = Bitmap.createBitmap(150 , 150 , Bitmap.Config.ARGB_8888);
        //Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap , bitmap.getWidth() , bitmap.getHeight() , true);
        Canvas canvas = new Canvas(bitmap);


        Paint paint = new Paint();
        paint.setColor(getResources().getColor(android.R.color.holo_red_dark));

        Bitmap framebmp = BitmapFactory.decodeResource(getResources() , R.drawable.markerframe);
        Bitmap framebmpscaled = Bitmap.createScaledBitmap(framebmp , 100 , 100 , true);


        canvas.drawBitmap(framebmpscaled , 0, 0 , paint);
        Bitmap bmp = BitmapFactory.decodeResource(getResources() , R.drawable.car);
        Bitmap car = Bitmap.createScaledBitmap(bmp , 70 , 70 , true);
        canvas.drawBitmap(car , 10 , 5 , paint);

        return bitmap;

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        gpsLocation = location;

        if (gpsLocation != null) {

            if (mMap == null) {

                SupportMapFragment map = (SupportMapFragment) ParkMapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.map);

                if (map != null) {

                    map.getMapAsync(ParkMapsActivity.this);
                }


            } else {

                LatLng latlng = new LatLng(gpsLocation.getLatitude() , gpsLocation.getLongitude());

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng , 15));
            }


        }

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

            if (openNav.isClickable()) {

                if (latitude!=0 && longitude!=0) {

                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+Double.toString(latitude)+","+Double.toString(longitude)+"&mode=w");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);


                }
            }

        } else if (v.getId() == R.id.mylocation) {

            if (mylocation.isClickable()) {

                mlp.getLocation(networkLocationListener);
            }

        }

    }

    public class MyNetworkLocationListener implements OnCompleteListener<Location>, OnFailureListener {


        @Override
        public void onComplete(@NonNull Task<Location> task) {

            if (task.isSuccessful()) {

                ParkMapsActivity.this.networkLocation = task.getResult();

                if (networkLocation != null) {

                    if (mMap == null) {

                        SupportMapFragment map = (SupportMapFragment) ParkMapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.map);

                        if (map != null) {

                            map.getMapAsync(ParkMapsActivity.this);
                        }


                    } else {

                        LatLng latlng = new LatLng(networkLocation.getLatitude() , networkLocation.getLongitude());

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng , 15));
                    }


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

//    fun decodePolyline(encoded: String): List<LatLng> {
//        val poly = ArrayList<LatLng>()
//        var index = 0
//        val len = encoded.length
//        var lat = 0
//        var lng = 0
//        while (index < len) {
//            var b: Int
//            var shift = 0
//            var result = 0
//            do {
//                b = encoded[index++].code - 63
//                result = result or (b and 0x1f shl shift)
//                shift += 5
//            } while (b >= 0x20)
//            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
//            lat += dlat
//            shift = 0
//            result = 0
//            do {
//                b = encoded[index++].code - 63
//                result = result or (b and 0x1f shl shift)
//                shift += 5
//            } while (b >= 0x20)
//            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
//            lng += dlng
//            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
//            poly.add(latLng)
//        }
//        return poly
//    }

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


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}