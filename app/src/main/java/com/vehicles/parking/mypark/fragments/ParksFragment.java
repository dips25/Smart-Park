package com.vehicles.parking.mypark.fragments;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import com.vehicles.parking.mypark.Adapters.ParkAdapter;
import com.vehicles.parking.mypark.DataBase.SqliteDatabase;
import com.vehicles.parking.mypark.Models.Park;
import com.vehicles.parking.mypark.R;
import com.vehicles.parking.mypark.Utils.CustomDialog;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParksFragment extends Fragment {

    RecyclerView parkRecycler;

    boolean isDate;
    ArrayList<Park> parksArrayList = new ArrayList<>(5);
    ParkAdapter parkAdapter;

    LinearLayout rootLayout;

    int MAX_ITEM_COUNT;

    int VISIBLE_ITEMS = 10;

    int limit = VISIBLE_ITEMS;

    int skip = 0;

    int flastCount = 10;

    String strdate = null;

    ImageView menuIcon , refresh;

    boolean ifInitialState = true;

    String uid;

    //MySimpleSwipeController swipeController;
    ItemTouchHelper itemTouchHelper;

    int buttonWidth = 300;

    RecyclerView.ViewHolder viewHolder;

    int position;
    TextView noRectExt;

    GestureDetector gestureDetector;
    //MyRecyclerViewListener recyclerViewListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_park, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootLayout = (LinearLayout) view.findViewById(R.id.park_rootlayout);
        menuIcon = (ImageView) view.findViewById(R.id.menuicon);
        refresh = (ImageView) view.findViewById(R.id.refresh);
        noRectExt = (TextView) view.findViewById(R.id.norecText);
        uid = getActivity().getSharedPreferences("details" , Context.MODE_PRIVATE).getString("contact" , "");

        //swipeController = new MySimpleSwipeController();
        //itemTouchHelper = new ItemTouchHelper(swipeController);

        parkRecycler = (RecyclerView) view.findViewById(R.id.parks_recycler);


        //recyclerViewListener = new MyRecyclerViewListener();

        //gestureDetector = new GestureDetector(recyclerViewListener);

       // parkRecycler.setOnTouchListener(recyclerViewListener);

        parkAdapter = new ParkAdapter(getActivity(), parksArrayList, new ParkAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick(ParkAdapter.ViewHolder viewholder) {




            }
        });
        parkRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        parkRecycler.setAdapter(parkAdapter);
