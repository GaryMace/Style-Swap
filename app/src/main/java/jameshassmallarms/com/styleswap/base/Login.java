package jameshassmallarms.com.styleswap.base;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.impl.Match;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;


public class Login extends AppCompatActivity implements View.OnClickListener {
    public static final String LOGIN_USER_EMAIL = "login_email";
    public static final String LOGIN_EXISTING_USER = "login_existing";
    private Button mLoginButton;
    private EditText mUserName, mUserPassword;
    private TextView mLaunchRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserName = (EditText) findViewById(R.id.activity_login_username);
        mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mUserPassword = (EditText) findViewById(R.id.activity_login_password);
        mLoginButton = (Button) findViewById(R.id.activity_login_button);
        mLaunchRegister = (TextView) findViewById(R.id.activity_login_launch_register);
        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*FireBaseQueries fb = new FireBaseQueries();

                Match m = new Match();
                m.setMatchName("Nerthan");
                m.setMatchMail("nerthandrake@gmail.com");
                m.setMatchNumber("083 376 9282");
                fb.addMatch("Garymac@live.ie", MainActivity.FIREBASE_BOTH_MATCHED, m);*/

                Intent res = new Intent();
                res.putExtra(MainActivity.GET_LOGIN_STATE, LOGIN_EXISTING_USER);
                res.putExtra(LOGIN_USER_EMAIL, "Garymac@live.ie");
                setResult(Activity.RESULT_OK, res);
                finish();
            }
        });
        mLaunchRegister.setOnClickListener(this);

        /**
         * If the login button is clicked, check if user exists.
         *
         * If they do "resultIntent.putString(MainActivity.GET_LOGIN_STATE, LOGIN_EXISTING_USER);
         * and pass the email back!
         */

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){ //need a switch statement to see which button was clicked in login
            case R.id.activity_login_button:


                break;

            case R.id.activity_login_launch_register:
                Intent i = new Intent(getBaseContext(), Register.class);
                i.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(i);
                break;
        }

    }

}