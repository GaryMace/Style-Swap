package jameshassmallarms.com.styleswap.infrastructure;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    public void download(final ImageView imageView, String username, String imagename ) {

        StorageReference picRef = storage.getReferenceFromUrl("gs://styleswap-4075c.appspot.com").child(username+"/"+imagename);

        final long ONE_MEGABYTE = 1024 * 1024;
        picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bmp);

                }
        }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                }
        });
    }


    public void executeIfExists(DatabaseReference databaseReference, final QueryMaster q){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    q.run(dataSnapshot);
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

    public ImageView getUserImage(String email, String imageName){
        ImageView imageView = null;
        download(imageView, email, imageName);
        return imageView;
    }

    public DatabaseReference getUserNumber(String email){
        return getUserReferenceByEmail(email).child("phoneNumber");
    }

    public DatabaseReference getUserItemDescription(String email){
        return getUserReferenceByEmail(email).child("itemDescription");
    }

    public DatabaseReference getMatches(String email){
        return getUserReferenceByEmail(email).child("matches");
    }

    public DatabaseReference getUserName(String email){
        return getUserReferenceByEmail(email).child("name");
    }
}
