package jameshassmallarms.com.styleswap.gui.im;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.base.MainActivity;
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

        //TODO: Store cached matches in database, reload them so this doesn't skip layout due to empty adapter
        //updateUI();
        getMatches(linker.getLoggedInUser());

        return view;
    }

    //TODO: ignore first item in list! it's the dummy field
    public void getMatches(String email) {
        final DatabaseReference userRef;
        userRef = db.getBothMatched(email);

        db.executeIfExists(userRef, new QueryMaster() {
            @Override
            public void run(DataSnapshot s) {
                GenericTypeIndicator<ArrayList<Match>> t = new GenericTypeIndicator<ArrayList<Match>>() {
                };
                ArrayList<Match> update = s.getValue(t);
                if (linker.getCachedMatches().isEmpty() ||
                    update.size() > linker.getCachedMatches().size() ||
                    update.size() < linker.getCachedMatches().size() ) {

                    linker.setCachedMatches(update);
                }
                updateUI();
            }
        });
    }
    public void download(final ImageView imageView, String username, String imagename, final int position) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference picRef = storage.getReferenceFromUrl("gs://styleswap-4075c.appspot.com").child(username + "/" + imagename);

        final long ONE_MEGABYTE = 1024 * 1024;
        picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
                linker.getCachedMatches()
                    .get(position)
                    .setMatchImage(((BitmapDrawable)imageView.getDrawable()).getBitmap());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void updateUI() {
        if (!linker.getCachedMatches().isEmpty()) {
            if (mAdapter == null) {
                mAdapter = new MatchAdapter(linker.getCachedMatches());
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
        //getMatches(linker.getLoggedInUser());
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

            db.removeMatch(linker.getLoggedInUser(), MainActivity.FIREBASE_BOTH_MATCHED, position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, matches.size());
        }

        public class MatchHolder extends RecyclerView.ViewHolder {
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
                deleteMatch.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int newPosition = getAdapterPosition();

                        Log.d("TAG", "removed Image at position" + newPosition);
                        removeAt(getAdapterPosition());
                        notifyItemRangeChanged(newPosition, matches.size());
                        notifyItemChanged(newPosition);
                    }
                });   //why did it take me so long to realise this was missing....FFUUUUU
            }

            public void bindMatch(Match m) {
                matchName.setText(m.getMatchName());
                matchNumber.setText(m.getMatchNumber());

                if (m.getMatchImage() != null)
                    matchImage.setImageBitmap(m.getMatchImage());
                else
                    download(matchImage, m.getMatchMail(), "Dress", getAdapterPosition());
                Log.d("TAG", "Mail is: \"" +m.getMatchMail() + "\"");
            }
        }
    }
}
