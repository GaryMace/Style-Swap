package jameshassmallarms.com.styleswap.gui.im;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.gui.EditProfileFragment;

public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String REVERT_TO_TAG = "profile_fragment";
    private ImageButton gotoEditProfile;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
