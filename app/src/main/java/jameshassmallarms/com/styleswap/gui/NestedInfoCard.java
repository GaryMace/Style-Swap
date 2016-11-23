package jameshassmallarms.com.styleswap.gui;

import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
import jameshassmallarms.com.styleswap.infrastructure.QueryMaster;

import static jameshassmallarms.com.styleswap.R.drawable.james;

/**
 * Created by Alan on 25/10/2016.
 */

public class NestedInfoCard extends Fragment {

    private FireBaseQueries test = new FireBaseQueries();
    private TextView userName;
    private TextView description;
    private ImageView userPic;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    QueryMaster q = new QueryMaster() {
        @Override
        public void run(DataSnapshot s) {
            description.setText(s.getValue().toString());
        }
    };

    QueryMaster P = new QueryMaster() {
        @Override
        public void run(DataSnapshot s) {
            userName.setText(s.getValue().toString());
        }
    };


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_user_information, container, false);
        userName = (TextView) root.findViewById(R.id.fragment_person_username);
        description = (TextView) root.findViewById(R.id.fragment_item_desc);
        userPic = (ImageView) root.findViewById(R.id.fragment_swipe_picture);
        Bundle b = getArguments();
        if (b != null) {
            String user = b.getString("UserName");
            userName.setText(user);
            description.setText("Testing the name only");
            userPic.setImageResource(R.drawable.james);
            // getUserInfo(user);
            /*test.download(userPic, user, "Dress");
            DatabaseReference tester = test.getUserItemDescription(user);
            test.executeIfExists(tester, q);
           // test.executeIfExists(tester, P);
            userName.setText("Kill me");
*/
        }
        return root;
    }

   private void getUserInfo(String u){
       userName.setText(u);
   }




}
