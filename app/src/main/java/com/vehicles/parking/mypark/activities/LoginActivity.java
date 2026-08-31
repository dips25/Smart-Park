package com.vehicles.parking.mypark.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.vehicles.parking.mypark.DataBase.SqliteDatabase;
import com.vehicles.parking.mypark.DataBase.UserSqliteDatabase;
import com.vehicles.parking.mypark.MainActivity;
import com.vehicles.parking.mypark.Models.Users;
import com.vehicles.parking.mypark.R;
import com.vehicles.parking.mypark.Utils.CustomAlertDialog;
import com.vehicles.parking.mypark.Utils.CustomDialog;
import com.vehicles.parking.mypark.Utils.Utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText name;

    EditText contact;


    MaterialButton save;

    boolean hasPerms = false;

    ActivityResultLauncher readSMSPerms;
    LinearLayout rootLayout;
    String number = "";
    String[] perms = {Manifest.permission.READ_SMS , Manifest.permission.READ_PHONE_NUMBERS , Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name = (EditText) findViewById(R.id.edt_name);
        contact = (EditText) findViewById(R.id.edt_contact);
        save = (MaterialButton) findViewById(R.id.btnLogin);
//        rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
        //save.setClickable(false);

        Log.d(LoginActivity.class.getName(), getSharedPreferences("details" , MODE_PRIVATE)
                .getBoolean("isLoggedIn" , false)+"");

//        if (getSharedPreferences("details" , MODE_PRIVATE).getBoolean("isLoggedIn" , false)) {
//
//            Intent intent = new Intent(LoginActivity.this , MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//
//        } else {
//
//            save.setClickable(true);
//
//
//        }


        readSMSPerms = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions() ,
                (isGranted)->{
//
            if (isGranted.containsValue(false)) {

                CustomDialog customDialog = new CustomDialog(LoginActivity.this);
                customDialog.setView(R.layout.permissions_dialog);
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
            //else {
//
//                hasPerms = true;
//            }


                });



//        contact.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                contact.setInputType(InputType.TYPE_NULL);
//                return true;
//            }
//        });

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
//                    Toast.makeText(LoginActivity.this , "Can't enter more than ten digits" , Toast.LENGTH_SHORT).show();
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


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getSharedPreferences("details",Context.MODE_PRIVATE).getBoolean("isLoggedIn" , false)
        && checkLocationPerms()) {

            //goToMainActivity

            Intent intent = new Intent(LoginActivity.this , MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else if (getSharedPreferences("details",Context.MODE_PRIVATE).getBoolean("isLoggedIn" , false)
        && !checkLocationPerms()){

            //goToLocationPermissionActivity

            Intent intent = new Intent(LoginActivity.this , LocationPermissionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }

    private void setOnClickListeners() {

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Utils.isBlank(name.getText().toString().trim())
                        &&!Utils.isBlank(contact.getText().toString().trim())) {

                    initiateLogin(name.getText().toString(),contact.getText().toString().trim());

                }



            }
        });


    }

