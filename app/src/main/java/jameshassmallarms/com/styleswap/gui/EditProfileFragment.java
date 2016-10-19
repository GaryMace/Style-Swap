package jameshassmallarms.com.styleswap.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import jameshassmallarms.com.styleswap.R;

/**
 * Created by gary on 10/10/16.
 */

public class EditProfileFragment extends Fragment {
        private EditText itemDescription;
        private Button editProfileButton;
        private String userName = "Haymaker Stirrat";
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mUserRef = mRootRef.child("Users").child(userName);
        Query q = mRootRef.child("Users").equalTo("good dress");



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
                editProfileButton = (Button) view.findViewById(R.id.editPhotoButton);
                itemDescription = (EditText) view.findViewById(R.id.itemDescription);

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
                                mUserRef.child("itemDescription").setValue(itemDescription.getText().toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                });
                editProfileButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                               mRootRef.child("Users").orderByChild("itemDescription").startAt("good dress").endAt("good dress").addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {

                                               System.out.println(dataSnapshot.getValue().toString());
                                       }

                                       @Override
                                       public void onCancelled(DatabaseError databaseError) {

                                       }
                               });
                        }
                });
        }
}
