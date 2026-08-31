package com.vehicles.parking.mypark.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.vehicles.parking.mypark.DataBase.SqliteDatabase;
import com.vehicles.parking.mypark.DataBase.UserSqliteDatabase;
import com.vehicles.parking.mypark.MainActivity;
import com.vehicles.parking.mypark.Models.Users;
import com.vehicles.parking.mypark.MyLocationProvider;
import com.vehicles.parking.mypark.R;
import com.vehicles.parking.mypark.Utils.CustomDialog;
import com.vehicles.parking.mypark.Utils.MyTextInputEditText;
import com.vehicles.parking.mypark.Utils.Utils;
import com.vehicles.parking.mypark.activities.LoginActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements LocationListener {

    private static final int CAMERA_CODE = 100;
    private static final int GALLERY_CODE = 200;

    TextView name , contact;
    TextView address;
    MyLocationProvider mlp;

    Location networkLocation , gpsLocation;

    double latitude , longitude;
    
    Button save;
    ImageView profileImage;

    LinearLayout camera , gallery;

    File cameraFile;
    Uri finalUri;

    boolean isPerms;
    String imageName = "";

    String contactNo;

    RelativeLayout profile_root;
    ImageView menuIcon;
    ImageView backgroundImage;
    CircleImageView addressImg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile , container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mlp = new MyLocationProvider(getActivity());
        mlp.setListener(ProfileFragment.this);
        mlp.getLocation(new ProfileFragment.MyNetworkLocationListener());

        name = (TextView) view.findViewById(R.id.edt_name);
        contact = (TextView) view.findViewById(R.id.edt_contact);
        address = (TextView) view.findViewById(R.id.edt_address);
        profileImage = (CircleImageView) view.findViewById(R.id.profile_image);
        save = (Button) view.findViewById(R.id.btn_save);
        menuIcon = (ImageView) view.findViewById(R.id.menu_icon);
        profile_root = (RelativeLayout) view.findViewById(R.id.profile_root);
        addressImg = (CircleImageView) view.findViewById(R.id.address_img);
        //backgroundImage = (ImageView) view.findViewById(R.id.backgroundImage);

        fetchData(getActivity().getSharedPreferences("details" , Context.MODE_PRIVATE).getString("contact" , ""));

        contact.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                contact.setInputType(InputType.TYPE_NULL);

                return true;
            }
        });

//        contact.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                if (s.length() > 10) {
//
//                    contactNo = s.toString().substring(0 , 10);
//                    //Log.d(ProfileFragment.class.getName(), "onTextChanged: " + s.toString().substring(count-1 , count));
//                    contact.setText(contactNo);
//                    contact.setSelection(contactNo.length());
//
//                    Toast.makeText(getActivity() , "Can't enter more than ten digits" , Toast.LENGTH_SHORT).show();
//                    return;
//
//                }
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//
//
//            }
//        });
        
        setOnClickListeners();

        addressImg.setOnClickListener((v)->{

            InputMethodManager inputMethodManager =
                    (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(
                    address.getApplicationWindowToken(),
                    InputMethodManager.RESULT_HIDDEN, 0);

            address.setText("Detecting Location...");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (networkLocation != null) {

                        address.setText(mlp.getFullAddress(networkLocation.getLatitude() , networkLocation.getLongitude()));
                        address.requestFocus();
                        //address.setSelection(address.getText().length());

                    } else if (gpsLocation != null) {

                        address.setText(mlp.getFullAddress(gpsLocation.getLatitude() , gpsLocation.getLongitude()));
                        address.requestFocus();
                        //address.setSelection(address.getText().length());

                    } else {

                        Toast.makeText(getContext(), "Can't detect location", Toast.LENGTH_SHORT).show();
                        address.requestFocus();
                        //address.setSelection(address.getText().length());
                    }

                }
            } , 5000);




        });

        //address.setShowSoftInputOnFocus(true);

//        address.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                address.setInputType(InputType.TYPE_CLASS_TEXT);
//                address.onTouchEvent(event);
//                return true;
//            }
//        });

