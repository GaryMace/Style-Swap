package jameshassmallarms.com.styleswap.gui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.impl.User;
import jameshassmallarms.com.styleswap.infrastructure.DatabaseHandler;

/**
 * Created by gary on 10/10/16.
 */

public class GoodbyeFragment extends Fragment {
    private DatabaseHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_db_test, container, false);
        db = new DatabaseHandler(getContext());
        Button addUser = (Button) view.findViewById(R.id.fragment_exp_add_user);
        final ImageView img = (ImageView) view.findViewById(R.id.test_view);
        addUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                User usr = new User("Garymac@live.ie", "password");
                //Add stock match_img when new login!
                //This logic should be something very similar to what will be used when we want to register a new user
                Bitmap stockImg = BitmapFactory.decodeResource(getResources(), R.drawable.stock_img);
                usr.setImg(stockImg);
                usr.setBio("Hello, I'm Gary");
                usr.setDressSize(5);
                usr.setPhoneNum("085 6969969");
                usr.setName("Gary");
                db.addUserToDatabase(usr);

                img.setImageBitmap(db.readImageForUser("Garymac@live.ie"));
                img.setVisibility(View.VISIBLE);
            }
        });

        Button readName = (Button) view.findViewById(R.id.fragment_exp_read_name);
        readName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "User name in db: " + db.readNameForUser("Garymac@live.ie"), Toast.LENGTH_SHORT).show();

            }
        });

        Button readBio = (Button) view.findViewById(R.id.fragment_exp_read_bio);
        readBio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "User bio in db: " + db.readBioForUser("Garymac@live.ie"), Toast.LENGTH_SHORT).show();

            }
        });

        Button updateBio = (Button) view.findViewById(R.id.fragment_exp_update_bio_button);
        updateBio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                db.updateBioForUser("Garymac@live.ie", "Hi, I'm now edited");

                Toast.makeText(getContext(), "Updated bio in db: " + db.readBioForUser("Garymac@live.ie"), Toast.LENGTH_SHORT).show();

            }
        });
        return view;
    }
}
