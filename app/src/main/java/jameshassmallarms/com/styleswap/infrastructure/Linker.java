package jameshassmallarms.com.styleswap.infrastructure;

import android.graphics.Bitmap;

import java.util.List;
import java.util.Queue;

import jameshassmallarms.com.styleswap.impl.Match;
import jameshassmallarms.com.styleswap.impl.User;

/**
 * Created by gary on 21/10/16.
 */

public interface Linker {

    boolean userLoggedIn();

    void toggleUserLoggedIn();

    String getLoggedInUser();

    void setLoggedInUser(String user);

    Bitmap getUserProfilePic();

    void setUserProfilePic(Bitmap img);

    void toggleUserChangedImg();

    boolean userChangedImg();

    List<Match> getCachedMatches();

    void setCachedMatches(List<Match> cachedMatches);

    void addCachedMatch(Match m);

    void removeCachedMatch(Match m);

    double getDeviceLat();

    double getDeviceLon();

    Queue<User> getCachedUsers();

    void setCachedUsers(Queue<User> users);
}
