package jameshassmallarms.com.styleswap.infrastructure;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Xml;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by siavj on 05/11/2016.
 */

public class FireBaseQueries {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    public DatabaseReference getUserReferenceByEmail(String email){
        return mRootRef.child("Users").child(encodeKey(email));
    }

    public void uploadImageView(ImageView image, String userID, String imageName){

        StorageReference picRef = storage.getReferenceFromUrl("gs://styleswap-4075c.appspot.com").child(userID+"/"+imageName);

        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        Bitmap bitmap = image.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = picRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
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
