package Android.com.styleswap.base;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import Android.com.styleswap.R;
import Android.com.styleswap.gui.discover.SwipeButtonsFragment;
import Android.com.styleswap.gui.im.MatchListFragment;
import Android.com.styleswap.gui.profile.EditProfileFragment;
import Android.com.styleswap.gui.profile.ProfileFragment;
import Android.com.styleswap.impl.Match;
import Android.com.styleswap.impl.User;
import Android.com.styleswap.infrastructure.FireBaseQueries;
import Android.com.styleswap.infrastructure.Linker;
import Android.com.styleswap.infrastructure.QueryMaster;

/**
 * Main Activity:
 *
 *              This is where the magic happens guys. Main activity is where all the hardcore backend
 *              craic happens. Main activity handles the GoogleAPI connection for GPS. There's a lot of
 *              code in here that checks the phone owners permissions to allow GPS requests.
 *
 *              The BarFragment is the main navigation tool for the app. The click listener attached
 *              to it swaps out the one currently displayed fragment with the new one depending on
 *              which tab you select.
 *
 *              When the App first launches MainActivity will start the AppStartupActivity which
 *              either will prompt the user to Login or Register. The resulting data from these Activities
 *              it then returned to MainActivity.
 *
 *              You'll notice that MainActivity implements an Interface called Linker. This allows the
 *              Fragments on the bar fragment to pull information from MainActivity from within their
 *              own class source files.
 *
 */
