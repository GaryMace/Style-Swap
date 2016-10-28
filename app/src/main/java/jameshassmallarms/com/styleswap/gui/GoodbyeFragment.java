package jameshassmallarms.com.styleswap.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.impl.User;
import jameshassmallarms.com.styleswap.infrastructure.DatabaseHandler;

/**
 * Created by gary on 10/10/16.
 */

public class GoodbyeFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                View view = inflater.inflate(R.layout.activity_main_test, container, false);

                return view;
        }
}
