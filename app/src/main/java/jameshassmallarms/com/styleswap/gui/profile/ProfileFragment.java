package jameshassmallarms.com.styleswap.gui.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import jameshassmallarms.com.styleswap.R;

public class ProfileFragment extends Fragment {
    private static final String REVERT_TO_TAG = "profile_fragment";
    private ImageButton gotoEditProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        gotoEditProfile = (ImageButton) view.findViewById(R.id.profilepic);
        gotoEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                EditProfileFragment editProf = new EditProfileFragment();


                ft.addToBackStack(ProfileFragment.REVERT_TO_TAG);
                ft.replace(R.id.activity_main_fragment_container, editProf, getString(R.string.fragment_edit_profle_id)).commit();
            }
        });
        return view;
    }
}
