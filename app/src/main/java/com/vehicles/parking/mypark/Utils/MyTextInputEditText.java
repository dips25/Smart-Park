package com.vehicles.parking.mypark.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.textfield.TextInputEditText;

public class MyTextInputEditText extends AppCompatEditText {

    Drawable right;

    int rwidth , rheight;

    int newX , newY;

    Context context;

    Rect bounds;
    Rect keyBoardBounds;

    OnDrawableClickListener onDrawableClickListener;
    public MyTextInputEditText(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public MyTextInputEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextInputEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setOnDrawableClickListener(OnDrawableClickListener onDrawableClickListener) {

        this.onDrawableClickListener = onDrawableClickListener;

    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {

        if (right != null) {

            this.right = right;
        }
        super.setCompoundDrawables(left, top, right, bottom);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                if (right != null) {

                    bounds = right.getBounds();
                }

                Log.d(MyTextInputEditText.class.getName(), "onTouchEvent: " + bounds);

                bounds = new Rect(getWidth()-60 , 0 , getWidth() , getHeight());
                keyBoardBounds = new Rect(0 , 0 , getWidth()-bounds.width() , getHeight());

                return true;

            case MotionEvent.ACTION_UP:

                   if (bounds.contains(x , y)) {

//                       this.setShowSoftInputOnFocus(true);
//                       this.setInputType(InputType.TYPE_CLASS_TEXT);

                       onDrawableClickListener.onDrawableClicked(OnDrawableClickListener.Position.RIGHT);

                       return true;

                   } else if (keyBoardBounds.contains(x , y)) {

                       onDrawableClickListener.onDrawableClicked(OnDrawableClickListener.Position.LEFT);
                       return true;
                   }

        }

        return false;
    }

    public interface OnDrawableClickListener{

        enum Position {LEFT , RIGHT , TOP , BOTTOM}

        public void onDrawableClicked(Position target);


    }
}
