package jameshassmallarms.com.styleswap.base;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
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
import android.widget.TextView;

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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.gui.BarFragment;
import jameshassmallarms.com.styleswap.impl.Match;
import jameshassmallarms.com.styleswap.infrastructure.Linker;

public class MainActivity extends AppCompatActivity
    implements Linker, GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    private static final String KEY_IS_LOGGED_IN = "logged_in";
    private static final String KEY_USER_LOGIN = "user_login";
    private static final int REQUEST_CHECK_SETTINGS = 1;
    private static final int REQUEST_CHECK_LOCATION_PREFERENCES = 1;

    protected static final String TAG = "location-updates-sample";
    //The desired interval for location updates. Inexact. Updates may be more or less frequent.
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 120000;
    //The fastest rate for active location updates. Exact. Updates will never be more frequent
    //than this value.
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
        UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    private BarFragment bottomBar;  //Navigation bar at bottom of screen

    //GPS API things
    private GoogleApiClient mGoogleAPIClient;
    protected LocationRequest mLocationRequest;
    protected Location mLastLocation;

    //Tracks the status of the location updates request. Value changes when the user presses the
    // Start Updates and Stop Updates buttons.
    protected Boolean mRequestingLocationUpdates;
    //Time when the location was updated represented as a String.
    protected String mLastUpdateTime;

    //User items
    private int searchRange;

    //Linker Interface items
    private boolean isUserLoggedIn;
    private String userLogin;
    private Bitmap userProfileImg;
    private boolean userChangedImg;
    private List<Match> cachedMatches;

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
            userProfileImg = null;  //this may need to be a database query?
            userChangedImg = false;
            cachedMatches = new ArrayList<>();
        }
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleAPIClient();

        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.activity_main, bottomBar, getString(R.string.fragment_bottom_bar_id)).commit(); //Swap layout for the bottombar layout resource file
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
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
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
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

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
                showExplanation("Permission Needed",
                    "We need to access your GPS to use for matching",
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    REQUEST_CHECK_LOCATION_PREFERENCES
                );
            } else {

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
     *
     * Gary:: Ignore these errors, they're fine
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleAPIClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleAPIClient, this);
    }

    @Override
    public boolean userLoggedIn() {
        return isUserLoggedIn;
    }

    @Override
    public void toggleUserLoggedIn() {
        if (userLoggedIn()) {
            isUserLoggedIn = false;
        } else {
            isUserLoggedIn = true;
        }
    }

    @Override
    public String getLoggedInUser() {
        if (userLoggedIn())
            return userLogin;
        else
            return null;
    }

    @Override
    public void setLoggedInUser(String user) {
        userLogin = user;
        if (!userLoggedIn())
            toggleUserLoggedIn();
    }

    @Override
    public Bitmap getUserProfilePic() {
        return userProfileImg;
    }

    @Override
    public void setUserProfilePic(Bitmap img) {
        this.userProfileImg = img;
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
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (mLastLocation == null) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleAPIClient);    //Gary:: Ignore thise error, it's fine
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            /**
             * Update the lat and lon here
             */
        }
        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void onStart() {
        super.onStart();
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
        stopLocationUpdates();
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        /**
         * Update the lat and lon here
         */
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
        savedInstanceState.putString(KEY_USER_LOGIN, userLogin);

        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mLastLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
    }
}
