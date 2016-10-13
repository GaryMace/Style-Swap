package jameshassmallarms.com.styleswap.gui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarFragment;
import com.roughike.bottombar.OnTabSelectedListener;

import jameshassmallarms.com.styleswap.R;

/**
 *
 * Created by gary on 10/10/16.
 */

public class BarFragment extends Fragment {
        private BottomBar bottomBar;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                View view = inflater.inflate(R.layout.fragment_bottom_bar, container, false);
                bottomBar = BottomBar.attach((Activity) getContext(), savedInstanceState);
                bottomBar.noTopOffset();
                bottomBar.noNavBarGoodness();

                int currentPosition = 0;

                addBarFragments();
                setTabColours();
                if (savedInstanceState != null) {
                        // Restore last state for checked position.
                        currentPosition = savedInstanceState.getInt("current_state", 0);
                }

                bottomBar.selectTabAtPosition(currentPosition, true);

                bottomBar.setOnItemSelectedListener(new OnTabSelectedListener() {
                        @Override
                        public void onItemSelected(int position) {

                                int str = Settings.System.getInt(getContext().getContentResolver(),
                                        Settings.System.ACCELEROMETER_ROTATION,
                                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                                if (str == 1) {
                                        // rotation is Unlocked

                                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                                } else {
                                        // rotation is Locked
                                        final Fragment liveFragment = getActivity().getSupportFragmentManager().findFragmentByTag("exercise_tag");

                                        if (liveFragment != null) {
                                                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                        }
                                }
                        }
                });
                return  view;
        }

        private void setTabColours() {
                for (int i = 0; i < 2; i++) {
                        bottomBar.mapColorForTab(i, "#000000");
                }
        }

        private void addBarFragments() {
                BottomBarFragment hello = new BottomBarFragment(new HelloFragment(), R.drawable.reviewicon_new, null);
                BottomBarFragment bye = new BottomBarFragment(new GoodbyeFragment(), R.drawable.shimmer_new, null);
                bottomBar.setFragmentItems(getFragmentManager(), R.id.bottom_bar_container,
                        hello,
                        bye
                );
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
                super.onSaveInstanceState(outState);
                outState.putInt("current_state", bottomBar.getCurrentTabPosition());
                bottomBar.onSaveInstanceState(outState);
        }
}
