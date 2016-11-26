package jameshassmallarms.com.styleswap.gui.im;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import jameshassmallarms.com.styleswap.infrastructure.DatabaseHandler;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
import jameshassmallarms.com.styleswap.infrastructure.Linker;
import jameshassmallarms.com.styleswap.infrastructure.QueryMaster;

/**
 * MatchListFragment:
 *
 *              This Fragment is attached to the BarFragment. It will display all the current matches
 *              that the logged in user has. Each Match is inflated to a recycle-view item that is
 *              independently populated with information pulled from FireBase. This information is
 *              stored in a list called cachedMatches in MainActivity so that if we tab change and go
 *              back to this fragment we don't have to re-query firebase.
 *
 *              Instead the fragment checks for updates, if no updates from firebase are received then
 *              it inflates the cached list from MainActivity which provides a fluid and instant user
 *              experience.
 *
 *              When the user long clicks on a list item it opens up a chat fragment which allows real-time
 *              instant messaging between the two users. You can also delete matches which removes
 *              that match from your list, their list and also deletes the chat room between you.
 *
 *               Created by gary on 13/10/16.
 *
 */
public class MatchListFragment extends Fragment {
    public static final String ARGUMENT_MATCH_IMAGE = "match_img";
    public static final String ARGUMENT_MATCH_NAME = "match_name";
    public static final String ARGUMENT_CHAT_KEY = "match_chat_key";
    private static final String REVERT_TO_TAG = "match_list_fragment";
    private static final String TAG = "debug_match";
    private ProgressBar mProgressBar;
    private RecyclerView mMatchRecycler;
    private FireBaseQueries db;
    private MatchAdapter mAdapter;
    private Linker linker;                      //Linker is an interface that lets us get cached data from MainActivity quickly
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_im, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mMatchRecycler = (RecyclerView) view
                .findViewById(R.id.fragment_im_recycler);
        mMatchRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMatchRecycler.setHasFixedSize(true);
        linker = (Linker) getActivity();
        db = new FireBaseQueries();

        //TODO: Store cached matches in database, reload them so this doesn't skip layout due to empty adapter
        //updateUI();
        getMatches(linker.getLoggedInUser());   //Get matches from firebase for the current logged in user

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
                update.remove(0);
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
        StorageReference picRef = storage.getReferenceFromUrl("gs://styleswap-f3aa9.appspot.com").child(username + "/" + imagename);

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
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
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
            holder.bindMatch(match);                                //Bind this.match to a visible list item in the adapter

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
            private LinearLayout mListItemContainer;
            private TextView matchName;
            private Button deleteMatch;
            private ImageView matchImage;
            private TextView matchNumber;
            private String matchChatKey;

            public MatchHolder(View itemView) {
                super(itemView);
                mListItemContainer = (LinearLayout) itemView.findViewById(R.id.fragment_im_list_item);
                matchImage = (ImageView) itemView.findViewById(R.id.fragment_im_match_image);
                matchName = (TextView) itemView.findViewById(R.id.fragment_im_match_name);
                matchNumber = (TextView) itemView.findViewById(R.id.fragment_im_match_contact);
                deleteMatch = (Button) itemView.findViewById(R.id.fragment_im_delete_match_button);
                deleteMatch.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int newPosition = getAdapterPosition();

                        Log.d("TAG", "removed Image at position" + newPosition);
                        removeAt(getAdapterPosition());                                 //Remove this user from my bothMatched list locally and on Firebase
                        notifyItemRangeChanged(newPosition, matches.size());            //Remove user from visible list locally
                        notifyItemChanged(newPosition);
                    }
                });   //why did it take me so long to realise this was missing....FFUUUUU
                mListItemContainer.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {                        //Launch IM fragment and add this fragment to back stack
                        Log.d("TAG", "Clicked Match, launching im fragment");
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ChatIm chatFragment = new ChatIm();

                        //Pass match data to fragment we're about to launch
                        Bundle argData = new Bundle();
                        Bitmap compressedImg = ((BitmapDrawable)matchImage.getDrawable()).getBitmap();
                        compressedImg = Bitmap.createScaledBitmap(compressedImg, 100, 100, false);

                        byte[] img = DatabaseHandler.createByteArray(compressedImg);
                        argData.putByteArray(ARGUMENT_MATCH_IMAGE, img);
                        argData.putString(ARGUMENT_MATCH_NAME, matchName.getText().toString());
                        argData.putString(ARGUMENT_CHAT_KEY, matchChatKey);
                        chatFragment.setArguments(argData);

                        ft.addToBackStack(MatchListFragment.REVERT_TO_TAG);
                        ft.replace(R.id.activity_main_fragment_container, chatFragment, getString(R.string.fragment_im_id)).commit();
                        return false;
                    }
                });
            }

            public void bindMatch(Match m) {
                matchChatKey = m.getChatKey();
                matchName.setText(m.getMatchName());
                matchNumber.setText(m.getMatchNumber());

                if (m.getMatchImage() != null)                  //We already have this matches image locally so don't re-download it
                    matchImage.setImageBitmap(m.getMatchImage());
                else
                    download(matchImage, m.getMatchMail(), "Dress", getAdapterPosition());  //We don't have it locally so download it
            }
        }
    }
}
