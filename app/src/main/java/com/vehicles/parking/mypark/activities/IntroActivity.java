package com.vehicles.parking.mypark.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.vehicles.parking.mypark.R;

public class IntroActivity extends AppCompatActivity {

    ViewPager viewPager;
    IntroPagerAdapter pagerAdapter;
    int[] resIds = {R.layout.page1 , R.layout.page2};

    ImageView[] dots = new ImageView[2];

    LinearLayout dotsHolder;
    TextView indicText , indiPrevText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);

        dotsHolder = (LinearLayout) findViewById(R.id.dots_holder);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5 ,5 ,5 ,5);

        for (int i = 0 ; i<2 ; i++) {

            dots[i] = new ImageView(this);
            dots[i].setLayoutParams(lp);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.dots_unselected));
            dotsHolder.addView(dots[i]);

        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.dot_selected));

        indicText = (TextView) findViewById(R.id.indi_text);
        indiPrevText = (TextView) findViewById(R.id.indiprev_text);
        indiPrevText.setVisibility(View.GONE);

        indiPrevText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

//                if (viewPager.getCurrentItem() == 0) {
//
//                    indiPrevText.setVisibility(View.GONE);
//                }
            }
        });

        indicText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (indicText.getText().toString().equalsIgnoreCase("Next")) {

//                    indiPrevText.setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);

                }

                 else if (indicText.getText().toString().equalsIgnoreCase("Start")) {

                    Intent intent = new Intent(IntroActivity.this , LoginActivity.class);
                    startActivity(intent);




                }

            }
        });


        viewPager = (ViewPager) findViewById(R.id.intropageadapter);
        pagerAdapter = new IntroPagerAdapter(resIds);
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {

                    indiPrevText.setVisibility(View.GONE);
                    indicText.setVisibility(View.VISIBLE);
                    indicText.setText("Next");


                } else if (position+1 == viewPager.getChildCount()) {

                    indiPrevText.setVisibility(View.VISIBLE);
                    indicText.setVisibility(View.VISIBLE);
                    indicText.setText("Start");


                }

                for (int i = 0 ; i<2 ; i++) {

                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.dots_unselected));

                }

                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.dot_selected));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public class IntroPagerAdapter extends PagerAdapter {

        IntroPagerAdapter pagerAdapter;
        int[] resIds;

        public IntroPagerAdapter(int [] resIds) {

            this.resIds = resIds;

        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            int id = resIds[position];

            ViewGroup view = (ViewGroup) LayoutInflater.from(IntroActivity.this).inflate(id , container , false);
            container.addView(view);

            return view;





        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return resIds.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
