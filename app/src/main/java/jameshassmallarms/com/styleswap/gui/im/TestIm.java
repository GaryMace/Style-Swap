package jameshassmallarms.com.styleswap.gui.im;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.infrastructure.DatabaseHandler;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
import jameshassmallarms.com.styleswap.infrastructure.Linker;

/**
 * Created by gary on 24/11/16.
 */

public class TestIm extends Fragment {
    public static final String MESSAGES_CHILD = "chats/haymakerStirrat@gmail.com/messages";
    private static final int REQUEST_INVITE = 1;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";
    private static final int MESSAGE_MINE = 0;
    private static final int MESSAGE_MATCHES = 1;
    private Linker linker;

    //Screen items
    private ImageButton mSendButton;
    private Random random;
    private EditText mMessageEditText;
    private ProgressBar mProgressBar;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    //Variables
    private String mUserMe;                                  //This is the logged in user
    private String mUserMatch;                               //This is the matched user's name
    private Bitmap mMyImg;
    private Bitmap mMatchImg;
    private String mChatKey;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder>
        mFirebaseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_im_chat, container, false);

        linker = (Linker) getActivity();
        random = new Random();

        Log.d("TAG", "Arg size: "+getArguments().size());
        mMyImg = linker.getUserProfilePic();
        mMatchImg = DatabaseHandler.getBitmapFromBlob(getArguments().getByteArray(MatchListFragment.ARGUMENT_MATCH_IMAGE));
        mUserMe = linker.getLoggedInUser();
        mUserMatch = getArguments().getString(MatchListFragment.ARGUMENT_MATCH_NAME);
        mChatKey = getArguments().getString(MatchListFragment.ARGUMENT_CHAT_KEY);

        mSendButton = (ImageButton) view.findViewById(R.id.sendMessageButton);
        mMessageEditText = (EditText) view.findViewById(R.id.messageEditText);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.msgListView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage,
            RecyclerView.ViewHolder>(
            ChatMessage.class,
            R.layout.chat_bubble_me,
            RecyclerView.ViewHolder.class,
            mFirebaseDatabaseReference.child("Chats").
                child(FireBaseQueries.encodeKey(mChatKey)).
                child("messages")) {

            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, ChatMessage message, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                if (message.getIsMine().equals(linker.getLoggedInUser())) {
                    populateMyBubble((MyMessageViewHolder) viewHolder, message, position);

                } else {
                    populateMatchBubble((MatchesMessageViewHolder) viewHolder, message, position);

                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == MESSAGE_MINE) {
                    View userMe = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.chat_bubble_me, parent, false);

                    return new MyMessageViewHolder(userMe);
                } else {
                    View userMe = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.chat_bubble_match, parent, false);

                    return new MatchesMessageViewHolder(userMe);
                }
            }


            @Override
            public int getItemViewType(int position) {
                ChatMessage message = getItem(position);
                Log.d("TAGGE", "is mine: " + message.getIsMine());
                if (message.getIsMine().equals(linker.getLoggedInUser())) {
                    return MESSAGE_MINE;
                } else {
                    return MESSAGE_MATCHES;
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                    mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                    (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessage message = new ChatMessage(mMessageEditText.getText().toString(), linker.getLoggedInUser());
                mFirebaseDatabaseReference.child("Chats")
                    .child(FireBaseQueries.encodeKey(mChatKey))
                    .child("messages")
                    .push().setValue(message);
                mMessageEditText.setText("");
            }
        });

        return view;
    }

    private void populateMyBubble(MyMessageViewHolder viewHolder, ChatMessage message, int position) {
        TextView msg;
        ImageView imgView;
        LinearLayout layout;
        LinearLayout parent_layout;

        msg = viewHolder.messageTextView;
        imgView = viewHolder.messengerImageView;
        layout = viewHolder.bubbleLayout;
        parent_layout = viewHolder.parentLayout;

        msg.setText(message.getText());

        imgView.setImageBitmap(mMyImg);
        layout.setBackgroundResource(R.drawable.im_my_chat_bubble);
        parent_layout.setGravity(Gravity.RIGHT);
        msg.setTextColor(Color.WHITE);
    }

    private void populateMatchBubble(MatchesMessageViewHolder viewHolder, ChatMessage message, int position) {
        TextView msg;
        ImageView imgView;
        LinearLayout layout;
        LinearLayout parent_layout;

        msg = viewHolder.messageTextView;
        imgView = viewHolder.messengerImageView;
        layout = viewHolder.bubbleLayout;
        parent_layout = viewHolder.parentLayout;

        msg.setText(message.getText());

        imgView.setImageBitmap(mMatchImg);
        layout.setBackgroundResource(R.drawable.im_match_chat_bubble);
        parent_layout.setGravity(Gravity.START);
        msg.setTextColor(Color.BLACK);
    }

    public class MyMessageViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout bubbleLayout;
        public LinearLayout parentLayout;
        public TextView messageTextView;
        public CircleImageView messengerImageView;

        public MyMessageViewHolder(View itemView) {
            super(itemView);
            bubbleLayout = (LinearLayout) itemView.findViewById(R.id.fragment_im_my_bubble);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.fragment_im_my_bubble_parent);
            messageTextView = (TextView) itemView.findViewById(R.id.fragment_im_my_msg);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.fragment_im_my_img);
        }
    }

    public class MatchesMessageViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout bubbleLayout;
        public LinearLayout parentLayout;
        public TextView messageTextView;
        public CircleImageView messengerImageView;

        public MatchesMessageViewHolder(View itemView) {
            super(itemView);
            bubbleLayout = (LinearLayout) itemView.findViewById(R.id.fragment_im_match_bubble);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.fragment_im_match_bubble_parent);
            messageTextView = (TextView) itemView.findViewById(R.id.fragment_im_match_msg);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.fragment_im_match_img);
        }
    }
}
