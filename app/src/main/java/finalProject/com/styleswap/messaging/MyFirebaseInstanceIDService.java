package finalProject.com.styleswap.messaging;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import finalProject.com.styleswap.infrastructure.FireBaseQueries;

/**
 * Created by siavj on 23/11/2016.
 */


/**
 * MyFirebaseInstanceIDService:
 *
 *                          This was intended to be used for messaging between users however we
 *                          ended up using a different approach. We left this code in as it
 *                          still provides additional functionallity of being able to send notifications
 *                          from our firebase console online to all our users to keep them informed
 *                          and engaged in our app. This class stores a unique token for each user
 *                          in our database so we send them notifications.
 *
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        FireBaseQueries firebase = new FireBaseQueries();
        //firebase.getUserToken().setValue(token);
    }
}