//        itemTouchHelper.attachToRecyclerView(null);
  //      itemTouchHelper.attachToRecyclerView(parkRecycler);








        loadItems(strdate , VISIBLE_ITEMS, skip , uid);

        setOnClickListeners();

        //swipeController.setOnTouchListener();






        parkRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Log.d(ParksFragment.class.getName(), "onScrolled: " + dy);

                if (dy > 0) {


                    int totalItemCount = ((LinearLayoutManager) recyclerView.getLayoutManager()).getItemCount();
                    int lastItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();



                    Toast.makeText(getActivity(), "Count: " + totalItemCount, Toast.LENGTH_SHORT).show();

                    if (lastItemPosition + 1 == totalItemCount) {

                        if (lastItemPosition + 1 == flastCount) {

                            Toast.makeText(getActivity(), "Scrolled", Toast.LENGTH_SHORT).show();

                            flastCount += VISIBLE_ITEMS;

                            skip += VISIBLE_ITEMS;

                            loadItems(strdate , VISIBLE_ITEMS, skip , uid);

                        }

                    }

                    Log.d(ParksFragment.class.getName(), "flastCount: " + flastCount);
                }


            }
        });
    }

    private void setOnClickListeners() {


        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.date) {

                            isDate = true;

                            flastCount  = 10;

                            skip = 0;

                            Calendar calendar = Calendar.getInstance();
                            Date date = calendar.getTime();

                            Log.d(ParksFragment.class.getName(), "onMenuItemClick: " + date.toString());

                            String simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy").format(date);

                            Log.d(ParksFragment.class.getName(), "onMenuItemClick: " + simpleDateFormat);

                            String[]datearr = simpleDateFormat.split("/");


                           DatePickerDialog dp = new DatePickerDialog(getActivity()
                                   , new DatePickerDialog.OnDateSetListener() {
                               @Override
                               public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                   int fmonth = month + 1;
                                   Pattern pattern = Pattern.compile("\\d{2}");
                                   Matcher daymatcher = pattern.matcher(Integer.toString(dayOfMonth));
                                   Matcher monthmmatcher = pattern.matcher(Integer.toString(fmonth));

                                   Log.d(ParksFragment.class.getName(), "onDateSet: " + daymatcher +" " + monthmmatcher);

                                   if (!daymatcher.matches() && !monthmmatcher.matches()) {

                                       strdate = "0"+Integer.toString(dayOfMonth) + "/" + "0"+Integer.toString(fmonth) + "/" + Integer.toString(year);

                                   } else if (!daymatcher.matches() && monthmmatcher.matches()){

                                       strdate = "0"+Integer.toString(dayOfMonth) + "/" + Integer.toString(fmonth) + "/" + Integer.toString(year);

                                   } else if (daymatcher.matches() && !monthmmatcher.matches()) {

                                       strdate = Integer.toString(dayOfMonth) + "/" + "0"+Integer.toString(fmonth) + "/" + Integer.toString(year);

                                   } else {

                                       strdate = Integer.toString(dayOfMonth) + "/" + Integer.toString(fmonth) + "/" + Integer.toString(year);

                                   }

                                   parksArrayList.clear();
                                   Log.d(ParksFragment.class.getName(), "onDateSet: " + strdate);
                                   loadItems(strdate , VISIBLE_ITEMS , skip , uid);

                               }
                           } , Integer.parseInt(datearr[2]) , Integer.parseInt(datearr[1])-1 , Integer.parseInt(datearr[0]));

                           dp.setOnCancelListener(new DialogInterface.OnCancelListener() {
                               @Override
                               public void onCancel(DialogInterface dialog) {

                                   parksArrayList.clear();
                                   flastCount  = 10;
                                   skip = 0;
                                   isDate = false;
                                   loadItems(null , VISIBLE_ITEMS , skip , uid);
                                   parkRecycler.scrollToPosition(0);

                               }
                           });

                           dp.show();

                            return true;

                        } else if (item.getItemId() == R.id.clear) {

                            if (parkRecycler != null) {

                                ProgressBar progressBar = new ProgressBar(getActivity());
                                progressBar.setIndeterminate(true);
                                progressBar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP_CONTENT , WRAP_CONTENT);
                                progressBar.setLayoutParams(lp);
                                lp.gravity = Gravity.CENTER;


                                SqliteDatabase sqliteDatabase = new SqliteDatabase(getActivity());

                                if (parkAdapter.getDeletelinkedList() != null && !parkAdapter.getDeletelinkedList().isEmpty()) {

                                    CustomDialog customDialog = new CustomDialog(getActivity());
                                    customDialog.setContentView(progressBar);
                                    customDialog.setCanceledOnTouchOutside(false);
                                    customDialog.show();

                                    for (Park park : parkAdapter.getDeletelinkedList()) {

                                        sqliteDatabase.deletePark(park.getId());


                                    }

                                    parksArrayList.removeAll(parkAdapter.getDeletelinkedList());
                                    parkAdapter.notifyDataSetChanged();
                                    parkAdapter.getDeletelinkedList().clear();

                                    customDialog.dismiss();




                                } else {

                                    Toast.makeText(getActivity(), "No Items selected.Swipe Items to select.", Toast.LENGTH_SHORT).show();
                                }

                                //deleteAll();

                                //parkRecycler.scrollToPosition(0);
                            }


                        }

                        return false;
                    }

                });
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (parkRecycler != null) {

                    parkRecycler.scrollToPosition(0);
                }

            }
        });

    }


    private void loadItems(String date , int limit, int skip , String uid) {


        SqliteDatabase db = new SqliteDatabase(getActivity());

        if (isDate) {

            parksArrayList.addAll(db.getAllParksByDate(date , limit , skip , uid));

        } else {

            parksArrayList.addAll(db.getAllParks(limit, skip , uid));

        }

        Log.d(ParksFragment.class.getName(), "ParkList: "+parksArrayList);



        if (parksArrayList != null) {

            if (!parksArrayList.isEmpty()) {

                parkRecycler.setVisibility(View.VISIBLE);
                parkAdapter.setItems(parksArrayList);
                noRectExt.setVisibility(GONE);



            } else {

               noRectExt.setVisibility(View.VISIBLE);
               parkRecycler.setVisibility(GONE);
            }
        } else {

            noRectExt.setVisibility(View.VISIBLE);
            parkRecycler.setVisibility(GONE);


        }


    }

    private void deleteAll() {

        String s = "";

        int count = parkRecycler.getLayoutManager().getItemCount();

        Log.d(ParksFragment.class.getName(), "deleteAll: " + count);

        for (int i = 0 ; i<count ; i++) {

            View child = parkRecycler.getLayoutManager().getChildAt(i);
            if (((RelativeLayout) child).getChildAt(0).getVisibility() == View.VISIBLE) {

                Log.d(ParksFragment.class.getName(), "deleteAll: " + "Visible");

            }


        }

        Log.d(ParksFragment.class.getName(), "deleteAll: " + s);
    }

