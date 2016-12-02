package finalProject.com.styleswap.gui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;


import finalProject.com.styleswap.R;
import finalProject.com.styleswap.infrastructure.FireBaseQueries;
import finalProject.com.styleswap.infrastructure.Linker;
import finalProject.com.styleswap.infrastructure.QueryMaster;



/**
 * EditProfileFragment:
 *
 *                  This class allows you to edit you accounts details. You can select new photos
 *                  from your gallery and edit your other detail people will see when they match
 *                  with you.
 *
 *`Created by James on 24/11/16.
 */

public class EditProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public static final int RESULT_LOAD_IMAGE = 4;
    private EditText itemDescription;
    private EditText userNumber;
    private EditText userName;
    private Linker linker;
    private String userEmail = null;
    private int mNewDressSize;
    FireBaseQueries fireBaseQueries = new FireBaseQueries();
    ImageView imageView;
    private static final String REVERT_TO_TAG = "edit_profile_fragment";
    public Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        Button mUploadNewImage = (Button) view.findViewById(R.id.editPhotoButton);
        Button mDoneButton = (Button) view.findViewById(R.id.editPhotoButton2);

        linker = (Linker) getActivity();

        itemDescription = (EditText) view.findViewById(R.id.itemDescription);
        itemDescription.setText(linker.getItemDescription());

        userName = (EditText) view.findViewById(R.id.userName);
        userName.setText(linker.getUserName());

        userNumber = (EditText) view.findViewById(R.id.userNumber);
        userNumber.setText(linker.getPhoneNumber());

        userEmail = linker.getLoggedInUser();
        mNewDressSize = linker.getDressSize();
        imageView = (ImageView) view.findViewById(R.id.profileImage);
        imageView.setImageDrawable(linker.getUserProfileImage().getDrawable());

        spinner = (Spinner) view.findViewById(R.id.spinner1);

        ArrayAdapter adapter =
            ArrayAdapter.createFromResource(this.getActivity(), R.array.dress_sizes, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //Set default adapter position to be users dress size so they dont accidentally change dress size to 2..
        int spinnerPosition = adapter.getPosition(linker.getDressSize()+"");

        //set the default according to value
        spinner.setSelection(spinnerPosition);

        mUploadNewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagefromGallery();
            }
        });

        //updates account info in online database when you click done
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference mUserRef = fireBaseQueries.getUserReferenceByEmail(userEmail);
                fireBaseQueries.executeIfExists(mUserRef, new QueryMaster() {
                    @Override
                    public void run(DataSnapshot s) {
                        mUserRef.child("itemDescription").setValue(itemDescription.getText().toString());
                        mUserRef.child("name").setValue(userName.getText().toString());
                        mUserRef.child("phoneNum").setValue(userNumber.getText().toString());
                        mUserRef.child("dressSize").setValue(mNewDressSize);
                        linker.setItemDescription(itemDescription.getText().toString());
                        linker.setPhoneNumber(userNumber.getText().toString());
                        linker.setUserName(userName.getText().toString());
                        linker.setDressSize(mNewDressSize);

                    }
                });
                getFragmentManager().popBackStack();

            }
        });
        return view;
    }

    //load cached profile image
    public void onStart() {
        super.onStart();
        imageView.setImageDrawable(linker.getUserProfileImage().getDrawable());
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    //opens gallery and lets you pick photo
    public void loadImagefromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent,
            "Select Picture"), RESULT_LOAD_IMAGE);
    }

    //makes your selected photo your new dress profile photo
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
            fireBaseQueries.uploadImageView(imageView, userEmail);

        } else if (resultCode == Activity.RESULT_CANCELED) {}
    }

    //updates dress size when you select on spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int newDressSize = Integer.parseInt(adapterView.getItemAtPosition(i).toString());
        mNewDressSize = newDressSize;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}