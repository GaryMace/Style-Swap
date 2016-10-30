package jameshassmallarms.com.styleswap.base;

import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.gui.BarFragment;
import jameshassmallarms.com.styleswap.infrastructure.Linker;

public class MainActivity extends AppCompatActivity implements Linker {
    private static final String KEY_IS_LOGGED_IN = "logged_in";
    private static final String KEY_USER_LOGIN = "user_login";
    private BarFragment bottomBar;  //Navigation bar at bottom of screen
    private boolean isUserLoggedIn;
    private String userLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Load the main XML layout file.. empty on purpose
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        bottomBar = new BarFragment();

        //Reload the user-log state if the app closes temporarily.
        if (savedInstanceState != null) {
            isUserLoggedIn = savedInstanceState.getBoolean(KEY_IS_LOGGED_IN);
            userLogin = savedInstanceState.getString(KEY_USER_LOGIN);
        } else {
            isUserLoggedIn = false;
        }
        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.activity_main, bottomBar, getString(R.string.fragment_bottom_bar_id)).commit(); //Swap layout for the bottombar layout resource file

    }

    @Override
    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    @Override
    public void toggleUserLoggedIn() {
        if (isUserLoggedIn()) {
            isUserLoggedIn = false;
        } else {
            isUserLoggedIn = true;
        }
    }

    @Override
    public String getLoggedInUser() {
        if (isUserLoggedIn())
            return userLogin;
        else
            return null;
    }

    @Override
    public void setLoggedInUser(String user) {
        userLogin = user;
        if (!isUserLoggedIn())
            toggleUserLoggedIn();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_IS_LOGGED_IN, isUserLoggedIn);
        savedInstanceState.putString(KEY_USER_LOGIN, userLogin);
    }
}
