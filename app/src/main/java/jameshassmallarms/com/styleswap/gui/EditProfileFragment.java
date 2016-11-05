package jameshassmallarms.com.styleswap.gui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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
import java.io.File;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;

/**
 * Created by gary on 10/10/16.
 */

public class EditProfileFragment extends Fragment {
        private EditText itemDescription;
        private Button editProfileButton;
        FireBaseQueries fireBaseQueries = new FireBaseQueries();
        DatabaseReference mUserRef = fireBaseQueries.getUserReferenceByEmail("haymakerStirrat@gmail.com");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        ImageView imageView ;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
                editProfileButton = (Button) view.findViewById(R.id.editPhotoButton);
                itemDescription = (EditText) view.findViewById(R.id.itemDescription);
                imageView = (ImageView) view.findViewById(R.id.profileImage);
                return  view;
        }

        @Override
        public void onPause() {
                super.onPause();
        }

        @Override
        public void onResume() {
                super.onResume();
                if (fireBaseQueries.exsists != null){

                }

        }

        public void onStart(){
                super.onStart();
                itemDescription.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                fireBaseQueries.executeIfExsits(mUserRef, new Runnable() {
                                        @Override
                                        public void run() {
                                                mUserRef.child("itemDescription").setValue(itemDescription.getText().toString());
                                        }
                                });


                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                });

                editProfileButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                //upload();
                                download();
                        }
                });
        }



        public void upload(){

                // Get the data from an ImageView as bytes
                StorageReference picRef = storage.getReferenceFromUrl("gs://styleswap-4075c.appspot.com").child("images").child("User1");
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                Bitmap bitmap = imageView.getDrawingCache();
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
        public void download() {

                StorageReference picRef = storage.getReferenceFromUrl("gs://styleswap-4075c.appspot.com").child("images/User1");

                final long ONE_MEGABYTE = 1024 * 1024;
                picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                System.out.println("aaa");
                                imageView.setImageBitmap(bmp);

                        }
                }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                        }
                });
        }
//                //example query
//                editProfileButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                               mRootRef.child("Users").orderByChild("itemDescription").startAt("good dress").endAt("good dress").addListenerForSingleValueEvent(new ValueEventListener() {
//                                       @Override
//                                       public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                               System.out.println(dataSnapshot.getValue().toString());
//                                       }
//
//                                       @Override
//                                       public void onCancelled(DatabaseError databaseError) {
//
//                                       }
//                               });
//                        }
//                });

}
