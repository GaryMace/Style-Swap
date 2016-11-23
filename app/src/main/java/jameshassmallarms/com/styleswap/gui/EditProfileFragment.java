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

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
        private String userEmail = "haymakerStirrat@gmail.com";
        FireBaseQueries fireBaseQueries = new FireBaseQueries();
        DatabaseReference mUserRef = fireBaseQueries.getUserReferenceByEmail(userEmail);//users email programatically
        ImageView imageView;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
                editProfileButton = (Button) view.findViewById(R.id.editPhotoButton);
                editProfileButton2 = (Button) view.findViewById(R.id.editPhotoButton2);

                itemDescription = (EditText) view.findViewById(R.id.itemDescription);

                imageView = (ImageView) view.findViewById(R.id.profileImage);
                fireBaseQueries.download(imageView, userEmail);
                return view;

        }


        public void onStart() {
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
                                fireBaseQueries.uploadImageView(imageView, userEmail);

                        } else {
                                Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
                        }
                } catch (Exception e) {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }

        }

}