//    private class MySimpleSwipeController extends ItemTouchHelper.Callback {
//
//        Canvas c;
//        int buttonWidth = 300;
//
//        int THRESHOLD_VELOCITY = 1000;
//
//        @Override
//        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//            return makeMovementFlags(0, ItemTouchHelper.RIGHT);
//        }
//
//        @Override
//        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            return false;
//        }
//
//        @Override
//        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//            ParksFragment.this.viewHolder = viewHolder;
//
//            position = viewHolder.getAdapterPosition();
//            if (parksArrayList.get(viewHolder.getAdapterPosition()).isRemove()) {
//
//                return;
//            }
//            ((Park) parksArrayList.get(viewHolder.getAdapterPosition())).setRemove(true);
//            ((ParkAdapter.ViewHolder)viewHolder).myUndoView.setVisibility(View.VISIBLE);
//
//            Log.d(ParksFragment.class.getName(), "onSwiped: " + ((ParkAdapter.ViewHolder)viewHolder).name.getText().toString());
//
//
//            Log.d(ParksFragment.class.getName(), "onSwiped: " + position);
//
//        }
//        @Override
//        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//
//           // super.onChildDraw(c, recyclerView, viewHolder, 0, dY, actionState, isCurrentlyActive);
//
//
//
//
//
//
//
//        }
//
//        private void setOnTouchListener() {
//
//
//
////            parkRecycler.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////
////                    Log.d(ParksFragment.class.getName(), "onSwipedPos: " + position);
////
////                    View child = parkRecycler.getChildAt(position);
////
////
////                    ParkAdapter.ViewHolder viewholder  = (ParkAdapter.ViewHolder) parkRecycler.getChildViewHolder(child);
////
////
////
////                    Rect r = new Rect(viewholder.myUndoView.getLeft()
////                            , viewholder.myUndoView.getTop()
////                            , 300 , 300);
////
////                    Rect r1 = new Rect(viewholder.myUndoView.getLeft() , viewholder.myUndoView.getTop() , 300 , 300);
////
////                    if (r.contains(r1)) {
////
////                        Log.d(ParksFragment.class.getName(), "onTouch: " + "true");
////                        viewholder.myUndoView.setVisibility(View.GONE);
////                        viewholder.linearroot.setTranslationX(0);
////                    }
////
////                }
////            });
//
//
////            parkRecycler.setOnTouchListener(new View.OnTouchListener() {
////                @Override
////                public boolean onTouch(View v, MotionEvent event) {
////
////                    switch (event.getAction()) {
////
////                        case MotionEvent.ACTION_DOWN:
////                            return false;
////
////                        case MotionEvent.ACTION_UP:
////
////                            Log.d(ParksFragment.class.getName(), "onSwipedPos: " + position);
////
////                            View child = parkRecycler.getChildAt(position);
////
////
////                                ParkAdapter.ViewHolder viewholder  = (ParkAdapter.ViewHolder) parkRecycler.getChildViewHolder(child);
////
////
////
////                                    Rect r = new Rect(viewholder.myUndoView.getLeft()
////                                            , viewholder.myUndoView.getTop()
////                                    , 300 , 300);
////
////                                    Rect r1 = new Rect(viewholder.myUndoView.getLeft() , viewholder.myUndoView.getTop() , 300 , 300);
////
////                                    if (r.contains(r1)) {
////
////                                        Log.d(ParksFragment.class.getName(), "onTouch: " + "true");
////                                        viewholder.myUndoView.setVisibility(View.GONE);
////                                        viewholder.linearroot.setTranslationX(0);
////                                    }
////
////
////
//////                                    if (r.contains((int)event.getX() , (int)event.getY())) {
//////
//////                                        Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
//////                                        //park.setRemove(false);
//////                                        //parkAdapter.notifyDataSetChanged();
//////
//////
//////
//////
//////                                    }
////
////                            return false;
////                    }
////
////
////
////
////
//////                    Rect r = new Rect(0 , 0 , bx , by);
//////                    int actionX = (int) event.getX();
//////                    int actionY = (int) event.getY();
////
////                     return false;
////
////                }
////            });
//        }
//
//        public void setOnScrollListener(Canvas c) {
//
////            parkRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
////                @Override
////                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
////                    super.onScrollStateChanged(recyclerView, newState);
////                }
////
////                @Override
////                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
////                    super.onScrolled(recyclerView, dx, dy);
////
////                    if (c == null) {
////
////                        Log.d(ParksFragment.class.getName(), "onScrolled: " + "Null");
////                    }
////
////                    int child = recyclerView.getLayoutManager().getItemCount();
////
////                    for (int i = 1 ; i<=child ; i++) {
////
////                        if (((Park) parksArrayList.get(i-1)).isRemove()) {
////
////                            View v = recyclerView.getLayoutManager().getChildAt(i-1);
////                            RelativeLayout rl = (RelativeLayout) v;
////                            rl.getChildAt(0).setVisibility(View.VISIBLE);
////                            rl.getChildAt(1).setTranslationX(300);
////
////
////
////                            //viewHolder.itemView.setTranslationX(300);
////                        }
////                    }
////                }
////            });
//
//
//        }
//
//
//        private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
//
//
//
////            ((ParkAdapter.ViewHolder)viewHolder).linearroot.setTranslationX(dX);
////            ((ParkAdapter.ViewHolder)viewHolder).myUndoView.setTranslationX(0);
//
//
////            Bitmap bmp = BitmapFactory.decodeResource(getResources() , R.drawable.userpic);
////            Bitmap fBmp = Bitmap.createScaledBitmap(bmp , 200 , 200 , false);
////            c.drawBitmap(fBmp , viewHolder.itemView.getLeft() , viewHolder.itemView.getTop() , null);
//
//
//            //Bitmap bitmap = Bitmap.createBitmap(300 , 200 , Bitmap.Config.ARGB_8888);
//
////            v.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////
////                    Log.d(ParksFragment.class.getName(), "onClick: " + "Touched");
////                }
////            });
//
//            MyUndoView v = new MyUndoView(getActivity());
//            v.createRect(0 , 0 ,  300 , 300);
//            v.draw(c);
//
//
//
//            //ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(viewHolder.itemView.getWidth() , 300);
//            //v.setLayoutParams(lp);
//////            myUndoView.setDrawingCacheEnabled(true);
////
////            v.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);
//            //Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//            //c = new Canvas(b);
//            //v.setY(viewHolder.itemView.getTop());
//
////
////            Paint p = new Paint();
////            p.setTextSize(30f);
////            p.setColor(Color.BLACK);
////            p.setAntiAlias(true);
////
////           // Bitmap d = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
////
//////            v.layout(viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(), 300, viewHolder.itemView.getBottom());
//////            c.drawBitmap(d , 0 , 0 , p);
////            c.drawText("Undo" , 0 , 0 , p);
//
////            Bitmap d = myUndoView.getDrawingCache();
//
////            if (d == null) {
////
////                Toast.makeText(getActivity(), "Null", Toast.LENGTH_SHORT).show();
////            }
//
//            //myUndoView.setDrawingCacheEnabled(false);
//
//            //c.drawBitmap(d , 0 , 0 , null);
//
//            //v.setOnClickedListener();
//
//          //
//
//
//
//
//
////            float buttonWidthWithoutPadding = buttonWidth - 20;
////            float corners = 16;
////
////
////            View itemView = viewHolder.itemView;
////            Paint p = new Paint();
////
////            RectF leftButton = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom());
////            p.setColor(Color.RED);
////            c.drawRoundRect(leftButton, corners, corners, p);
////            drawText("UNDO", c, leftButton, p);
//
//
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
//        }
//
//
//
//
//        public void draw(Canvas c) {
//
//            if (viewHolder != null) {
//
//                drawButtons(c , viewHolder);
//            }
//        }
//    }



    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 60;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p);
    }





}

