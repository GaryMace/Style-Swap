package Android.com.styleswap.base;

import android.app.Activity;
import android.content.Intent;
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
import com.google.firebase.database.ValueEventListener;

import Android.com.styleswap.R;
import Android.com.styleswap.infrastructure.DatabaseHandler;
import Android.com.styleswap.infrastructure.FireBaseQueries;
import Android.com.styleswap.infrastructure.QueryMaster;
import Android.com.styleswap.messaging.MyFirebaseInstanceIDService;


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
public class Login extends AppCompatActivity {
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
        Log.d(TAG, "Table exists = "+localDb.isTableExists("userRemember", true));
        
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
        AppStartupActivtiy.isNetworkAvailable(h, AppStartupActivtiy.TIME_OUT_PERIOD);

        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppStartupActivtiy.isNetworkAvailable(h, AppStartupActivtiy.TIME_OUT_PERIOD);

                if (mInternetConnected) {
                    if (submissionFilled()) {
                        final String userName = mUserName.getText().toString();
                        final String password = mUserPassword.getText().toString();

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

        mLaunchRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), Register.class);
                i.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(i);
            }
        });

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
                } else {    //user name doesn't exist
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
}