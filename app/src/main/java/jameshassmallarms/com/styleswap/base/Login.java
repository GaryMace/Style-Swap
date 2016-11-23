package jameshassmallarms.com.styleswap.base;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.infrastructure.DatabaseHandler;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
import jameshassmallarms.com.styleswap.messaging.MyFirebaseInstanceIDService;


public class Login extends AppCompatActivity implements View.OnClickListener {
    public static final String LOGIN_USER_EMAIL = "login_email";
    public static final String LOGIN_EXISTING_USER = "login_existing";
    private static final String TAG = "debug_login";
    private static final int REMEMBER_ME = 1;
    private DatabaseHandler localDb;
    private FireBaseQueries firebaseDb;
    private Button mLoginButton;
    private EditText mUserName, mUserPassword;
    private TextView mLaunchRegister;
    private CheckBox mRememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseDb = new FireBaseQueries();
        localDb = new DatabaseHandler(getBaseContext());

        mUserName = (EditText) findViewById(R.id.activity_login_username);
        mUserPassword = (EditText) findViewById(R.id.activity_login_password);
        mLoginButton = (Button) findViewById(R.id.activity_login_button);
        mLaunchRegister = (TextView) findViewById(R.id.activity_login_launch_register);
        mRememberMe = (CheckBox) findViewById(R.id.activity_login_remember_me);

        loadRememberDataIfExists();

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
                MyFirebaseInstanceIDService test = new MyFirebaseInstanceIDService();
                test.onTokenRefresh();

                finish();/*
=======
                if (submissionFilled()) {
                    final String userName = mUserName.getText().toString();
                    final String password = mUserPassword.getText().toString();
                    //if ()
                    executeIfExists(firebaseDb.getPassword(userName), new QueryMaster() {
                        @Override
                        public void run(DataSnapshot s) {
                            String fbPassword = s.getValue(String.class);
                            if (password.equals(fbPassword)) {      //Login successfully
                                if (mRememberMe.isChecked()) {      //Do they want us to remember their login details
                                    String storedEmail = localDb.readEmail();
                                    if (storedEmail != null && !storedEmail.equals(userName)) { //If local details are out-of-date, update them.

                                        localDb.updateEmail(userName);
                                        localDb.updatePassword(fbPassword);
                                        localDb.updateRememberMe(REMEMBER_ME);
                                    } else if (storedEmail == null){
                                        localDb.addDetails(userName, password, REMEMBER_ME);
                                    }
                                } else {
                                    localDb.deleteEntry();
                                }
                                Log.d(TAG, "Login Successful: " + userName);
                                Intent res = new Intent();
                                res.putExtra(MainActivity.GET_LOGIN_STATE, LOGIN_EXISTING_USER);
                                res.putExtra(LOGIN_USER_EMAIL, userName);
                                setResult(Activity.RESULT_OK, res);
                                finish();
                            } else {
                                Toast.makeText(getBaseContext(), "Whoops, password was wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getBaseContext(), "Email or password was left empty!", Toast.LENGTH_SHORT).show();
                }
>>>>>>> Updated Login*/
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

    private void loadRememberDataIfExists() {
        String savedEmail = localDb.readEmail();
        if (savedEmail != null) {
            mUserName.setText(savedEmail);
            mUserPassword.setText(localDb.readPassword());
            mRememberMe.setChecked(localDb.readRememberMe());
        } else {
            Log.d(TAG, "User didn't want to be remembered");
        }
    }

    public void executeIfExists(DatabaseReference databaseReference, final QueryMaster q) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    q.run(dataSnapshot);
                } else {    //user name doesn;t exist
                    Toast.makeText(getBaseContext(), "That UserName doesn't exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean submissionFilled() {
        return mUserName.getText() != null && mUserPassword.getText() != null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) { //need a switch statement to see which button was clicked in login
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

