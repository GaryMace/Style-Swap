package jameshassmallarms.com.styleswap.gui.im;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.impl.Match;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
import jameshassmallarms.com.styleswap.infrastructure.Linker;
import jameshassmallarms.com.styleswap.infrastructure.QueryMaster;

/**
 * Created by gary on 13/10/16.
 */

public class MatchListFragment extends Fragment {
    private RecyclerView mMatchRecycler;
    private FireBaseQueries db;
    private MatchAdapter mAdapter;
    private Linker linker;

    //Test shit, to be removed
    private TextView lat;
    private TextView lon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_im, container, false);

        mMatchRecycler = (RecyclerView) view
            .findViewById(R.id.fragment_im_recycler);
        mMatchRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMatchRecycler.setHasFixedSize(true);
        linker = (Linker) getActivity();
        db = new FireBaseQueries();

        lat = (TextView) view.findViewById(R.id.lat);
        lon = (TextView) view.findViewById(R.id.lon);
        lat.setText("Lat: " + linker.getDeviceLat());
        lat.setText("Lon: " +linker.getDeviceLon());

        /*FireBaseQueries.executeIfExists(getPhonenumber("GaryMac@live.ie"), new Runnable{
            for (DataSnapshot child: snapshot.getChildren()) {
                String username = (String) child.child("username").getValue();
                usernames.add(username);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                EditFriendsActivity.this,
                android.R.layout.simple_list_item_multiple_choice,
                usernames);

            mFriendsList.setAdapter(adapter);
        }, other);*/
        test();
        updateUI();


        return view;
    }

    private void test() {
        if (linker.getCachedMatches().isEmpty()) {
            Log.d("TAG", "Matches was empty");
            Match m1 = new Match();
            Bitmap img1 = BitmapFactory.decodeResource(getResources(), R.drawable.ja);
            Bitmap img2 = BitmapFactory.decodeResource(getResources(), R.drawable.profilepicexample);
            Bitmap img3 = BitmapFactory.decodeResource(getResources(), R.drawable.ja);
            m1.setMatchImage(Bitmap.createScaledBitmap(img1,200, 200,true));
            m1.setMatchName("James");
            m1.setMatchNumber("085 766 3464");

            Match m2 = new Match();
            m2.setMatchImage(Bitmap.createScaledBitmap(img2,200, 200,true));
            m2.setMatchName("Alan");
            m2.setMatchNumber("082 766 2132");

            Match m3 = new Match();
            m3.setMatchImage(Bitmap.createScaledBitmap(img3,200, 200,true));
            m3.setMatchName("Stock");
            m3.setMatchNumber("087 432 1234");
            linker.addCachedMatch(m1);
            linker.addCachedMatch(m2);
            linker.addCachedMatch(m3);
        }
    }

    private void updateUI() {
        boolean firebaseServerHasNewData = false;
        List<Match> matches = linker.getCachedMatches();
       //DatabaseReference ref = db.getMatches(linker.getLoggedInUser());

        if (matches == null && linker.userLoggedIn()) {

            /*new Thread(new Runnable() {
                public void run() {
                    final Bitmap bitmap =
                        loadImageFromNetwork("http://example.com/image.png");
                    mImageView.post(new Runnable() {
                        public void run() {
                            mImageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }).start();*/

            /*db.executeIfExists(ref, new QueryMaster() {
                @Override
                public void run(DataSnapshot s) {
                    s.getValue();
                }
            });*/

        }
        if (firebaseServerHasNewData) {
            //add new matches
        } else {
            if (mAdapter == null) {
                mAdapter = new MatchAdapter(matches);
            } else {
                mAdapter.notifyDataSetChanged();
            }
            mMatchRecycler.setAdapter(mAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }


    private class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchHolder> {
        private List<Match> matches;

        public MatchAdapter(List<Match> matches) {
            this.matches = matches;
        }

        @Override
        public MatchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_im_match, parent, false);
            return new MatchHolder(view);
        }

        @Override
        public void onBindViewHolder(MatchHolder holder, int position) {
            Match match = matches.get(position);
            holder.bindMatch(match);

        }

        @Override
        public int getItemCount() {
            return matches.size();
        }

        public void removeAt(int position) {
            linker.getCachedMatches().remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, matches.size());
        }

        public class MatchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView matchName;
            private Button deleteMatch;
            private ImageView matchImage;
            private TextView matchNumber;

            public MatchHolder(View itemView) {
                super(itemView);
                matchImage = (ImageView) itemView.findViewById(R.id.fragment_im_match_image);
                matchName = (TextView) itemView.findViewById(R.id.fragment_im_match_name);
                matchNumber = (TextView) itemView.findViewById(R.id.fragment_im_match_contact);
                deleteMatch = (Button) itemView.findViewById(R.id.fragment_im_delete_match_button);
                deleteMatch.setOnClickListener(this);   //why did it take me so long to realise this was missing....FFUUUUU
            }

            public void bindMatch(Match m) {
                matchName.setText(m.getMatchName());
                matchNumber.setText(m.getMatchNumber());
                matchImage.setImageBitmap(m.getMatchImage());
            }

            @Override
            public void onClick(View v) {
                int newPosition = this.getAdapterPosition();

                if(v.equals(deleteMatch)){
                    Log.d("TAG", "removed Image at position" + newPosition);
                    removeAt(getAdapterPosition());
                    notifyItemRangeChanged(newPosition, matches.size());
                    notifyItemChanged(newPosition);
                    //TODO: I dont think this removes it completely, kappa it does for sure.. maybe?
                }
            }
        }
    }
}