//    private void login() {
//
//        if (Utils.isBlank(contact.getText().toString())) {
//
//            if (checkPerms()) {
//
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
//                    SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
//                    List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
//
//
//
//                    Log.d("Test", "Current list = " + subsInfoList);
//
//                    for (SubscriptionInfo subscriptionInfo : subsInfoList) {
//
//                        number = subscriptionInfo.getNumber();
//
//                        if (number != null) {
//
//                            break;
//                        }
//
//
//
//                        Log.d("Test", " Number is  " + number);
//                    }
//
//                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(LoginActivity.this);
//                    //customAlertDialog.setLayout(R.layout.phone_layout);
//                    RelativeLayout relativeLayout = new RelativeLayout(LoginActivity.this);
//                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//                    lp.setMargins(Utils.PixeltoDp(this,2) , Utils.PixeltoDp(this , 2) , Utils.PixeltoDp(this,2) , Utils.PixeltoDp(this,2));
//                    TextView tv = new TextView(LoginActivity.this);
//                    tv.setText("Want to proceed with " + number + "?");
//                    tv.setLayoutParams(lp);
//                    relativeLayout.addView(tv);
//
//
//                    customAlertDialog.setLayout(relativeLayout);
//
//                    customAlertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//
//                            contact.setText(number);
//
//
//                        }
//                    });
//                    customAlertDialog.setCancelable(false);
//
//                    AlertDialog alertDialog = customAlertDialog.create();
//                    alertDialog.show();
//
//                }
//
////                TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
////                String number = telephony.getSubscriberId();
//
//
//            }
//
//
//        } else {
//
//            initiateLogin();
//        }
//
//
//    }

    private void initiateLogin(String name,String contactNo) {

//        contactNo = contact.getText().toString();

//        Pattern pattern = Pattern.compile("[789]\\d{9}");
//        Matcher matcher = pattern.matcher(contactNo.trim());
//        if (!matcher.matches()) {
//
//            Toast.makeText(LoginActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
//            return;
//
//        }

        if (!Utils.isBlank(name.trim())&&!Utils.isBlank(contactNo.trim())) {

            UserSqliteDatabase db = new UserSqliteDatabase(LoginActivity.this);
            SqliteDatabase db1 = new SqliteDatabase(LoginActivity.this);

            Users users = db.getUser(contactNo.trim() , 0);

            if (users != null) {

                SharedPreferences sharedPreferences = getSharedPreferences("details" , Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("isLoggedIn" , true).apply();
                sharedPreferences.edit().putString("contact" , contactNo.trim()).apply();

                Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                startActivity(intent);

            } else {

                Users newuser = new Users();
                newuser.setId(db.getLastIndex());
                newuser.setName(name.trim());
                newuser.setContact(contactNo.trim());
                newuser.setAddress("");
                newuser.setImage("");

                if (db.insert(newuser)) {

                    SharedPreferences sharedPreferences = getSharedPreferences("details" , Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean("isLoggedIn" , true).apply();
                    sharedPreferences.edit().putString("contact" , contactNo.trim()).apply();

                    if (checkLocationPerms()) {

                        Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);


                    } else {

                        Intent intent = new Intent(LoginActivity.this , LocationPermissionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);


                    }

                }

            }

        } else {

            Toast.makeText(LoginActivity.this, "Enter all the details", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean checkLocationPerms() {

        return (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public boolean checkPerms() {

        for (String s : perms) {

            if (ContextCompat.checkSelfPermission(this , s) == PackageManager.PERMISSION_GRANTED) {

                hasPerms = true;

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this , s)) {

//                CustomDialog customDialog = new CustomDialog(LoginActivity.this);
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
//                        readSMSPerms.launch(perms);
//                        customDialog.dismiss();
//
//                    }
//                });
//
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                       customDialog.dismiss();
//
//                    }
//                });
                readSMSPerms.launch(perms);
                return false;

            } else {

                readSMSPerms.launch(perms);
                return false;
            }


        }

        return hasPerms;


    }

    public void requestPerms() {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

//            for (String s : perms) {

                if (ContextCompat.checkSelfPermission(this , Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    //readSMSPerms.launch(Manifest.permission.CAMERA);
//                    return;

                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

                    //readSMSPerms.launch(Manifest.permission.CAMERA);


                } else {

                    hasPerms = true;
                }
            //}


       // }
//        else {
//
//            for (String s : perms) {
//
//                if (ContextCompat.checkSelfPermission(this , s) != PackageManager.PERMISSION_GRANTED) {
//
//                    hasPerms = false;
//                    ActivityCompat.requestPermissions(this , perms , 100);
//                    break;
//
//                } else {
//
//                    hasPerms = true;
//                }
//            }
//
//
//        }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//
//        if (requestCode == 100) {
//
//               login();
//            }
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }


}



