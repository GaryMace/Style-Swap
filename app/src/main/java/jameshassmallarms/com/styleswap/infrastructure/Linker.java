package jameshassmallarms.com.styleswap.infrastructure;

import android.graphics.Bitmap;

/**
 * Created by gary on 21/10/16.
 */

public interface Linker {

    boolean isUserLoggedIn();

    void toggleUserLoggedIn();

    String getLoggedInUser();

    void setLoggedInUser(String user);

    Bitmap getUserProfilePic();

    void setUserProfilePic(Bitmap img);

    void toggleUserChangedImg();

    boolean userChangedImg();
}
