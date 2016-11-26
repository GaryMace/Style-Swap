package jameshassmallarms.com.styleswap.gui.profile;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
import jameshassmallarms.com.styleswap.infrastructure.Linker;
import jameshassmallarms.com.styleswap.infrastructure.QueryMaster;

import static android.app.Activity.RESULT_OK;

/**
 * Created by gary on 10/10/16.
 */

public class EditProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {
        public static final int RESULT_LOAD_IMAGE = 4;
        private EditText itemDescription;
        private EditText userNumber;
        private EditText userName;
        private Button editProfileButton;
        private Button editProfileButton2;
        private Linker linker;
        private String userEmail = null;
        FireBaseQueries fireBaseQueries = new FireBaseQueries();
        static ImageView imageView;
        private static final String REVERT_TO_TAG = "edit_profile_fragment";
        public Spinner spinner;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

                editProfileButton = (Button) view.findViewById(R.id.editPhotoButton);
                editProfileButton2 = (Button) view.findViewById(R.id.editPhotoButton2);

                linker = (Linker) getActivity();

                itemDescription = (EditText) view.findViewById(R.id.itemDescription);
                itemDescription.setText(linker.getItemDescription());

                userName = (EditText) view.findViewById(R.id.userName);
                userName.setText(linker.getUserName());

                userNumber = (EditText) view.findViewById(R.id.userNumber);
                userNumber.setText(linker.getPhoneNumber());

                userEmail = linker.getLoggedInUser();

                imageView = (ImageView) view.findViewById(R.id.profileImage);
                imageView.setImageDrawable(linker.getUserProfileImage().getDrawable());

                spinner = (Spinner) view.findViewById(R.id.spinner1);
                ArrayAdapter adapter = ArrayAdapter.createFromResource(this.getActivity(),R.array.dress_sizes,android.R.layout.simple_spinner_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(this);

                editProfileButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                loadImagefromGallery(getView());
                        }
                });

                editProfileButton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                //fireBaseQueries.getUserItemDescription(email).setV;
                        final DatabaseReference mUserRef = fireBaseQueries.getUserReferenceByEmail(userEmail);
                        fireBaseQueries.executeIfExists(mUserRef, new QueryMaster() {
                                @Override
                                public void run(DataSnapshot s) {
                                        mUserRef.child("itemDescription").setValue(itemDescription.getText().toString());
                                        mUserRef.child("name").setValue(userName.getText().toString());
                                        mUserRef.child("phoneNum").setValue(userNumber.getText().toString());
                                        //mUserRef.child("dressSize").setValue(.getText().toString());
                                        mUserRef.child("dressSize").setValue(8);
                                }
                        });
                        getFragmentManager().popBackStack();

                        }
                });
                return view;

        }


        public void onStart() {
                super.onStart();

        }


        @Override
        public void onResume() {
                super.onResume();


        }

        public void loadImagefromGallery(View view) {
                // Create intent to Open Image applications like Gallery, Google Photos
              /*  Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                Log.d("TAGGE", linker.getLoggedInUser());
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);*/
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), RESULT_LOAD_IMAGE);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                Log.d("TAGGE", "THANK YOU TO FUCK: res code is " + requestCode);
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
                        Uri selectedImageUri = data.getData();
                        imageView.setImageURI(selectedImageUri);

                        Log.d("TAGGE", "result ok: ");

                        fireBaseQueries.uploadImageView(imageView, userEmail);
                        //imageView.setImageDrawable(linker.getUserProfileImage().getDrawable());
                } else if (resultCode == Activity.RESULT_CANCELED) {
                        Log.d("TAGGE", "result canceled: ");

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

        public static void callback(ImageView v) {
                imageView = v;
        }
}