//        address.setOnDrawableClickListener(new MyTextInputEditText.OnDrawableClickListener() {
//            @Override
//            public void onDrawableClicked(Position target) {
//
//                switch (target) {
//
//                    case RIGHT:
//
//                        InputMethodManager inputMethodManager =
//                                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                        inputMethodManager.toggleSoftInputFromWindow(
//                                address.getApplicationWindowToken(),
//                                InputMethodManager.RESULT_HIDDEN, 0);
//
//                        address.setText("Detecting Location...");
//
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                if (networkLocation != null) {
//
//                                    address.setText(mlp.getFullAddress(networkLocation.getLatitude() , networkLocation.getLongitude()));
//                                    address.requestFocus();
//                                    address.setSelection(address.getText().length());
//
//                                } else if (gpsLocation != null) {
//
//                                    address.setText(mlp.getFullAddress(gpsLocation.getLatitude() , gpsLocation.getLongitude()));
//                                    address.requestFocus();
//                                    address.setSelection(address.getText().length());
//
//                                } else {
//
//                                    Toast.makeText(getContext(), "Can't detect location", Toast.LENGTH_SHORT).show();
//                                    address.requestFocus();
//                                    address.setSelection(address.getText().length());
//                                }
//
//                            }
//                        } , 5000);
//
//                        break;
//
//                    case LEFT:
//                        InputMethodManager inputMethodManager1 =
//                                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                        inputMethodManager1.toggleSoftInputFromWindow(
//                                address.getApplicationWindowToken(),
//                                InputMethodManager.SHOW_FORCED, 0);
//
//                        address.requestFocus();
//                        address.setSelection(address.getText().length());
//                        break;
//
//
//
//
//                }
//
//            }
//        });

    }

    private void fetchData(String contact) {

        try {

            UserSqliteDatabase userSqliteDatabase = new UserSqliteDatabase(getActivity());

            Users users = userSqliteDatabase.getUser(contact);

            if (users != null) {

                name.setText(users.getName());
                this.contact.setText(users.getContact());
                address.setText(users.getAddress());
                imageName = users.getImage();

                if (Utils.isBlank(imageName)) {

                    int id = getActivity().getResources().getIdentifier("userpic" , "drawable" , getActivity().getPackageName());
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources() , id);
                    profileImage.setImageBitmap(bitmap);

                } else {

                    File fFile = new File(imageName);
                    Bitmap bitmap = BitmapFactory.decodeFile(fFile.getPath());
                    profileImage.setImageBitmap(bitmap);
                    //backgroundImage.setImageBitmap(bitmap);

                }

            } else {

                Toast.makeText(getActivity(), "Unable to fetch details.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {

            int id = getActivity().getResources().getIdentifier("userpic" , "drawable" , getActivity().getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(getResources() , id);
            profileImage.setImageBitmap(bitmap);


        }




    }

    private void setOnClickListeners() {

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(getActivity() , menuIcon);
                popupMenu.getMenuInflater().inflate(R.menu.logout_menu , popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.logout) {

                            getActivity().getSharedPreferences("details" , Context.MODE_PRIVATE)
                                    .edit().putBoolean("isLoggedIn" , false).apply();

                            getActivity().getSharedPreferences("details" , Context.MODE_PRIVATE)
                                    .edit().putString("contact" , "").apply();

                            Intent intent = new Intent(getActivity() , LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);


//                            SqliteDatabase db = new SqliteDatabase(context);
//                            db.deletePark(park.getId());
//                            parkArrayList.remove(park);
//                            notifyDataSetChanged();

                            return true;
                        }

                        return false;

                    }
                });


            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contactNo = contact.getText().toString();

