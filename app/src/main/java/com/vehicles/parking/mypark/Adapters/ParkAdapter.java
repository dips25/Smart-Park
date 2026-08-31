package com.vehicles.parking.mypark.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;



import com.vehicles.parking.mypark.Models.Park;
import com.vehicles.parking.mypark.R;
import com.vehicles.parking.mypark.Utils.MyUndoView;
import com.vehicles.parking.mypark.Utils.Utils;
import com.vehicles.parking.mypark.activities.ParkMapsActivity;

import java.util.ArrayList;
import java.util.LinkedList;

public class ParkAdapter extends RecyclerView.Adapter<ParkAdapter.ViewHolder> {

    private static final String TAG = ParkAdapter.class.getName();

    Context context;
    ArrayList<Park> parkArrayList;

    boolean isRunning;

    int pos;
    public OnItemClickedListener onItemClickedListener;
    GestureDetector gestureDetector;

    ViewHolder viewHolder;
    private LinkedList<Park> deletelinkedList = new LinkedList<>();
    public int SCREEN_WIDTH;

    boolean isUndoVisible = true;

    int eX;


    //MyRecyclerViewListener recyclerViewListener;

    public ParkAdapter(Context context , ArrayList<Park> parkArrayList , OnItemClickedListener onItemClickedListener) {

        this.context = context;
        this.parkArrayList = parkArrayList;
        this.onItemClickedListener = onItemClickedListener;
        SCREEN_WIDTH = Utils.getScreenWidth(this.context);



    }

    public LinkedList<Park> getDeletelinkedList() {
        return deletelinkedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_park , parent , false);
        return new ViewHolder(view);


    }

//    @Override
//    public void onViewRecycled(@NonNull ViewHolder holder) {
//        super.onViewRecycled(holder);
//
//        //holder.setFlag(holder.getFlag());
//    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Park park = parkArrayList.get(position);

        if (park != null) {

//            if (holder.isSwiped()) {
//
//                Log.d(TAG, "onBindViewHolder: " + "Swiped");
//
//                holder.viewHolderRoot.setVisibility(View.VISIBLE);
//                holder.viewHolderRoot.text.setVisibility(View.VISIBLE);
//                holder.linearroot.setVisibility(View.INVISIBLE);
//                deletelinkedList.add(park);
//
//
//            } else {
//
//
//                holder.viewHolderRoot.setVisibility(View.VISIBLE);
//                holder.viewHolderRoot.text.setVisibility(View.INVISIBLE);
//                holder.linearroot.setVisibility(View.VISIBLE);
//                //holder.linearroot.setElevation(20);
//                deletelinkedList.remove(park);
//
//            }

            holder.name.setText(park.getName());
            holder.address.setText(park.getAddress());
            holder.date.setText(park.getDate());
            holder.time.setText(park.getTime());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    if (holder.viewHolderRoot.text.getVisibility() == View.VISIBLE) {
//
//                        holder.linearroot.setVisibility(View.VISIBLE);
//                        holder.viewHolderRoot.text.setVisibility(View.INVISIBLE);
//                        holder.viewHolderRoot.setVisibility(View.VISIBLE);
//                        holder.setSwiped(false);
//                        deletelinkedList.remove(park);
//                        notifyItemChanged(position);
//
//
//                    } else {

                        Intent intent = new Intent(context , ParkMapsActivity.class);
                        intent.putExtra("park" , park);
                        context.startActivity(intent);

                   // }

                }
            });



