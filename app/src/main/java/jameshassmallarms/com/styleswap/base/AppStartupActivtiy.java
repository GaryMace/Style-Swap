package jameshassmallarms.com.styleswap.base;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import jameshassmallarms.com.styleswap.R;

/**
 * Created by gary on 18/11/16.
 */

public class AppStartupActivtiy extends Activity {
    private static final String TAG = "debug_app_startup";
    private static final int GET_USER_LOGIN = 1;
    private static final int GET_NEW_USER = 2;

    private VideoView mIntroVid;
    private Button mLogin;
    private Button mRegister;

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

        mLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent getLoginIntent = new Intent(getBaseContext(), Login.class);
                getLoginIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(getLoginIntent);
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent getLoginIntent = new Intent(getBaseContext(), Register.class);
                getLoginIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(getLoginIntent);
            }
        });
    }

    MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener(){

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
    /**
     * Deals with the result from the Login. That being the screen where you select a shimmer to connect.
     * It'll link the sensor you select to the body position you select.
     * @param requestCode Random
     * @param resultCode Either RESULT_CANCELED(when no sensor selected) or RESULT_OK(when shimmer selected)
     * @param data shimmer bluetooth address
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //ACTIVITY.RESULT_OK is -1, ACTIVITY.RESULT_CANCELED = 0
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "Register cancelled");
            //So we can distinguish between a connection and a tab change onResume();
        } else if (resultCode == Activity.RESULT_OK) {
            //If they logged into an existing account
            if (true) {
                //get the rest of the info from firebase with a query

            } else {

            }
        }
    }

    //Prevent back presses
    @Override
    public void onBackPressed() {
    }
}
