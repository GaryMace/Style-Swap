package finalProject.com.styleswap.infrastructure;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.List;
import java.util.Queue;

import finalProject.com.styleswap.impl.Match;
import finalProject.com.styleswap.impl.User;

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

    String getAge();

    boolean isUserLoggedIn();

    void toggleUserLoggedIn();

    String getLoggedInUser();

    ImageView getUserProfileImage();

    Bitmap getUserProfilePic();

    List<Match> getCachedMatches();

    void setCachedMatches(List<Match> cachedMatches);

    double getDeviceLat();

    double getDeviceLon();

    String  getItemDescription();

    String  getUserName();

    String  getPhoneNumber();

    void  setItemDescription(String description);

    void  setUserName(String name);

    void  setPhoneNumber(String number);

    void setDressSize(int newDressSize);

    int getDressSize();
}