public class MainActivity extends AppCompatActivity
    implements Linker, GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    // Saved instance state flags
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    private static final String KEY_IS_LOGGED_IN = "logged_in";
    private static final String KEY_USER_LOGIN = "user_login";
    private FireBaseQueries fireBaseQueries = new FireBaseQueries();
    private static final int REQUEST_CHECK_SETTINGS = 1;
    private static final int REQUEST_CHECK_LOCATION_PREFERENCES = 1;
    private static final String TAG = "debug_main";

    //  GPS API things  //////////////////////////////////////////////////
    //The desired interval for location updates. Inexact. Updates may be more or less frequent.
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 60000;
    //The fastest rate for active location updates. Exact. Updates will never be more frequent
    //than this value.
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private GoogleApiClient mGoogleAPIClient;
    protected LocationRequest mLocationRequest;
    protected Location mLastLocation;
    //Tracks the status of the location updates request. Value changes when the user presses the
    //Start Updates and Stop Updates buttons.
    protected Boolean mRequestingLocationUpdates;
    //Time when the location was updated represented as a String.
    protected String mLastUpdateTime;
    ///////////////////////////////////////////////////////////////////////

    public static final int GET_USER_INFORMATION = 0;
    public static final String GET_LOGIN_STATE = "login_state";

    //FireBase constants
    public static final String FIREBASE_BOTH_MATCHED = "bothMatched";
    public static final String FIREBASE_MATCHED_ME = "matchedMe";
    public static final String FIREBASE_RECENT_MATCH = "recentlyMatched";


    //Logged in user fields
    private String mUserLogin;
    private int mUserSize;
    private int mUserAge;   //do we care about their age?
    private String mUserName;
    private String mUserNumber;
    private ImageView profileImage;
    private String mItemDescription;

    //Linker Interface appMessages
    private boolean isUserLoggedIn;
    private Bitmap userProfileImg;
    private boolean userChangedImg;
    private List<Match> cachedMatches;

    //Bar fragments
    private SwipeButtonsFragment mSwipeButtons;
    private MatchListFragment mMatchList;
    private ProfileFragment mProfileFragment;

    //Queue of Users for SwipeButton
    private Queue<User> cachedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileImage = new ImageView(getBaseContext());
        setContentView(R.layout.activity_main); //Load the main XML layout file.. empty on purpose
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);
        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleAPIClient();

        if (mProfileFragment == null) {
            mSwipeButtons = new SwipeButtonsFragment();
            mMatchList = new MatchListFragment();
            mProfileFragment = new ProfileFragment();
        }

        BottomBar bottomBar = (BottomBar) findViewById(R.id.activity_main_bottombar);
        bottomBar.setDefaultTabPosition(1);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentManager fragmentManager;
                fragmentManager = getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                if (tabId == R.id.tab_profile) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                    ft.replace(R.id.activity_main_fragment_container, mProfileFragment, getString(R.string.fragment_bottom_bar_id)).commit();
                } else if (tabId == R.id.tab_search) {
                    ft.replace(R.id.activity_main_fragment_container, mSwipeButtons, getString(R.string.fragment_bottom_bar_id)).commit();

                } else if (tabId == R.id.tab_contact) {
                    ft.replace(R.id.activity_main_fragment_container, mMatchList, getString(R.string.fragment_bottom_bar_id)).commit();
                }
            }
        });

    }

    //Starts the first screen you see. MainActivity then gets the Login/Register data later.
    private void startAppStartupActivityForResult() {
        Intent getLoginIntent = new Intent(getBaseContext(), AppStartupActivtiy.class);
        startActivityForResult(getLoginIntent, GET_USER_INFORMATION);
    }

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

        if (requestCode == EditProfileFragment.RESULT_LOAD_IMAGE &&
            data != null) { //For loading an image form the gallery in EditProfileFragment.

            Uri selectedImageUri = data.getData();
            ImageView imageView = new ImageView(getBaseContext());
            imageView.setImageURI(selectedImageUri);

            profileImage = imageView;
            Log.d(TAG, "Image gotten, updating!");
            fireBaseQueries.uploadImageView(imageView, mUserLogin);

        } else {
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Register cancelled");

            } else if (resultCode == Activity.RESULT_OK &&
                data != null) {

                //First get the result code
                String loginState = data.getExtras().getString(MainActivity.GET_LOGIN_STATE);
                if (loginState != null) {
                    if (loginState.equals(Login.LOGIN_EXISTING_USER)) {            //If they logged into an existing account
                        mUserLogin = data.getExtras().getString(Login.LOGIN_USER_EMAIL);
                        toggleUserLoggedIn();

                        //Pull the rest of the user information from firebase after they login
                        DatabaseReference mUserRef = fireBaseQueries.getUserReferenceByEmail(mUserLogin);
                        fireBaseQueries.executeIfExists(mUserRef, new QueryMaster() {
                            @Override
                            public void run(DataSnapshot s) {
                                User loggedInUser = s.getValue(User.class);
                                mUserAge = loggedInUser.getAge();      //Why do we care about an age?
                                mUserName = loggedInUser.getName();
                                mUserNumber = loggedInUser.getPhoneNum();
                                mUserSize = loggedInUser.getDressSize();
                                mItemDescription = loggedInUser.getItemDescription();
                            }


                        });

                        fireBaseQueries.download(profileImage, mUserLogin);
                        createLocationRequest();

                        //All the information we need should have been just entered by the
                    } else if (loginState.equals(Register.REGISTER_NEW_USER)) {      //User created a new account
                        mUserLogin = data.getExtras().getString(Register.REGISTER_EMAIL);
                        mUserAge = data.getExtras().getInt(Register.REGISTER_AGE);      //Why do we care about an age?
                        mUserName = data.getExtras().getString(Register.REGISTER_NAME);
                        mUserNumber = data.getExtras().getString(Register.REGISTER_PHONE);
                        mUserSize = data.getExtras().getInt(Register.REGISTER_SIZE);
                        Log.d(TAG, "User registered to email: " + mUserLogin+", "+mUserName+", "+mUserSize);
                        fireBaseQueries.download(profileImage, mUserLogin);
                        toggleUserLoggedIn();   //FFS of course we forgot to do this.. no wonder it was returning null in MatchListFrags
                    }

                }
            }
        }

    }

    /**
     * Updates fields based on data stored in the bundle. Most of the GPS code in here came
     * straight from Google code labs
     *
     * But.. it works :)
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            //Reload the user-log state if the app closes temporarily.

            isUserLoggedIn = savedInstanceState.getBoolean(KEY_IS_LOGGED_IN);
            mUserLogin = savedInstanceState.getString(KEY_USER_LOGIN);

            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
                //setButtonsEnabledState();
            }

            // Update the value of mLastLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mLastLocation
                // is not null.
                mLastLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
        } else {
            cachedUsers = new PriorityQueue<>();
            isUserLoggedIn = false;
            userProfileImg = BitmapFactory.decodeResource(getResources(), R.drawable.stock);  //this may need to be a database query?
            userChangedImg = false;
            cachedMatches = new ArrayList<>();
            startAppStartupActivityForResult(); //Launch the start-up screen
        }
    }

    protected synchronized void buildGoogleAPIClient() {
        mGoogleAPIClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
        createLocationRequest();
        getUserLocationFromRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void getUserLocationFromRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleAPIClient,
                builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates k = result.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        checkLocationPermissions();
                        Log.d(TAG, "Starting location updates");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            Log.d(TAG, "Resolution required, dont have access to GPS");
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    //Yeah this is my code though.. not googles.
    private void checkLocationPermissions() {

        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) { //Dont care if they have access, ask them anyways

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
                showExplanation("Permission Needed",        //Make pop-up dialog asking for permissions
                    "We need to access your GPS to use for matching",
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    REQUEST_CHECK_LOCATION_PREFERENCES
                );
            } else {
                Log.d(TAG, "Not asking, just getting Loc");
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CHECK_LOCATION_PREFERENCES);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    requestPermission(permission, permissionRequestCode);
                }
            });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
            new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CHECK_LOCATION_PREFERENCES: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Starting GPS Update");
                    startLocationUpdates();
                } else {
                    //Nothing
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location update recieved");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleAPIClient, mLocationRequest, this);

        } else {
            Log.d(TAG, "Location update failed");
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleAPIClient, this);
    }

    @Override
    public String getAge() {
        return Integer.toString(mUserAge);
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
            return mUserLogin;
        else
            return null;
    }

    @Override
    public void setLoggedInUser(String user) {
        mUserLogin = user;
        if (!isUserLoggedIn())
            toggleUserLoggedIn();
    }

    @Override
    public ImageView getUserProfileImage() {
        return profileImage;
    }

    @Override
    public Bitmap getUserProfilePic() {
        return userProfileImg;
    }


    @Override
    public void toggleUserChangedImg() {
        if (userChangedImg) {
            userChangedImg = false;
        } else {
            userChangedImg = true;
        }
    }

    @Override
    public boolean userChangedImg() {
        return userChangedImg;
    }

    @Override
    public List<Match> getCachedMatches() {
        return cachedMatches;
    }

    @Override
    public void setCachedMatches(List<Match> cachedMatches) {
        if (cachedMatches != null)
            this.cachedMatches = cachedMatches;
    }

    @Override
    public void addCachedMatch(Match m) {
        cachedMatches.add(m);
    }

    @Override
    public void removeCachedMatch(Match m) {
        if (cachedMatches.contains(m)) {
            cachedMatches.remove(m);
        }
    }

    @Override
    public double getDeviceLat() {
        if (mLastLocation != null)
            return mLastLocation.getLatitude();
        else
            return 0;
    }

    @Override
    public double getDeviceLon() {
        if (mLastLocation != null)
            return mLastLocation.getLongitude();
        else
            return 0;
    }

    @Override
    public Queue<User> getCachedUsers() {
        if (cachedUsers != null)
            return cachedUsers;
        else
            return null;
    }

    @Override
    public void setCachedUsers(Queue<User> users) {
        cachedUsers = users;
    }

    @Override
    public String getItemDescription() {
        return mItemDescription;
    }

    @Override
    public String getUserName() {
        return mUserName;
    }

    @Override
    public String getPhoneNumber() {
        return mUserNumber;
    }

    @Override
    public void setDressSize(int newDressSize) {
        DatabaseReference db = fireBaseQueries.getUserReferenceByEmail(mUserLogin);
        mUserSize = newDressSize;
        db.child("dressSize").setValue(newDressSize);
    }

    @Override
    public int getDressSize() {
        return mUserSize;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected to GoogleApiClient");
        if (mLastLocation == null &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleAPIClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        }
        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        mGoogleAPIClient.connect();
    }

    protected void onStart() {
        super.onStart();
        mGoogleAPIClient.disconnect();  // #Magic
        mGoogleAPIClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleAPIClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleAPIClient.isConnected())
            mGoogleAPIClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLastLocation != null)
            stopLocationUpdates();

    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.d(TAG, "Lat: " +mLastLocation.getLatitude());
        Log.d(TAG, "Lon: " +mLastLocation.getLongitude());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleAPIClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_IS_LOGGED_IN, isUserLoggedIn);
        savedInstanceState.putString(KEY_USER_LOGIN, mUserLogin);

        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mLastLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
    }
}
