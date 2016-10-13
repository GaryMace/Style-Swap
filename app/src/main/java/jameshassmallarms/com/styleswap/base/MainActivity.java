package jameshassmallarms.com.styleswap.base;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.gui.BarFragment;

public class MainActivity extends AppCompatActivity {
        private BarFragment bottomBar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                // Make us non-modal, so that others can receive touch events.
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

                // ...but notify us that it happened.
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

                bottomBar = new BarFragment();

                FragmentManager fragmentManager;
                fragmentManager = getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.activity_main, bottomBar, getString(R.string.fragment_bottom_bar_id)).commit();

        }
}
