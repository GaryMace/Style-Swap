package Android.com.styleswap.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import Android.com.styleswap.R;


/**
 * Created by Alan on 21/11/2016.
 */

public class BlankFragment extends Fragment{

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the view
        View root = inflater.inflate(R.layout.fragment_blank_screen, container, false);
        return root;
    }
}
