package finalProject.com.styleswap.infrastructure;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

import finalProject.com.styleswap.base.MainActivity;
import finalProject.com.styleswap.impl.Match;
import finalProject.com.styleswap.impl.User;

/**
 * FireBaseQueries:
 *
 *
 *                  This Class contains methods to execute commonly occurring interactions with our
 *                  Firebase real time database and storage. Occasionally we need to interact in a
 *                  custom way with Firebase but in most instances the methods in this class are all
 *                  that is required.
 *
 * Created by James on 24/11/16.
 */

public class FireBaseQueries {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    /**
     * getUserReferenceByEmail:
     *
     * encoded emails are used as the key in our database since firebase keys cannot contain certain
     * characters. This method returns a reference to Root->Users->Encoded Email
     *
     * @param email     users email who you want a reference to.
     * @return  DatabaseReference
     */
    public DatabaseReference getUserReferenceByEmail(String email) {
        return mRootRef.child("Users").child(encodeKey(email));
    }

    /**
     * getChatRoot
     *
     * @return  DatabaseReference       reference to all the chat rooms Root->Chats
     */
    public DatabaseReference getChatRoot() {
        return mRootRef.child("Chats");
    }


    /**
     * getUserLocationReferenceByEmail:
     *
     * @param email     users email who you want a reference to.
     * @return  DatabaseReference   reference to passed users location data
     */
    public DatabaseReference getUserLocationReferenceByEmail(String email) {
        return mRootRef.child("UserLocation").child(encodeKey(email));
    }

    /**
     * uploadImageView:
     *
     * @param image     the imageView you want upload to our online storage
     * @param userID     the email of the user who want to store the image
     */
    public void uploadImageView(ImageView image, String userID) {

        StorageReference picRef = storage.getReferenceFromUrl("gs://styleswap-70481.appspot.com").child(userID + "/" + "Dress");

        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
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
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    /**
     * uploadImageView:
     *
     * @param imageView     the imageView you want to set to download
     * @param username      the email of the user who whose photo you want to download
     */
    public void download(final ImageView imageView, String username) {

        StorageReference picRef = storage.getReferenceFromUrl("gs://styleswap-70481.appspot.com").child(username + "/" + "Dress");

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


    /**
     * executeIfExists:
     *
     *                 Checks if there is any value stored at the given database reference.
     *                 If there is a value the queryMaster is executed.
     *                 Without calling this is you try write to a reference which does not exsist in
     *                 the database it will create it which will cause problems.
     *
     *                 Can also be used when you need to get and use the value stored at the reference.
     *                 You cannot instantly get info from the database as it requires time to download it.
     *                 Using this method the callback will occur from addListenerForSingleValueEvent() once
     *                 it has gotten the value at the reference. You can then use it in your queryMaster.
     *
     * @param databaseReference
     * @param q
     */
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

    /**
     * encodeKey:
     *
     * replaces characters which cannot be used in Firebase keys
     *
     * @param key     the key/email you want to encode
     * @return  encodedKey
     */
    public static String encodeKey(String key) {
        try {
            return URLEncoder.encode(key, "UTF-8").replace(".", "%2E");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Encoding Error";
    }

    /**
     * decodeKey:
     *
     * replaces characters which cannot be used in Firebase keys
     *
     * @param key     the key/email you want to encode
     * @return  decodeKey
     */
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

    public DatabaseReference getPassword(String email) {
        return getUserReferenceByEmail(email).child("password");
    }

    public DatabaseReference getUserItemDescription(String email) {
        return getUserReferenceByEmail(email).child("itemDescription");
    }

    public DatabaseReference getBothMatched(String email) {
        return getUserReferenceByEmail(email).child(MainActivity.FIREBASE_BOTH_MATCHED);
    }

    public DatabaseReference getMatchedme(String email) {
        return getUserReferenceByEmail(email).child(MainActivity.FIREBASE_MATCHED_ME);
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

    /**
     * addMatch:
     *
     * adds a match to a Match list in the real time database of a user
     *
     * @param email     user whos matches you want to add to
     * @param matchType     which match list you want to add to
     * @param newMatch     the match you want to add
     */
    public void addMatch(String email, String matchType, final Match newMatch) {
        final DatabaseReference userRef;

        if (matchType.equals(MainActivity.FIREBASE_BOTH_MATCHED))
            userRef = getBothMatched(email);
        else if (matchType.equals(MainActivity.FIREBASE_MATCHED_ME))
            userRef = getMatchedme(email);
        else
            return;

        //if the user exists add match
        executeIfExists(userRef, new QueryMaster() {
            @Override
            public void run(DataSnapshot s) {
                //indicates the data type pulled from database
                GenericTypeIndicator<ArrayList<Match>> t = new GenericTypeIndicator<ArrayList<Match>>() {};
                ArrayList<Match> update = s.getValue(t);
                update.add(newMatch);
                userRef.setValue(update);

            }
        });
    }

    public DatabaseReference getChatRoom(String chatRoomKey) {
        return getChatRoot().child(chatRoomKey);
    }

    /**
     * addMatch:
     *
     * removes a match from a Match list in the real time database of a user
     *
     * @param email     user whos matches you want to add to
     * @param matchType     which match list you want to add to
     * @param position     position of the match you want to add
     */
    public void removeMatch(String email, String matchType, final int position) {
        DatabaseReference userRef;

        if (matchType.equals(MainActivity.FIREBASE_BOTH_MATCHED)) {
            userRef = getBothMatched(email);
        } else {
            userRef = getMatchedme(email);
        }
        final DatabaseReference finalRef = userRef;

        executeIfExists(userRef, new QueryMaster() {
            @Override
            public void run(DataSnapshot s) {
                GenericTypeIndicator<ArrayList<Match>> t = new GenericTypeIndicator<ArrayList<Match>>() {};
                ArrayList<Match> update = s.getValue(t);
                update.remove(position);
                finalRef.setValue(update);

            }
        });
    }

    public void deleteChatRoom(String chatRoomKey) {
        final DatabaseReference ref = getChatRoom(chatRoomKey);

        executeIfExists(ref, new QueryMaster() {
            @Override
            public void run(DataSnapshot s) {
                ref.setValue(null);
            }
        });
    }
}
