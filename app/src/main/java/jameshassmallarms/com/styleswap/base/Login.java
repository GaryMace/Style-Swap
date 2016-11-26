package jameshassmallarms.com.styleswap.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.impl.Match;
import jameshassmallarms.com.styleswap.impl.User;
import jameshassmallarms.com.styleswap.infrastructure.DatabaseHandler;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
import jameshassmallarms.com.styleswap.infrastructure.QueryMaster;
import jameshassmallarms.com.styleswap.messaging.MyFirebaseInstanceIDService;


/**
 * Login:
 *
 *          The Login activity is started by the AppStartupActivity. AppStartup expects to get a
 *          result from this activity. There are two kinds of results that are returned.

 *          1) The user logins to an existing account and that users email is sent back to
 *          AppStartup. From there it is sent back to MainActivity
 *
 *          2) The user chooses the "Sign up" button and Login Activity launches Register Activity.
 *          There is logic in place that tells AppStartup that it now expects the result directly
 *          from Register and NOT LOGIN.
 *
 *          LOCAL DATABASE:
 *          We used our local database to store the users login info if they click the
 *          RememberMe Checkbox. It simply stores the most up-to-date login info and reloads
 *          it from the database when the app relaunches.
 */
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

    private boolean mInternetConnected;

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
        final Handler h = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what != 1) { // code if not connected
                    mInternetConnected = false;

                } else { // code if connected
                    mInternetConnected = true;
                }
            }
        };
        isNetworkAvailable(h,2000);

        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isNetworkAvailable(h,2000);
                /*FireBaseQueries fb = new FireBaseQueries();
                Match m = new Match();*/
                /*User usr = new User("Alan", 21, "haymakerStirrat@gmail.com", "1234", 10);
                fb.pushNewUserDetails(usr);*/

                /*m.setMatchName("Gary");
                m.setMatchMail("Garymac@live.ie");
                m.setMatchNumber("083 376 9282");
                fb.addMatch("haymakerStirrat@gmail.com", MainActivity.FIREBASE_MATCHED_ME, m);*/

                /*m.setMatchName("Gary");
                m.setMatchMail("Garymac@live.ie");
                m.setMatchNumber("083 376 9282");
                m.setMatchChatToken("Garymac@live.iehaymakerStirrat@gmail.com");
                fb.addMatch("haymakerStirrat@gmail.com", MainActivity.FIREBASE_BOTH_MATCHED, m);*/


                //TODO: breaks on first install, fix
                if (mInternetConnected) {
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
                                        } else if (storedEmail == null) {
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
                                    MyFirebaseInstanceIDService messagingToken = new MyFirebaseInstanceIDService();
                                    messagingToken.onTokenRefresh();

                                    finish();
                                } else {
                                    Toast.makeText(getBaseContext(), "Whoops, password was wrong!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getBaseContext(), "Email or password was left empty!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "You need an internet connection to Login!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mLaunchRegister.setOnClickListener(this);

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
                            urlc.setConnectTimeout(1500);
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
                    while (!responded && (waited < timeout)) {
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