//                Pattern pattern = Pattern.compile("[789]\\d{9}");
//                Matcher matcher = pattern.matcher(contactNo);
//                if (!matcher.matches()) {
//
//                    Toast.makeText(getActivity(), "Please enter valid number", Toast.LENGTH_SHORT).show();
//                    return;
//
//                }

                if ((name.getText().toString()!=null && !Utils.isBlank(name.getText().toString()))
                &&(contact.getText().toString()!=null && !Utils.isBlank(contact.getText().toString()))
                &&(address.getText().toString()!=null && !Utils.isBlank(address.getText().toString()))) {

                    UserSqliteDatabase user = new UserSqliteDatabase(getActivity());
                    Users users = new Users();
                    users.setId(user.getLastIndex());
                    users.setName(name.getText().toString().trim());
                    users.setContact(contactNo.trim());
                    users.setAddress(address.getText().toString().trim());
                    users.setImage(imageName);

                    if (user.updateUser(users)) {

                        //Toast.makeText(getActivity(), "Profile updated", Toast.LENGTH_SHORT).show();


                    } else {

                        Toast.makeText(getActivity(), "Unable to update", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(getActivity(), "Enter all the details", Toast.LENGTH_SHORT).show();
                }
            }
        });



        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((MainActivity)getActivity()).requestPerms()) {

                    Dialog dialogBox = new Dialog(getActivity());
                    dialogBox.setContentView(R.layout.bottomsheet_camera_gallery);

                    if(dialogBox.getWindow() != null){
                        dialogBox.getWindow().setBackgroundDrawable(
                                new ColorDrawable(Color.TRANSPARENT));
                    }



                    dialogBox.create();
                    dialogBox.show();

                    LinearLayout camera = dialogBox.findViewById(R.id.layoutCamera);
                    LinearLayout gallery = dialogBox.findViewById(R.id.layoutGallery);
                    MaterialButton cancel = dialogBox.findViewById(R.id.btnCancel);

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialogBox.dismiss();


                        }
                    });


                    camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            try {

                                cameraFile = Utils.createNewFile(getActivity());
                                finalUri = FileProvider.getUriForFile(getActivity() , "com.vehicles.parking.mypark.fileprovider" , cameraFile);
                                intent1.putExtra(MediaStore.EXTRA_OUTPUT , finalUri);
                                startActivityForResult(intent1 , CAMERA_CODE);


                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    });

                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                            galleryIntent.setType("image/*");
                            startActivityForResult(Intent.createChooser(galleryIntent , "Choose Image") , 200);

                        }

                    });


                } else {

                    ((MainActivity)getActivity()).requestPerms();
                }

    }
    });
}


    @Override
    public void onLocationChanged(@NonNull Location location) {

        this.gpsLocation = location;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {

            Uri uri = data.getData();
            imageName = getGalleryImage(uri);


            try {

                InputStream is = getContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                profileImage.setImageBitmap(bitmap);
                //File file =  Utils.saveImage(getActivity() , image , uri);

                //Uri uri1 = FileProvider.getUriForFile(getActivity() , "com.vehicles.parking.mypark" , file);

//                Intent intent = new Intent(getActivity() , PicUpdateService.class);
//                intent.putExtra("file" , uri1.toString());
//                ContextCompat.startForegroundService(getActivity() , intent);

            } catch (IOException e) {
                //throw new RuntimeException(e);
            }


        } else if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {

            try {
                Utils.saveImage(getActivity() , finalUri.toString() , finalUri);

                InputStream is = getContext().getContentResolver().openInputStream(finalUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                profileImage.setImageBitmap(bitmap);
                imageName = cameraFile.getPath();

//                Intent intent = new Intent(getActivity() , PicUpdateService.class);
//                intent.putExtra("file" , finalUri.toString());
//
//                ContextCompat.startForegroundService(getActivity() , intent);



            } catch (IOException e) {
                //throw new RuntimeException(e);
            }
        }


    }

    private String getGalleryImage(Uri uri) {

        String data = MediaStore.Images.Media.DATA;

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().getContentResolver().query(uri , projection , null , null , null);

        cursor.moveToFirst();

        int columnindex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        String imageName = cursor.getString(columnindex);

        return imageName;

    }






    public class MyNetworkLocationListener implements OnCompleteListener<Location>, OnFailureListener {


        @Override
        public void onComplete(@NonNull Task<Location> task) {

            if (task.isSuccessful()) {

                ProfileFragment.this.networkLocation = task.getResult();


            } else {

                mlp.getGPSLocation();
            }



        }


        @Override
        public void onFailure(@NonNull Exception e) {

            mlp.getGPSLocation();

        }



    }

}
