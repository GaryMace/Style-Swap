package Android.com.styleswap.infrastructure;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by gary on 09/11/16.
 *
 * This class is used to pass a block of executable code to the firebase result handler. We could've
 * just used a Runnable but this is a lot more explicit and novel. 
 */
public abstract class QueryMaster {
    public abstract void run(DataSnapshot s);
}