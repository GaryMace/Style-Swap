package jameshassmallarms.com.styleswap.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jameshassmallarms.com.styleswap.R;

/**
 * Created by gary on 18/11/16.
 */

public class AppStartupActivtiy extends Activity {
    private static final String TAG = "debug_app_startup";

    private VideoView mIntroVid;
    private Button mLogin;
    private Button mRegister;

    //Threaded Text display fields
    private Timer mSwipeTimer;
    private int mCurrTextBox = 0;
    private static final int NUM_TEXT_BOXES_TO_DISPLAY = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_startup);

        mIntroVid = (VideoView)
            findViewById(R.id.activity_startup_video);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.views;
        mIntroVid.setVideoURI(Uri.parse(path));

        mIntroVid.setOnPreparedListener(PreparedListener);
        mIntroVid.requestFocus();

        mLogin = (Button) findViewById(R.id.activity_startup_login);
        mRegister = (Button) findViewById(R.id.activity_startup_register);

        mLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent getLoginIntent = new Intent(getBaseContext(), Login.class);
                getLoginIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(getLoginIntent);
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent getLoginIntent = new Intent(getBaseContext(), Register.class);
                getLoginIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(getLoginIntent);
            }
        });

       List<CustomObject> items = new ArrayList<>();
        items.add(new CustomObject("Hello", "Welcome to StyleSwap. Start swapping old dresses now."));
        items.add(new CustomObject("Discover", "Find new people to swap dresses with. Unlimited matching available."));
        items.add(new CustomObject("Chat", "Talk to new people today and start swapping dresses, your options are limitless."));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.activity_app_startup_viewpager);
        CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(this, items);
        viewPager.setAdapter(customPagerAdapter);

       /* //TODO: change this so that it works!!
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (mCurrTextBox == NUM_TEXT_BOXES_TO_DISPLAY) {
                    mCurrTextBox = 0;
                }
                viewPager.setCurrentItem(mCurrTextBox++, true);
            }
        };

        mSwipeTimer = new Timer();
        mSwipeTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 500, 3000);*/
    }

    MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer m) {
            try {
                if (m.isPlaying()) {
                    m.stop();
                    m.release();
                    m = new MediaPlayer();
                }
                m.setVolume(0f, 0f);
                m.setLooping(true);
                m.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //Prevent back presses
    @Override
    public void onBackPressed() {
    }

    public class CustomPagerAdapter extends PagerAdapter {

        List<CustomObject> items;
        LayoutInflater inflater;

        public CustomPagerAdapter(Context context, List<CustomObject> items) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.items = items;
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View itemView;
            itemView = inflater.inflate(R.layout.activity_app_startup_text_slider, container, false);

            TextView topTextItem = (TextView) itemView.findViewById(R.id.activity_app_startup_top_slider);
            TextView bottomTextItem = (TextView) itemView.findViewById(R.id.activity_app_startup_bottom_slider);

            CustomObject customObject = items.get(position);

            topTextItem.setText(customObject.top);
            bottomTextItem.setText(customObject.bottom);

            container.addView(itemView);

            return itemView;
        }
    }

    public class CustomObject {
        String top;
        String bottom;

        public CustomObject(String top, String bottom) {
            this.top = top;
            this.bottom = bottom;
        }
    }
}
