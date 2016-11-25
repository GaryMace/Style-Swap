package jameshassmallarms.com.styleswap.infrastructure;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import jameshassmallarms.com.styleswap.impl.Match;
import jameshassmallarms.com.styleswap.impl.User;

/**
 * Created by siavj on 05/11/2016.
 */

public class FireBaseQueries {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    public DatabaseReference getUserReferenceByEmail(String email) {
        return mRootRef.child("Users").child(encodeKey(email));
    }
    public DatabaseReference getChatRoot() {
        return mRootRef.child("Chats");
    }

    public DatabaseReference getUserLocationReferenceByEmail(String email) {
        return mRootRef.child("UserLocation").child(encodeKey(email));
    }

    public void uploadImageView(ImageView image, String userID) {

        StorageReference picRef = storage.getReferenceFromUrl("gs://styleswap-f3aa9.appspot.com").child(userID + "/" + "Dress");

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

    public void download(final ImageView imageView, String username) {

        StorageReference picRef = storage.getReferenceFromUrl("gs://styleswap-f3aa9.appspot.com").child(username + "/" + "Dress");

        final long ONE_MEGABYTE = 1024 * 1024;
        picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
                System.out.println("downloaded");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }


    public void executeIfExists(DatabaseReference databaseReference, final QueryMaster q) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    q.run(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static String encodeKey(String key) {
        try {
            return URLEncoder.encode(key, "UTF-8").replace(".", "%2E");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Encoding Error";
    }

    private String decodeKey(String key) {
        try {
            return URLDecoder.decode(key, "UTF-8").replace("2%E", ".");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Encoding Error";
    }

    public DatabaseReference getUserNumber(String email) {
        return getUserReferenceByEmail(email).child("phoneNumber");
    }

    public DatabaseReference getThing(String email) {
        return getUserReferenceByEmail(email).child("-chats").child("haymakerstirrat%40gmail%2Ecom").child("messages");
    }

    public DatabaseReference getPassword(String email) {
        return getUserReferenceByEmail(email).child("password");
    }

    public DatabaseReference getUserItemDescription(String email) {
        return getUserReferenceByEmail(email).child("itemDescription");
    }

    public DatabaseReference getBothMatched(String email) {
        return getUserReferenceByEmail(email).child("bothMatched");
    }

    public DatabaseReference getMatchedme(String email) {
        return getUserReferenceByEmail(email).child("matchedMe");
    }
    public DatabaseReference createChatRoom(String chatRoomKey) {
        return getChatRoot().child(chatRoomKey).child("messages");
    }

    public DatabaseReference getUserName(String email) {
        return getUserReferenceByEmail(email).child("name");
    }

    public void pushNewUserDetails(User newUser) {
        DatabaseReference mUserRef = getUserReferenceByEmail(newUser.getEmail());
        mUserRef.setValue(newUser);
    }

    public DatabaseReference getUserToken(String email) {
        return getUserReferenceByEmail(email).child("messageToken");
    }

    public void addMatch(String email, String matchType, final Match newMatch) {
        final DatabaseReference userRef;

        if (matchType.equals("bothMatched"))
            userRef = getBothMatched(email);
        else if (matchType.equals("matchedMe"))
            userRef = getMatchedme(email);
        else
            return;

        executeIfExists(userRef, new QueryMaster() {
            @Override
            public void run(DataSnapshot s) {
                GenericTypeIndicator<ArrayList<Match>> t = new GenericTypeIndicator<ArrayList<Match>>() {
                };
                ArrayList<Match> update = s.getValue(t);
                update.add(newMatch);
                userRef.setValue(update);

            }
        });
    }

    public void removeMatch(String email, String matchType, final int position) {
        final DatabaseReference userRef;

        userRef = getMatchedme(email);

        executeIfExists(userRef, new QueryMaster() {
            @Override
            public void run(DataSnapshot s) {
                GenericTypeIndicator<ArrayList<Match>> t = new GenericTypeIndicator<ArrayList<Match>>() {
                };
                ArrayList<Match> update = s.getValue(t);
                update.remove(position);
                userRef.setValue(update);

            }
        });
    }
}
