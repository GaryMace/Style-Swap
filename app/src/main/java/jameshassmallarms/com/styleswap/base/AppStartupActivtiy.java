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
import android.util.Log;
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
    private static final int LOGIN_EXISTING_USER = 1;
    private static final int REGISTER_NEW_USER = 2;
    private static final long SWITCH_TO_NEXT_MESSAGE_DELAY = 8000;
    private static final long START_TIMER_DELAY = 0;

    private VideoView mIntroVid;
    private Button mLogin;
    private Button mRegister;

    //Threaded Text display fields
    private Timer mSwipeTimer;
    private int mCurrTextBox = 0;
    private static final int NUM_TEXT_BOXES_TO_DISPLAY = 4;

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
                startActivityForResult(getLoginIntent, LOGIN_EXISTING_USER);
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent getLoginIntent = new Intent(getBaseContext(), Register.class);
                startActivityForResult(getLoginIntent, REGISTER_NEW_USER);
            }
        });

        //Messages shown at bootom of screen
        List<AppStartupMessage> appMessages = new ArrayList<>();

        appMessages.add(new AppStartupMessage("Hello", "Sign up for free to find new people to swap old dresses with."));
        appMessages.add(new AppStartupMessage("Discover", "Find the dress you've always wanted from another person."));
        appMessages.add(new AppStartupMessage("Your Matches", "Browse your matches and find the perfect StyleSwap for you."));
        appMessages.add(new AppStartupMessage("Contact", "Get in touch with your matches to arrange your StyleSwap."));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.activity_app_startup_viewpager);
        AppStartupPagerAdapter appStartupPagerAdapter = new AppStartupPagerAdapter(this, appMessages);
        viewPager.setAdapter(appStartupPagerAdapter);


        //TODO: If a user swipes this overwrites it and puts it 2 or even 3 messages ahead. Swiping should -1 or +1 mCurrTextBox
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
        mSwipeTimer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                handler.post(Update);
            }
            },
            START_TIMER_DELAY,
            SWITCH_TO_NEXT_MESSAGE_DELAY);
    }

    //Loops video and mutes its sound.
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

    //TODO: Needs refactoring, not returning intent to main properly
    /**
     * Deals with the result from the Login. That being the screen where you select a shimmer to connect.
     * It'll link the sensor you select to the body position you select.
     *
     * @param requestCode Random
     * @param resultCode  Either RESULT_CANCELED(when they go back to main screen) or RESULT_OK(when login/register)
     * @param data        Register / Login data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //ACTIVITY.RESULT_OK is -1, ACTIVITY.RESULT_CANCELED = 0
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "Register cancelled");
        } else if (resultCode == Activity.RESULT_OK) {
            //First get the result code
            String loginState = data.getExtras().getString(MainActivity.GET_LOGIN_STATE);
            if (loginState != null) {
                Intent mainActivityRes = new Intent();
                if (loginState.equals(Login.LOGIN_EXISTING_USER)) {            //If they logged into an existing account
                    mainActivityRes.putExtra(Login.LOGIN_USER_EMAIL, data.getExtras().getString(Login.LOGIN_USER_EMAIL));
                    //get the rest of the info from firebase with a query
                    mainActivityRes.putExtra(MainActivity.GET_LOGIN_STATE, LOGIN_EXISTING_USER);
                    setResult(Activity.RESULT_OK, mainActivityRes);
                    finish();
                } else if (loginState.equals(Register.REGISTER_NEW_USER)) {      //User created a new account
                    /*mUserLogin = data.getExtras().getString(Register.REGISTER_EMAIL);
                    mUserAge = data.getExtras().getInt(Register.REGISTER_AGE);      //Why do we care about an age?
                    mUserName = data.getExtras().getString(Register.REGISTER_NAME);
                    mUserNumber = data.getExtras().getString(Register.REGISTER_PHONE);
                    mUserSize = data.getExtras().getInt(Register.REGISTER_SIZE);
                    String password = data.getExtras().getString(Register.REGISTER_PASSWORD);*/   //send this to firebase instantly then remove our reference to it
                }

            }
        }
    }
    //Prevent back presses
    @Override
    public void onBackPressed() {
    }


    /**
     * The following Code relates to the scolling messages seen at the bottom of the AppStartup Login
     * screen.
     *
     * The Adapter is responsible for changing the displayed text when either the timer runs out or
     * the user scrolls through the texts seen.
     */
    public class AppStartupPagerAdapter extends PagerAdapter {
        List<AppStartupMessage> items;
        LayoutInflater inflater;

        public AppStartupPagerAdapter(Context context, List<AppStartupMessage> items) {
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

            AppStartupMessage appStartupMessage = items.get(position);

            topTextItem.setText(appStartupMessage.topMsg);          //Set title of next message
            bottomTextItem.setText(appStartupMessage.bottomMsg);    //Set next message content
            container.addView(itemView);

            return itemView;
        }
    }


    //Message Wrapper
    private class AppStartupMessage {
        String topMsg;
        String bottomMsg;

        public AppStartupMessage(String topMsg, String bottomMsg) {
            this.topMsg = topMsg;
            this.bottomMsg = bottomMsg;
        }
    }
}
