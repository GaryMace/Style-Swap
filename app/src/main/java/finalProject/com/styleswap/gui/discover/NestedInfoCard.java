package finalProject.com.styleswap.gui.discover;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import finalProject.com.styleswap.R;
import finalProject.com.styleswap.infrastructure.FireBaseQueries;

/**
 * Created by Alan on 25/10/2016.
 *
 *  NestedInfoCard:
 *
 *  This is the nested fragment which uses the bundle which is passed in from swipeButtonsFragment.
 *  It assigns the name, description to a Text view within the card and Assigns the byte array to the imageView.
 *
 */


public class NestedInfoCard extends Fragment {

    private FireBaseQueries test = new FireBaseQueries();
    private TextView userName;
    private TextView description;
    private ImageView userPic;
    private String email;
    private String number;
    private String user;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private ProgressBar mProgressBar;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_user_information, container, false);
        userName = (TextView) root.findViewById(R.id.fragment_person_username);
        description = (TextView) root.findViewById(R.id.fragment_item_desc);
        userPic = (ImageView) root.findViewById(R.id.fragment_swipe_picture);
        mProgressBar = (ProgressBar) root.findViewById(R.id.blank_frag_progress_bar);
        Bundle b = getArguments();
        if (b != null) {
            user = b.getString("UserName");
            email = b.getString("Email");
            String desc = b.getString("Description");
            byte[] img = b.getByteArray("IMG");
            number = b.getString("Num");
            Log.d("Debug_nestedCard", user);
            userName.setText(user);
            description.setText(desc);
            Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            userPic.setImageBitmap(bmp);
        }

        return root;
    }


    public String getName(){
        return user;
    }






}
