package finalProject.com.styleswap.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import finalProject.com.styleswap.R;


/**
 * Created by Alan on 21/11/2016.
 *
 *  Sets the nested fragment as a searching for matches fragment.
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
