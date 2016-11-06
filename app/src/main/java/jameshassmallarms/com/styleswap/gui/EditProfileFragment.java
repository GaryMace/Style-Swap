package jameshassmallarms.com.styleswap.gui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Toast;

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

import static android.app.Activity.RESULT_OK;

/**
 * Created by gary on 10/10/16.
 */

public class EditProfileFragment extends Fragment {
        private EditText itemDescription;
        private Button editProfileButton;
        FireBaseQueries fireBaseQueries = new FireBaseQueries();
        DatabaseReference mUserRef = fireBaseQueries.getUserReferenceByEmail("haymakerStirrat@gmail.com");
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
                                loadImagefromGallery(getView());
                        }
                });

        }

        @Override
        public void onResume() {
                super.onResume();


        }

        public void loadImagefromGallery(View view) {
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, 1);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                try {
                        // When an Image is picked
                        if (requestCode == 1 && resultCode == RESULT_OK
                                && null != data) {
                                // Get the Image from data
                                imageView.setImageURI(data.getData());
                                fireBaseQueries.uploadImageView(imageView, "haymakerStirrat@gmail.com", "Dress");

                        } else {
                                Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
                        }
                } catch (Exception e) {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }

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
//        Uri selectedImage = data.get
//        String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//        // Get the cursor
//        Cursor cursor = getContext().getContentResolver().query(selectedImage,
//                filePathColumn, null, null, null);
//// Move to first row
//cursor.moveToFirst();
//
//        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//        imgDecodableString = cursor.getString(columnIndex);
//        File f = new File(imgDecodableString);
//        System.out.println(BitmapFactory.decodeFile(f.getAbsolutePath()));
//        cursor.close();
//// Set the Image in ImageView after decoding the String
//imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));