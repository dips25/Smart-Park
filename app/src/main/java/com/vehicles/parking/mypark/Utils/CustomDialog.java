package com.vehicles.parking.mypark.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class CustomDialog extends Dialog {

    Context context;
    public CustomDialog(@NonNull Context context) {

        super(context);
        this.context = context;
    }

    public CustomDialog setView(int resId) {

        this.setContentView(resId);
        return this;
    }

    public CustomDialog setSize(int width , int height) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        this.getWindow().setLayout(point.x , point.y-100);

        return this;

    }

    public CustomDialog setSize(int width , int height , int custom) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        this.getWindow().setLayout(point.x , (point.y/custom));

        return this;

    }
}
