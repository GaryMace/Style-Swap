package jameshassmallarms.com.styleswap.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jameshassmallarms.com.styleswap.R;

/**
 * Created by gary on 10/10/16.
 */

public class HelloFragment extends Fragment {
        private Button mEditProfileButton;
        private String name;
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mNameRef = mRootRef.child("userName");

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
                mEditProfileButton = (Button)view.findViewById(R.id.editPhotoButton);

                return  view;
        }

        public void onStart(){
                super.onStart();
                mNameRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                name = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                });

                mEditProfileButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                if(name == "Nugs")
                                        mNameRef.setValue("Haymaker Stirrat");
                                else
                                        mNameRef.setValue("Nugs");
                        }
                });
        }
}
