package jameshassmallarms.com.styleswap.infrastructure;

import android.util.Xml;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by siavj on 05/11/2016.
 */

public class FireBaseQueries {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    public DatabaseReference getUserReferenceByEmail(String email){
        return mRootRef.child("Users").child(encodeKey(email));
    }


    public void executeIfExsits(DatabaseReference databaseReference, final Runnable runnable){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    runnable.run();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String encodeKey(String key){
        try {
            return  URLEncoder.encode(key, "UTF-8").replace(".", "%2E");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Encoding Error";
    }

    private String decodeKey(String key){
        try {
            return  URLDecoder.decode(key, "UTF-8").replace("2%E", ".");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Encoding Error";
    }

}
