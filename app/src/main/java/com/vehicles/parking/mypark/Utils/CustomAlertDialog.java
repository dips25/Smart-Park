package com.vehicles.parking.mypark.Utils;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.vehicles.parking.mypark.R;

public class CustomAlertDialog extends AlertDialog.Builder {

    public Context context;
    AlertDialog alertDialog;

    CustomAlertDialog a;

    int width , height;


    public CustomAlertDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }


    public void setLayout(int resId) {

      setView(resId);


    }

    public void setLayout(View resId) {

        setView(resId);


    }

    public void setImage(int resId) {

        setIcon(context.getResources().getDrawable(resId));

    }

    public void setSize(int width , int height) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        alertDialog.getWindow().setLayout(point.x , point.y/4);


    }

    public AlertDialog setSize(int width , int height , int custom) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        this.width = point.x;
        this.height = point.y;

        Log.d(CustomDialog.class.getName(), "setSize: " + this.width + " " + this.height);



        alertDialog.getWindow().setLayout(this.width , this.height);
        return alertDialog;


    }

    @NonNull
    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public AlertDialog build() {

       alertDialog = this.create();
       return alertDialog;
    }
}
