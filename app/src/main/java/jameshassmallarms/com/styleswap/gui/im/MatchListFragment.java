package jameshassmallarms.com.styleswap.gui.im;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.impl.Match;
import jameshassmallarms.com.styleswap.infrastructure.Linker;

/**
 * Created by gary on 13/10/16.
 */

public class MatchListFragment extends Fragment{
    private RecyclerView mMatchRecycler;
    private Button mDeleteMatch;
    private MatchAdapter mAdapter;
    private Linker linker;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_im, container, false);
        mMatchRecycler = (RecyclerView) view
            .findViewById(R.id.fragment_im_recycler);
        mMatchRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        linker = (Linker) getActivity();
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


        updateUI();


        return view;
    }

    private void updateUI() {
        boolean firebaseServerHasNewData = false;
        List<Match> matches = linker.getCachedMatches();
        if (matches == null) {

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
            matchImage = (CircleImageView) itemView.findViewById(R.id.fragment_im_match_image);
            matchName = (TextView) itemView.findViewById(R.id.fragment_im_match_name);
            matchNumber = (TextView) itemView.findViewById(R.id.fragment_im_match_contact);
            deleteMatch = (Button) itemView.findViewById(R.id.fragment_im_delete_match_button);
        }

        public void bindMatch(Match m) {
            final Match match = m;
            matchName.setText(m.getMatchName());
            matchNumber.setText(m.getMatchNumber());
            matchImage.setImageBitmap(m.getMatchImageKey());
            deleteMatch.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    new Runnable(){
                        @Override
                        public void run() {
                            linker.removeCachedMatch(match);
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
