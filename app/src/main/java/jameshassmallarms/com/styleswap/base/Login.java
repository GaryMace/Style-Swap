package jameshassmallarms.com.styleswap.base;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
import jameshassmallarms.com.styleswap.infrastructure.QueryMaster;


public class Login extends AppCompatActivity implements View.OnClickListener {
    public static final String LOGIN_USER_EMAIL = "login_email";
    public static final String LOGIN_EXISTING_USER = "login_existing";
    Button ButtonLogin;
    EditText etUsername, etPassword;
    TextView LinkWithRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        etPassword = (EditText) findViewById(R.id.etPassword);
        ButtonLogin = (Button) findViewById(R.id.ButtonLogin);
        LinkWithRegister = (TextView) findViewById(R.id.LinkWithRegister);
        ButtonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent res = new Intent();
                res.putExtra(MainActivity.GET_LOGIN_STATE, LOGIN_EXISTING_USER);
                res.putExtra(LOGIN_USER_EMAIL, "Garymac@live.ie");
                setResult(Activity.RESULT_OK, res);
                FireBaseQueries firebase = new FireBaseQueries();
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                
                firebase.getUserToken("Garymac@live.ie").setValue(refreshedToken);

                finish();
            }
        });
        LinkWithRegister.setOnClickListener(this);

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
            case R.id.ButtonLogin:


                break;

            case R.id.LinkWithRegister:
                Intent i = new Intent(getBaseContext(), Register.class);
                i.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(i);
                break;
        }

    }

//    private void onTokenRefresh(DatabaseReference userToken) {
//        FireBaseQueries firebase = new FireBaseQueries();
//        // Get updated InstanceID token.
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        //Log.d(TAG, "Refreshed token: " + refreshedToken);
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
//        firebase.getUserToken(refreshedToken);
//
//    }



}