//            GestureDetectorCompat gestureDetector = new GestureDetectorCompat(context , new MyGestureListener(park , position , holder , holder.itemView));
//
//            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//
//
//
//                    //gestureDetector.onTouchEvent(event);
//
//                    switch(event.getAction()) {
//
//                        case MotionEvent.ACTION_DOWN:
//
//
//                            isRunning = false;
//
//                            return false;
//
//                        case MotionEvent.ACTION_UP:
//
//                            if (parkArrayList.get(holder.getAdapterPosition()).isRemove()) {
//
//                                Rect r = new Rect(((ParkAdapter.ViewHolder)holder).myUndoView.getLeft()
//                                        , ((ParkAdapter.ViewHolder)holder).myUndoView.getTop()
//                                        , 300 , 300);
//
//                                Rect r1 = new Rect(((ParkAdapter.ViewHolder)holder).myUndoView.getLeft()
//                                        , ((ParkAdapter.ViewHolder)holder).myUndoView.getTop()
//                                        , 300 , 300);
//
//                                if (r.contains(r1)) {
//
//                                     isUndoVisible = true;
//                                }
//
//
//                            }
//
//
//                            if (isUndoVisible) {
//
//                                parkArrayList.get(holder.getAdapterPosition()).setRemove(false);
//                                deletelinkedList.remove(park);
//                                moveView(event.getX() , holder , false);
//                                isUndoVisible = false;
//
//                               // Toast.makeText(context, "isUndo", Toast.LENGTH_SHORT).show();
//
//                                return false;
//
//                            } else if (!parkArrayList.get(holder.getAdapterPosition()).isRemove()) {
//
//                                Intent intent = new Intent(context , ParkMapsActivity.class);
//                                intent.putExtra("park" , park);
//                                context.startActivity(intent);
//
//                                //Toast.makeText(context, "isOpen", Toast.LENGTH_SHORT).show();
//
//                                return false;
//
//                            } else if (!parkArrayList.get(holder.getAdapterPosition()).isRemove()
//                            && isRunning) {
//
//                                moveView(event.getX() , holder , false);
//                               // Toast.makeText(context, "isMove", Toast.LENGTH_SHORT).show();
//                                return false;
//
//
//                            }
//
//
//
//
//
//
//                            Log.d(TAG, "onTouch: " + "Up");
//
//
//
//                            return false;
//
//                        case MotionEvent.ACTION_CANCEL:
//
//                            Log.d(TAG, "onTouch: " + "Cancel");
//
//                            moveView(event.getX() , holder , false);
//
//                            return false;
//
//
//                        case MotionEvent.ACTION_MOVE:
//
//                            isRunning = true;
//
//                            holder.linearroot.setTranslationX(50);
//
//                            moveView(event.getX() , holder , true);
//
////                            if (event.getX() > 0) {
////
////                                if (event.getX()==SCREEN_WIDTH) {
////
////                                    holder.linearroot.setTranslationX(SCREEN_WIDTH + 10);
////                                    holder.itemView.setTranslationX(0);
////
////
////                                } else if (event.getX()>SCREEN_WIDTH/2) {
////
////                                    moveView(event.getX() , holder , false);
////
//////                                    holder.linearroot.setTranslationX(event.getX());
//////                                    holder.itemView.setTranslationX(0);
////
////
////                                } else if (event.getX()<SCREEN_WIDTH/2) {
////
////                                    holder.linearroot.setTranslationX(0);
////                                    holder.itemView.setTranslationX(0);
////
////
////                                }
////
////
////
////
////
////
////                            }
//
//                            Log.d(TAG, "onTouch: " + event.getX() + " " + event.getY());
//
//                            return false;
//
//
//                    }
//
//                    return false;
//
//                }
//
//
//            });
//
//
//
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//
//                }
//            });

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    if (parkArrayList.get(position).isRemove()) {
//
//                        parkArrayList.get(position).setRemove(false);
//                        holder.myUndoView.setVisibility(View.GONE);
//                        holder.linearroot.setTranslationX(0);
//                        notifyDataSetChanged();
//
//                        //onItemClickedListener.onItemClick(holder);
//                    }
//
//
//
//
//
//                }
//            });

//            holder.myUndoView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    holder.myUndoView.setVisibility(View.GONE);
//                    holder.linearroot.setTranslationX(0);
//                    holder.linearroot.setElevation(0);
//                    holder.itemView.setTranslationX(0);
//
//                }
//            });




            //holder.flag = false;
//            if (park.isRemove()) {
//
//                holder.myUndoView.setVisibility(View.VISIBLE);
//                holder.linearroot.setTranslationX(300);
//                holder.linearroot.setElevation(20);
//                holder.itemView.setTranslationX(0);
//            } else {
//
//                holder.myUndoView.setVisibility(View.GONE);
//                holder.linearroot.setTranslationX(0);
//                holder.linearroot.setElevation(0);
//                holder.itemView.setTranslationX(0);
//
//
//
//
//            }
            //holder.setIsRecyclable(false);

            if (park.getTimeStamp() != null) {

                getHistory(park.getTimeStamp() , holder);

            }

//            holder.myUndoView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//
//                }
//            });

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                   // Toast.makeText(context, Boolean.toString(holder.getFlag()), Toast.LENGTH_SHORT).show();
//
////                    Intent intent = new Intent(context , ParkMapsActivity.class);
////                    intent.putExtra("park" , park);
////                    context.startActivity(intent);
//
//                }
//            });

//            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                    PopupMenu popupMenu = new PopupMenu(context , holder.historytext);
//                    popupMenu.getMenuInflater().inflate(R.menu.delete_menu , popupMenu.getMenu());
//                    popupMenu.show();
//
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//
//                            if (item.getItemId() == R.id.delete) {
//
//
//                                SqliteDatabase db = new SqliteDatabase(context);
//                                db.deletePark(park.getId());
//                                parkArrayList.remove(park);
//                                notifyDataSetChanged();
//
//                                return true;
//                            }
//
//                            return false;
//
//                        }
//                    });
//                    return true;
//
//
//
//                }
//            });
        }



