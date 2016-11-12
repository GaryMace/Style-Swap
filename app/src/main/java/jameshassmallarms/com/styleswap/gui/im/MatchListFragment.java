package jameshassmallarms.com.styleswap.gui.im;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            m1.setMatchImage(img1);
            m1.setMatchName("James");
            m1.setMatchNumber("085 766 3464");

            Match m2 = new Match();
            m2.setMatchImage(img2);
            m2.setMatchName("Alan");
            m2.setMatchNumber("082 766 2132");

            Match m3 = new Match();
            m3.setMatchImage(img3);
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


    private class MatchHolder extends RecyclerView.ViewHolder {
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
        }

        public void bindMatch(Match m) {
            final Match match = m;
            matchName.setText(m.getMatchName());
            matchNumber.setText(m.getMatchNumber());
            matchImage.setImageBitmap(m.getMatchImage());
            deleteMatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Runnable() {
                        @Override
                        public void run() {
                            Log.d("TAG", "delete clicked");
                            linker.removeCachedMatch(match);
                            matchName.setVisibility(View.GONE);
                            matchNumber.setVisibility(View.GONE);
                            matchImage.setVisibility(View.GONE);
                        }
                    }.run();
                }
            });
        }
    }

    private class MatchAdapter extends RecyclerView.Adapter<MatchHolder> {
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
    }
}
