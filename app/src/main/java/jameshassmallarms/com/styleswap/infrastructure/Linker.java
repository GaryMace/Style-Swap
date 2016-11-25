package jameshassmallarms.com.styleswap.infrastructure;

import android.graphics.Bitmap;

import java.util.List;
import java.util.Queue;

import jameshassmallarms.com.styleswap.impl.Match;
import jameshassmallarms.com.styleswap.impl.User;

/**
 * Created by gary on 21/10/16.
 */

/**
 * Linker:
 *
 *              Linker is the interface that is implemented by MainActivity. It allows the Fragments
 *              in the BarFragment to access or change cached data in MainActivity. Note only the three
 *              fragments in the BarFragment can use it since only their Activity implements it.
 *
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

    List<Match> getCachedMatches();

    void setCachedMatches(List<Match> cachedMatches);

    void addCachedMatch(Match m);

    void removeCachedMatch(Match m);

    double getDeviceLat();

    double getDeviceLon();

    Queue<User> getCachedUsers();

    void setCachedUsers(Queue<User> users);
}