//        Display display = holder.itemView.getDisplay();
//        Point point = new Point();
//        display.getSize(point);

//        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(1507, View.MeasureSpec.AT_MOST);
//        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//
//        holder.itemView.measure(widthMeasureSpec , heightMeasureSpec);
//
//        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = windowManager.getDefaultDisplay();
//        Point point = new Point();
//        display.getSize(point);

//        Log.d(ParkAdapter.class.getName(), "onBindPhone: " + point.x + " " + point.y);
//
//
//
//        Log.d(ParkAdapter.class.getName(), "onBindViewHolder: " +  holder.itemView.getMeasuredHeight());

    }

    private void moveView(float x, ViewHolder holder , boolean isMove) {

        eX = (int)x;

        if (eX<SCREEN_WIDTH/2) {

            holder.linearroot.setTranslationX(eX);
            holder.itemView.setClickable(false);

            if (!isMove) {

                while (eX>20) {

                    eX = eX-20;

                    if (eX>0) {

                        holder.linearroot.setTranslationX(eX);
                        holder.itemView.setTranslationX(0);

                    } else {

                        eX = 0;
                        holder.linearroot.setTranslationX(eX);
                        holder.itemView.setTranslationX(0);
                    }


                }

                holder.myUndoView.setVisibility(View.GONE);


            }



        } else if (eX > SCREEN_WIDTH/2) {

            holder.linearroot.setTranslationX(eX);

            while (eX>SCREEN_WIDTH/2 && !isMove) {

                eX = eX + 20;

                if (eX>SCREEN_WIDTH) {

                    break;
                }

                holder.linearroot.setTranslationX(eX);
                holder.itemView.setTranslationX(0);

            }

            holder.myUndoView.setVisibility(View.VISIBLE);
            parkArrayList.get(holder.getAdapterPosition()).setRemove(true);
            deletelinkedList.add(parkArrayList.get(holder.getAdapterPosition()));
            isRunning = false;
        }

        eX = 0;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public long getItemId(int position) {
        pos = position;
        return position;
    }

    public long getPosition() {

        return pos;

    }

    private void getHistory(String timeStamp , ViewHolder holder) {

//        CountDownTimer ct = new CountDownTimer(Integer.MAX_VALUE , 5000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        };

        double timeNow = System.currentTimeMillis()/1000;
        double timeServer = Double.parseDouble(timeStamp);

        double diff = timeNow - timeServer;

        if (timeNow == timeServer) {

            holder.historytext.setText("Now");
            return;

        }

        int mins =(int) diff/60;
        int hrs =(int) mins/60;
        int days =(int) hrs/24;
        int months =(int) days/30;
        int yrs =(int) months/12;

        if (yrs>0) {

            holder.historytext.setText(Integer.toString(yrs)+"years ago");

        }else if (months>0){

            holder.historytext.setText(Integer.toString(months)+"months ago");

        } else if (days>0){

            holder.historytext.setText(Integer.toString(days)+"days ago");

        } else if (hrs>0) {

            holder.historytext.setText(Integer.toString(hrs)+"hours ago");

        } else {

            holder.historytext.setText(Integer.toString(mins)+"mins ago");
        }
    }

    @Override
    public int getItemCount() {
        return parkArrayList.size();
    }

    public void setItems(ArrayList<Park> parksArrayList) {

        this.parkArrayList = parksArrayList;
        Log.d(TAG, "setItems: "+parkArrayList.size());
        notifyDataSetChanged();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

       public boolean flag;
       public boolean hasSwiped;

       public RelativeLayout linearroot;
        public LinearLayout undoHolder;



       //public View itemView;

        public TextView name , address , date , time , historytext;

        public MyUndoView myUndoView;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(300 , 300);



        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            name = (TextView) itemView.findViewById(R.id.single_item_name);
            address = (TextView) itemView.findViewById(R.id.single_item_address);
            date = (TextView) itemView.findViewById(R.id.single_item_date);
            time = (TextView) itemView.findViewById(R.id.single_item_time);
            historytext = (TextView) itemView.findViewById(R.id.historytext);

//            viewHolderRoot.addView(myUndoView);


        }

        public boolean getFlag() {

            return flag;
        }

        public void setFlag(boolean flag) {

            this.flag = flag;
        }



        public void setUndoView(int dx) {

            linearroot.setTranslationX(dx);




        }

        public void setUndoView() {

            //linearroot.setTranslationX(300);




        }


    }

