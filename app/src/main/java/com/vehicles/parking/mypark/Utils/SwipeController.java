package com.vehicles.parking.mypark.Utils;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.vehicles.parking.mypark.Adapters.ParkAdapter;

public class SwipeController extends ItemTouchHelper.Callback {

    private ButtonState buttonstate = ButtonState.GONE;
    private static final int buttonWidth = 300;
    private boolean swipeBack = false;

    RectF buttonInstance = null;

    private RecyclerView.ViewHolder currentItemViewHolder;
    RecyclerView recyclerView;

    View fItemView;

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        currentItemViewHolder = viewHolder;
        this.recyclerView = recyclerView;
        this.fItemView = viewHolder.itemView;



//        if (((ParkAdapter.ViewHolder) viewHolder).getFlag()) {
//
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            drawButtons(c , viewHolder);
//            setTouchListener(c , recyclerView , viewHolder , dX , dY , actionState , isCurrentlyActive);
//
//        } else {
//
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            ((ParkAdapter.ViewHolder) viewHolder).setFlag(true);
//            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//
//
//        }

        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonstate != ButtonState.GONE) {


                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                drawButtons(c , viewHolder);
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                //setOnScrollListsner(c , recyclerView , viewHolder);
            }
            else {
                super.onChildDraw(c , recyclerView , viewHolder , dX , dY , actionState , isCurrentlyActive);
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }





        }

   



    private void drawButtons(Canvas c , RecyclerView.ViewHolder viewHolder) {


            float buttonWidthWithoutPadding = buttonWidth - 20;
            float corners = 16;


            View itemView = viewHolder.itemView;
            Paint p = new Paint();

            RectF leftButton = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom());
            p.setColor(Color.RED);
            c.drawRoundRect(leftButton, corners, corners, p);
            drawText("UNDO", c, leftButton, p);

//        RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
//        p.setColor(Color.RED);
//        c.drawRoundRect(rightButton, corners, corners, p);
//        drawText("DELETE", c, rightButton, p);

            if (buttonstate == ButtonState.VISIBLE) {
                buttonInstance = leftButton;
            }

//        else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
//            buttonInstance = rightButton;
//        }
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 60;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        return makeMovementFlags(0, ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    private void setTouchListener(final Canvas c,
                                  final RecyclerView recyclerView,
                                  final RecyclerView.ViewHolder viewHolder,
                                  final float dX, final float dY,
                                  final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                // if (swipeBack) {
                // if (dX > buttonWidth) {

                buttonstate = ButtonState.VISIBLE;



                // }


                //  }
                return false;
            }
        });

        if (buttonstate != ButtonState.GONE) {
            setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            //setItemsClickable(recyclerView, false);
        }
    }

    private void setTouchDownListener(final Canvas c,
                                      final RecyclerView recyclerView,
                                      final RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY,
                                      final int actionState, final boolean isCurrentlyActive) {

        setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        recyclerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//                }
//                return false;
//            }
//        });
    }

    private void setTouchUpListener(final Canvas c,
                                    final RecyclerView recyclerView,
                                    final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY,
                                    final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {


                    if (buttonInstance.contains(event.getX(), event.getY())) {

                        Toast.makeText(v.getContext(), "Clicked", Toast.LENGTH_SHORT).show();

//                        Slide endSlide = new Slide(Gravity.END);
//                        endSlide.setDuration(200);

                        //Scene scene = new Scene((ViewGroup) viewHolder.itemView);

                        //Scene start = Scene.getSceneForLayout((ViewGroup) v , R.layout.buttons_invisible , v.getContext());
//                       Scene end = Scene.getSceneForLayout((ViewGroup) recyclerView.getLayoutManager().getChildAt(0) , R.layout.single_item_park , v.getContext());
//                        TransitionManager.go(end , endSlide);


                        SwipeController.super.onChildDraw(c, recyclerView, viewHolder, 0, dY, 0, false);

                    }
//                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//
//
//
//
//                            return true;
//                        }
                    //   });
                    //setItemsClickable(recyclerView, true);
                    //swipeBack = false;
                    //buttonstate = ButtonState.GONE;
                }
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView,
                                   boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }


//    public void onDraw(Canvas c) {
//
//        if (currentItemViewHolder != null) {
//            drawButtons(c, currentItemViewHolder);
//        }
//
//
//    }


        enum ButtonState {

            GONE, VISIBLE;
        }
}

