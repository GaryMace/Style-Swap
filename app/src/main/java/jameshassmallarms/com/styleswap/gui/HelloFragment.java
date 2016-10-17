package jameshassmallarms.com.styleswap.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jameshassmallarms.com.styleswap.R;

/**
 * Created by gary on 10/10/16.
 */

public class HelloFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);


                return  view;
        }
}
