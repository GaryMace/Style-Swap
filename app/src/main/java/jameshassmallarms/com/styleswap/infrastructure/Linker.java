package jameshassmallarms.com.styleswap.infrastructure;

/**
 * Created by gary on 21/10/16.
 */

public interface Linker {

    boolean isUserLoggedIn();

    void toggleUserLoggedIn();

    String getLoggedInUser();

    void setLoggedInUser(String user);
}
