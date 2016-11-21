package jameshassmallarms.com.styleswap.gui;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.impl.Match;
import jameshassmallarms.com.styleswap.impl.User;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
import jameshassmallarms.com.styleswap.infrastructure.QueryMaster;

import static android.app.Activity.RESULT_OK;

/**
 * Created by gary on 10/10/16.
 */

public class EditProfileFragment extends Fragment {
        private EditText itemDescription;
        private Button editProfileButton;
        private Button editProfileButton2;
        FireBaseQueries fireBaseQueries = new FireBaseQueries();
        DatabaseReference mUserRef = fireBaseQueries.getUserReferenceByEmail("haymakerStirrat@gmail.com");//users email programatically
        ImageView imageView ;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
                editProfileButton = (Button) view.findViewById(R.id.editPhotoButton);
                editProfileButton2 = (Button) view.findViewById(R.id.editPhotoButton2);

                itemDescription = (EditText) view.findViewById(R.id.itemDescription);

                imageView = (ImageView) view.findViewById(R.id.profileImage);
                fireBaseQueries.download(imageView, "haymakerStirrat@gmail.com", "Dress");
                return  view;

        }


        public void onStart(){
                super.onStart();
                editProfileButton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                //fireBaseQueries.getUserItemDescription(email).setV;
                                fireBaseQueries.executeIfExists(mUserRef, new QueryMaster() {
                                        @Override
                                        public void run(DataSnapshot s) {
                                                mUserRef.child("itemDescription").setValue(itemDescription.getText().toString());
                                                mUserRef.child("itemDescription").setValue(itemDescription.getText().toString());//replace with other edit texts
                                                mUserRef.child("itemDescription").setValue(itemDescription.getText().toString());
                                        }
                                });

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

        //getPhonenumber()
        // execute If exsists( runnable)
        //runable = whatever code i want

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

//                                User testUser = new User("test@gmail.com", "pass");
//                                fireBaseQueries.pushNewUserDetails(testUser);
//                                Match newMatch = new Match();
//                                newMatch.setMatchName("haymakerStirrat@gmail.com");
//                                fireBaseQueries.addMatch(testUser.getEmail(), "iMatched", newMatch);