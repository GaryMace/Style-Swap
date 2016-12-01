package finalProject.com.styleswap.gui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import finalProject.com.styleswap.R;
import finalProject.com.styleswap.infrastructure.Linker;

public class ProfileFragment extends Fragment {
    private static final String REVERT_TO_TAG = "profile_fragment";
    private ImageButton gotoEditProfile;
    private Button logout;
    private TextView profileinfo;
    private TextView profileinfo2;
    private ImageView profilePic;
    private Linker linker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        linker = (Linker) getActivity();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        profileinfo = (TextView) view.findViewById(R.id.profileinfo);
        profileinfo.setText(linker.getUserName());

        profileinfo2 = (TextView) view.findViewById(R.id.profileinfo2);
        profileinfo2.setText(linker.getAge());

        profilePic = (ImageView) view.findViewById(R.id.profile_trans);

        logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getContext().getPackageManager()
                        .getLaunchIntentForPackage( getContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });

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

    @Override
    public void onStart(){
        super.onStart();
        System.out.println("try use");
        profilePic.setImageDrawable(linker.getUserProfileImage().getDrawable());
    }
}
