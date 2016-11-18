package jameshassmallarms.com.styleswap.base;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import jameshassmallarms.com.styleswap.R;

/**
 * Created by gary on 18/11/16.
 */

public class AppStartupActivtiy extends Activity {
    private static final int GET_USER_LOGIN = 0;
    private static final int GET_NEW_USER = 1;

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

        MediaController mc = new MediaController(this);
        mc.setAnchorView(mIntroVid);
        mc.setMediaPlayer(mIntroVid);
        mIntroVid.setOnPreparedListener(PreparedListener);

        mLogin = (Button) findViewById(R.id.activity_startup_login);
        mRegister = (Button) findViewById(R.id.activity_startup_register);

        mLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent getLoginIntent = new Intent(getBaseContext(), Login.class);
                startActivityForResult(getLoginIntent, GET_USER_LOGIN);
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent getLoginIntent = new Intent(getBaseContext(), Register.class);
                startActivityForResult(getLoginIntent, GET_NEW_USER);
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
}
