package jameshassmallarms.com.styleswap.infrastructure;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by gary on 09/11/16.
 */

public abstract class QueryMaster {
    public abstract void run(DataSnapshot s);
}