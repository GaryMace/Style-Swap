package jameshassmallarms.com.styleswap.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
import jameshassmallarms.com.styleswap.gui.im.ProfileFragment;
import jameshassmallarms.com.styleswap.impl.Match;
import jameshassmallarms.com.styleswap.impl.User;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
import jameshassmallarms.com.styleswap.infrastructure.QueryMaster;

import static android.app.Activity.RESULT_OK;

/**
 * Created by gary on 10/10/16.
 */

public class EditProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {
        private EditText itemDescription;
        private Button editProfileButton;
        private Button editProfileButton2;
        private String userEmail = "haymakerStirrat@gmail.com";
        FireBaseQueries fireBaseQueries = new FireBaseQueries();
        DatabaseReference mUserRef = fireBaseQueries.getUserReferenceByEmail(userEmail);//users email programatically
        ImageView imageView;
        private static final String REVERT_TO_TAG = "edit_profile_fragment";
        public Spinner spinner;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
                editProfileButton = (Button) view.findViewById(R.id.editPhotoButton);
                editProfileButton2 = (Button) view.findViewById(R.id.editPhotoButton2);

                itemDescription = (EditText) view.findViewById(R.id.itemDescription);

                imageView = (ImageView) view.findViewById(R.id.profileImage);
                fireBaseQueries.download(imageView, userEmail);
                spinner = (Spinner) view.findViewById(R.id.spinner1);
                ArrayAdapter adapter = ArrayAdapter.createFromResource(this.getActivity(),R.array.dress_sizes,android.R.layout.simple_spinner_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(this);
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
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = manager.beginTransaction();
                                ProfileFragment editProf = new ProfileFragment();


                                ft.addToBackStack(EditProfileFragment.REVERT_TO_TAG);
                                ft.replace(R.id.activity_main_fragment_container, editProf, getString(R.string.fragment_profile_id)).commit();

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

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView myText = (TextView) view;
                Toast.makeText(this.getActivity(), "You Selected "+myText.getText(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
}