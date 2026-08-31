package com.vehicles.parking.mypark.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vehicles.parking.mypark.R;

public class MyUndoView extends View {

    Canvas c;

    RectF leftButton;
    public MyUndoView(Context context) {
        super(context);
        setClickable(true);

        requestPointerCapture();

        //this.setOnClickListener(this);
    }

    public MyUndoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyUndoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyUndoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }



    @Override
    protected void onDraw(@NonNull Canvas c) {
        super.onDraw(c);

        this.c = c;
        leftButton = new RectF(0 , 0 , 300 , 300);
        Paint p = new Paint();
        p.setColor(Color.TRANSPARENT);
        c.drawRect(leftButton , p);
        drawText("UNDO", c, leftButton, p);
    }

    public void createRect(int left , int top , int right , int bottom) {

        invalidate();

    }

    public void setOnClickedListener() {

       if (leftButton.contains(30 , 40)) {

           Toast.makeText(getContext(), "True", Toast.LENGTH_SHORT).show();
       }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                return false;

            case MotionEvent.ACTION_UP:

                Toast.makeText(getContext(), "Event: " + event.getX(), Toast.LENGTH_SHORT).show();
                requestPointerCapture();
                //onClickedListener.onItemClicked();
                return false;



                }



        return false;

    }

    @Override
    public boolean onCapturedPointerEvent(MotionEvent event) {

        Log.d(MyUndoView.class.getName(), "onCapturedPointerEvent: " + event.getX() + " " + event.getY());

        return true;

    }

    //    private void drawButtons(Canvas c) {
//
//        int buttonWidth = 300;
//
//
//        //Bitmap bitmap = Bitmap.createBitmap(300 , 200 , Bitmap.Config.ARGB_8888);
//
//
//
//        float buttonWidthWithoutPadding = buttonWidth - 20;
//        float corners = 16;
//
//
//
//        Paint p = new Paint();
//
//        RectF leftButton = new RectF(0, 0, 200 , 500);
//        p.setColor(Color.RED);
//        c.drawRoundRect(leftButton, corners, corners, p);
//        drawText("UNDO", c, leftButton, p);
//
////        RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
////        p.setColor(Color.RED);
////        c.drawRoundRect(rightButton, corners, corners, p);
////        drawText("DELETE", c, rightButton, p);
//
////        if (buttonstate == SwipeController.ButtonState.VISIBLE) {
////            buttonInstance = leftButton;
////        }
//
////        else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
////            buttonInstance = rightButton;
////        }
//    }
//
    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 30;
        p.setColor(Color.RED);
        p.setAntiAlias(true);
        p.setTextSize(textSize);
        p.setTypeface(getResources().getFont(R.font.regular));


        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p);
    }




}
