package jameshassmallarms.com.styleswap.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jameshassmallarms.com.styleswap.R;

/**
 *
 * AppStartup Activity:
 *
 *              This activity allows the user to choose whether they want to login or Register straight
 *              off the bat. It launches either a LoginActivity or a RegisterActivity, gets the result
 *              and hands it back to MainActivity.
 *
 *              The auto changing messages at the bottom are handled using a threaded timer that will
 *              change the text displayed from a list every 8 seconds. You can also swipe through
 *              the messages.
 *
 *              DISCLAIMER: Video displayed is not ours, it's a recording of a project runway episode.
 *
 * Created by gary on 18/11/16.
 */

public class AppStartupActivtiy extends Activity {
    public static final int TIME_OUT_PERIOD = 2000;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        mIntroVid = (VideoView)
                findViewById(R.id.activity_startup_video);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.intro; //Inflate the intro video, not ours, got it from project runway...
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

        //Messages shown at bottom of screen
        List<AppStartupMessage> appMessages = new ArrayList<>();

        appMessages.add(new AppStartupMessage("Hello", "Sign up for free to find new people to StyleSwap with."));
        appMessages.add(new AppStartupMessage("Discover", "Find the dress you've always wanted. Sign up now!"));
        appMessages.add(new AppStartupMessage("Your Matches", "Browse your matches and find the perfect StyleSwap for you."));
        appMessages.add(new AppStartupMessage("Contact", "Get in touch with your matches to arrange your StyleSwap."));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.activity_app_startup_viewpager);
        AppStartupPagerAdapter appStartupPagerAdapter = new AppStartupPagerAdapter(this, appMessages);
        viewPager.setAdapter(appStartupPagerAdapter);


        final Handler handler = new Handler();  //Handle the result of the timer runnable with this handler
        final Runnable Update = new Runnable() {
            public void run() {
                if (mCurrTextBox == NUM_TEXT_BOXES_TO_DISPLAY) {    //If at end of list, go back to start
                    mCurrTextBox = 0;
                }
                viewPager.setCurrentItem(mCurrTextBox++, true); //increment Current list item in ViewPager
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
                m.setLooping(true);     //Keep looping video
                m.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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
                    mainActivityRes.putExtra(Login.LOGIN_USER_EMAIL, data.getExtras().getString(Login.LOGIN_USER_EMAIL));   //NB flag
                    mainActivityRes.putExtra(MainActivity.GET_LOGIN_STATE, Login.LOGIN_EXISTING_USER);
                    setResult(Activity.RESULT_OK, mainActivityRes);
                    finish();
                } else if (loginState.equals(Register.REGISTER_NEW_USER)) {      //User created a new account
                    //This is another NB flag, need to tell MainActivity what type of result it's getting, i.e. is it getting vals from Reg or Login?
                    mainActivityRes.putExtra(MainActivity.GET_LOGIN_STATE, Register.REGISTER_NEW_USER);

                    String mUserEmail = data.getExtras().getString(Register.REGISTER_EMAIL);
                    int mUserAge = data.getExtras().getInt(Register.REGISTER_AGE);      //Why do we care about an age?
                    String mUserName = data.getExtras().getString(Register.REGISTER_NAME);
                    int mUserNumber = data.getExtras().getInt(Register.REGISTER_PHONE);
                    int mUserSize = data.getExtras().getInt(Register.REGISTER_SIZE);
                    mainActivityRes.putExtra(Register.REGISTER_EMAIL, mUserEmail);
                    mainActivityRes.putExtra(Register.REGISTER_NAME, mUserName);
                    mainActivityRes.putExtra(Register.REGISTER_AGE, mUserAge);
                    mainActivityRes.putExtra(Register.REGISTER_PHONE, mUserNumber);
                    mainActivityRes.putExtra(Register.REGISTER_SIZE, mUserSize);
                    setResult(Activity.RESULT_OK, mainActivityRes);
                    finish();
                }

            }
        }
    }

    //Prevent back presses, this would cause the app to move to the main app screen, #NotIdeal
    @Override
    public void onBackPressed() {
    }


    /**
     * The following Code relates to the scrolling messages seen at the bottom of the AppStartup Login
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

    /**
     * Tries to connect to google using a thread, if it cannot then the app prevents users from
     * progressing to next steps.
     *
     * The reason we use a thread is because running this on the UI thread will cause a NetworkException
     * to be raised, #NotIdeal.
     *
     * Thanks to StackOverflow for this cheeky fix.
     * http://stackoverflow.com/questions/6493517/detect-if-android-device-has-internet-connection
     * @param handler
     * @param timeout
     */
    public static void isNetworkAvailable(final Handler handler, final int timeout) {
        // ask fo message '0' (not connected) or '1' (connected) on 'handler'
        // the answer must be send before before within the 'timeout' (in milliseconds)

        new Thread() {
            private boolean responded = false;

            @Override
            public void run() {
                // set 'responded' to TRUE if is able to connect with google mobile (responds fast)
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                            urlc.setRequestProperty("User-Agent", "Test");
                            urlc.setRequestProperty("Connection", "close");
                            urlc.setConnectTimeout(timeout);
                            urlc.connect();
                            if (urlc.getResponseCode() == 200)
                                responded = true;
                            else
                                responded = false;
                        } catch (Exception e) {
                        }
                    }
                }.start();

                try {
                    int waited = 0;
                    while (!responded && (waited < timeout)) {  //Keep trying to connect until timeout
                        sleep(100);
                        if (!responded) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                } // do nothing
                finally {
                    if (!responded) {
                        handler.sendEmptyMessage(0);
                    } else {
                        handler.sendEmptyMessage(1);
                    }
                }
            }
        }.start();
    }
}