//    public class MyRecyclerViewListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
//
//        //GestureDetector gestureDetector = new GestureDetector(this);
//
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            gestureDetector.onTouchEvent(event);
//            return false;
//        }
//
//
//        @Override
//        public boolean onSingleTapUp(@NonNull MotionEvent e) {
//
//            LinearLayout linearLayout = new LinearLayout(context);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT , WRAP_CONTENT);
//            linearLayout.setLayoutParams(lp);
//            lp.gravity = Gravity.CENTER;
//
//            ProgressBar progressBar = new ProgressBar(context);
//            progressBar.setIndeterminate(false);
//
//            linearLayout.addView(progressBar);
//
//            CustomDialog customDialog = new CustomDialog(context);
//            //customDialog.setSize(300 , 300 , 4);
//            customDialog.setCancelable(false);
//            customDialog.setContentView(linearLayout);
//            customDialog.setTitle("Processing...");
//            customDialog.show();
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    Rect r = new Rect(((ParkAdapter.ViewHolder)viewHolder).myUndoView.getLeft()
//                            , ((ParkAdapter.ViewHolder)viewHolder).myUndoView.getTop()
//                            , 300 , 300);
//
//                    Rect r1 = new Rect(((ParkAdapter.ViewHolder)viewHolder).myUndoView.getLeft()
//                            , ((ParkAdapter.ViewHolder)viewHolder).myUndoView.getTop()
//                            , 300 , 300);
//
//                    if (r.contains(r1)) {
//
//                        Log.d(ParksFragment.class.getName(), "onTouch: " + "true");
//                        ((ParkAdapter.ViewHolder)viewHolder).myUndoView.setVisibility(View.GONE);
//                        ((ParkAdapter.ViewHolder)viewHolder).linearroot.setTranslationX(0);
//
//                        parkArrayList.get(viewHolder.getAdapterPosition()).setRemove(false);
//
//                        customDialog.dismiss();
//
//
//
//
//                    }
//
//                }
//            } , 2000);
//
//
//
//
//
//
//
//
//            // Log.d(ParksFragment.class.getName(), "onSingleTapUp: " + "Tap Up " + e.getX() + " " + e.getY() + name);
//            return true;
//        }
//
//        @Override
//        public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
//
//            Log.d(ParksFragment.class.getName(), "onFling: "  + e2.getX() + " " + velocityX);
//
//            if (velocityX > 1000) {
//
//                ((ParkAdapter.ViewHolder)h).linearroot.setTranslationX(300);
//                ((ParkAdapter.ViewHolder)viewHolder).myUndoView.setVisibility(View.VISIBLE);
//
//
//            }
//            return true;
//        }
//
//        @Override
//        public boolean onDown(@NonNull MotionEvent e) {
//            return super.onDown(e);
//        }
//
//        @Override
//        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
//            return super.onSingleTapConfirmed(e);
//        }
//    }



    public interface OnItemClickedListener{

        public void onItemClick(ParkAdapter.ViewHolder viewHolder);

    }


    private class MyGestureListener implements GestureDetector.OnGestureListener{

        View viewHolder;
        ViewHolder holder;
        int position;
        Park park;

        MyGestureListener(Park park , int position , ViewHolder holder , View viewHolder) {

            this.viewHolder = viewHolder;
            this.holder = holder;
            this.position = position;
            this.park = park;

        }


            public boolean onDown(@NonNull MotionEvent e) {
                return false;
            }

        @Override
        public void onShowPress(@NonNull MotionEvent e) {

        }


        @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {

            if (holder.myUndoView.getVisibility() == View.GONE) {

                Intent intent = new Intent(context , ParkMapsActivity.class);
                    intent.putExtra("park" , park);
                    context.startActivity(intent);

                    return false;
            }

                holder.myUndoView.setVisibility(View.GONE);
                holder.linearroot.setTranslationX(0);
                parkArrayList.get(position).setRemove(false);
                deletelinkedList.remove(park);
                return false;
            }

            @Override
            public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {

        }


        @Override
            public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {

                Log.d(TAG, "onFling: " + velocityX);

                if (e2.getX() - e1.getX() > 40 && Math.abs(velocityX) > 50) {

                        parkArrayList.get(position).setRemove(true);
                        deletelinkedList.add(park);

                        holder.myUndoView.setVisibility(View.VISIBLE);
                        holder.linearroot.setTranslationX(300);


//                    LinearLayout ll = (LinearLayout) viewHolder.findViewById(R.id.linearroot);
//
//                    ll.setTranslationX(300);
                        //notifyItemChanged(position);
                        //viewHolder.myUndoView.setVisibility(View.VISIBLE);

                    return false;
                }

                return false;
            }

    }
}
