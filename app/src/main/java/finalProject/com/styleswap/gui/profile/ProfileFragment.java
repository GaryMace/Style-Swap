package finalProject.com.styleswap.gui.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;/*
<<<<<<< 2e1878d49c8862379371e0311dd0860992cbcffd
import android.util.Log;
=======
import android.view.KeyEvent;
>>>>>>> Fullscreen and bug fixes*/
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import finalProject.com.styleswap.R;
import finalProject.com.styleswap.infrastructure.FireBaseQueries;
import finalProject.com.styleswap.infrastructure.Linker;

/**
 * ProfileFragment:
 *                  This class shows your current profile Information you can access logout of edit
 *                  profile from here.
 *
 *`Created by James on 24/11/16.
 */

public class ProfileFragment extends Fragment {
    public static final int CAMERA_REQUEST = 1888;
    private static final String REVERT_TO_TAG = "profile_fragment";
    private ImageView profilePic;
    private Linker linker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        linker = (Linker) getActivity();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView profileInfo = (TextView) view.findViewById(R.id.profileinfo);
        profileInfo.setText(linker.getUserName());

        TextView profileInfo2 = (TextView) view.findViewById(R.id.profileinfo2);
        profileInfo2.setText(linker.getAge());

        profilePic = (ImageView) view.findViewById(R.id.profile_trans);

        Button mCameraButton = (Button) view.findViewById(R.id.profilecamera);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });


        //logs out by relauching app
        final Button logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getContext().getPackageManager()
                        .getLaunchIntentForPackage( getContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });


        //goes to edit profile fragment
        ImageButton gotoEditProfile = (ImageButton) view.findViewById(R.id.profilepic);
        gotoEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                EditProfileFragment editProf = new EditProfileFragment();

                //this means when you press back from editProfile you will come back to this fragment
                ft.addToBackStack(ProfileFragment.REVERT_TO_TAG);
                ft.replace(R.id.activity_main_fragment_container, editProf, getString(R.string.fragment_edit_profle_id)).commit();
            }
        });
        return view;
    }

    //set image to cached image
    @Override
    public void onStart(){
        super.onStart();
        profilePic.setImageDrawable(linker.getUserProfileImage().getDrawable());
    }

    /*//makes captured photo your profile photo
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profilePic.setImageBitmap(photo);
            linker.setUserProfileImage(photo);
            FireBaseQueries base = new FireBaseQueries();
            base.uploadImageView(profilePic, linker.getLoggedInUser());
        }*/
    /*@Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //On back button pressed
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);  //Just go back to home screen, initially it was logging user out.
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    return true;
                }
                return false;
            }
        });
    }*/
}
