package finalProject.com.styleswap.gui.im;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import finalProject.com.styleswap.R;
import finalProject.com.styleswap.base.MainActivity;
import finalProject.com.styleswap.impl.Match;
import finalProject.com.styleswap.infrastructure.DatabaseHandler;
import finalProject.com.styleswap.infrastructure.FireBaseQueries;
import finalProject.com.styleswap.infrastructure.Linker;
import finalProject.com.styleswap.infrastructure.QueryMaster;

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
    private LinearLayout mHasMatches;
    private RecyclerView mMatchRecycler;
    private FireBaseQueries mDb;
    private MatchAdapter mAdapter;
    private Linker linker;                      //Linker is an interface that lets us get cached data from MainActivity quickly
    private FragmentManager fragmentManager;
    private boolean mFoundNewUpdatesFromFirebase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_im, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mHasMatches = (LinearLayout) view.findViewById(R.id.fragment_im_no_matches);
        mHasMatches.setVisibility(LinearLayout.INVISIBLE);

        mMatchRecycler = (RecyclerView) view
                .findViewById(R.id.fragment_im_recycler);
        mMatchRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMatchRecycler.setHasFixedSize(true);
        linker = (Linker) getActivity();
        mDb = new FireBaseQueries();

        Log.d(TAG, "Liker email for matches is: "+linker.getLoggedInUser());
        getMatches(linker.getLoggedInUser());   //Get matches from firebase for the current logged in user

        return view;
    }

    /**
     * Once this fragment is re-inflated to the screen we need to check if there are any new matches, but also
     * to check if anyone has deleted us.
     *
     */
    public void getMatches(String email) {
        final DatabaseReference userRef;
        userRef = mDb.getBothMatched(email);

        mDb.executeIfExists(userRef, new QueryMaster() {
            @Override
            public void run(DataSnapshot s) {
                GenericTypeIndicator<ArrayList<Match>> t = new GenericTypeIndicator<ArrayList<Match>>() {
                };
                ArrayList<Match> update = s.getValue(t);
                update.remove(0);                       //First item from firebase is always a dummy (otherwise firebase list wont exist), ignore it here.
                if (linker.getCachedMatches().isEmpty() ||
                    update.size() > linker.getCachedMatches().size() ||     //If there's been some update get the new list.
                    update.size() < linker.getCachedMatches().size() ) {

                    mFoundNewUpdatesFromFirebase = true;
                    linker.setCachedMatches(update);
                }

                updateUI();
            }
        });
    }
    public void download(final ImageView imageView, String username, String imagename, final int position) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference picRef = storage.getReferenceFromUrl("gs://styleswap-70481.appspot.com").child(username + "/" + imagename);

        final long ONE_MEGABYTE = 1024 * 1024;
        picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
                linker.getCachedMatches()       //Cache the match for faster fragment inflation on tab revisit. Will be slow first time only this way
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
            if (mAdapter == null || mFoundNewUpdatesFromFirebase) {
                mAdapter = new MatchAdapter(linker.getCachedMatches());
                mFoundNewUpdatesFromFirebase = false;
            } else {
                mAdapter.notifyDataSetChanged();
            }
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);      //Matches found, hide the progress bar
            mHasMatches.setVisibility(LinearLayout.INVISIBLE);      //Has matches so hide the textview
            mMatchRecycler.setAdapter(mAdapter);
        } else {
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);      //Matches found, hide the progress bar
            mHasMatches.setVisibility(LinearLayout.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        getMatches(linker.getLoggedInUser());
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
            holder.bindMatch(match);                                //Bind this match to a visible list item in the adapter

        }

        @Override
        public int getItemCount() {
            return matches.size();
        }

        public void removeAt(int position) {
            Match m = linker.getCachedMatches().remove(position);

            String chatKey = m.getChatKey();

            mDb.removeMatch(linker.getLoggedInUser(), MainActivity.FIREBASE_BOTH_MATCHED, position);

            //TODO: removeAtPosition wont work for my match
            //mDb.removeMatch(m.getMatchMail(), MainActivity.FIREBASE_BOTH_MATCHED, position);

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
                        mDb.deleteChatRoom(matchChatKey);
                        notifyItemRangeChanged(newPosition, matches.size());            //Remove user from visible list locally
                        notifyItemChanged(newPosition);
                    }
                });   //why did it take me so long to realise this was missing....FFUUUUU

                mListItemContainer.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {                        //Launch IM fragment and add this fragment to back stack
                        Log.d(TAG, "Chatkey is: " + matchChatKey);
                        executeIfExists(mDb.getChatRoom(matchChatKey), new QueryMaster() {
                            @Override
                            public void run(DataSnapshot s) {
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
                            }
                        });
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

        public void executeIfExists(DatabaseReference databaseReference, final QueryMaster q) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        q.run(dataSnapshot);
                    else {
                        //Chatroom doenst exist maybe? do stuff
                        Toast.makeText(getActivity(), "Chat room doesn't exist anymore, you've been unmatched", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
}
