package jameshassmallarms.com.styleswap.gui.im;

import android.graphics.Bitmap;
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

import de.hdodenhof.circleimageview.CircleImageView;
import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.infrastructure.DatabaseHandler;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
import jameshassmallarms.com.styleswap.infrastructure.Linker;

/**
 * ChatIm:
 *
 *              This fragment inflates the messaging screen between two users. We use a Firebase
 *              recycler that listens for new messages on firebase in a chat room and pulls them down.
 *
 *              From there the messages are handled in such a way to determine who they belong to.
 *
 *              All of the messages that the current logged in user sends will be displayed as blue.
 *              All of the messages that a match sends will be displayed as grey for the logged in user.
 *
 *
 *
 *              Created by gary on 24/11/16.
 */

public class ChatIm extends Fragment {
    private static final int MESSAGE_MINE = 0;
    private static final int MESSAGE_MATCHES = 1;
    private Linker linker;

    //Screen items
    private ImageButton mSendButton;
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

        Log.d("TAG", "Arg size: "+getArguments().size());   //Args passed from MatchListFragment
        mMyImg = linker.getUserProfilePic();
        mMatchImg = DatabaseHandler.getBitmapFromBlob(getArguments().getByteArray(MatchListFragment.ARGUMENT_MATCH_IMAGE));     //Match Image
        mUserMe = linker.getLoggedInUser();
        mUserMatch = getArguments().getString(MatchListFragment.ARGUMENT_MATCH_NAME);   //Match name
        mChatKey = getArguments().getString(MatchListFragment.ARGUMENT_CHAT_KEY);       //Chat room key

        mSendButton = (ImageButton) view.findViewById(R.id.sendMessageButton);
        mMessageEditText = (EditText) view.findViewById(R.id.messageEditText);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);               //For when the messages are being downloaded
        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.msgListView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);

        // NOTE: the second generic param is a ViewHolder! this is so we can inflate different layouts
        // in the recycler depending on who sent what message.
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage,
            RecyclerView.ViewHolder>(
            ChatMessage.class,
            R.layout.chat_bubble_me,                            //this is a dummy layout, will be replaced
            RecyclerView.ViewHolder.class,
            mFirebaseDatabaseReference.child("Chats").          //Links to the chat room on firebase
                child(FireBaseQueries.encodeKey(mChatKey)).
                child("messages")) {

            //When we're loading messages make either a layout for me (blue bubble) or my Match (grey bubble)
            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, ChatMessage message, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                if (message.getIsMine().equals(linker.getLoggedInUser())) {                 //If the message loaded was mine, blue bubble it
                    populateMyBubble((MyMessageViewHolder) viewHolder, message, position);

                } else {    //If not my message load it into grey bubble
                    populateMatchBubble((MatchesMessageViewHolder) viewHolder, message, position);

                }
            }

            /**
             * This passes either the blue bubble layout (for my messages) or the grey bubble
             * layout (for my matches messages) to the relevant custom ViewHolder object.
             *
             * ^^ Remember earlier how we said that the recycler is of type <ChatMessage, Recycler.ViewHolder> ?
             */
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


            //Figures out if message to display is mine or my matches
            @Override
            public int getItemViewType(int position) {
                ChatMessage message = getItem(position);
                if (message.getIsMine().equals(linker.getLoggedInUser())) {
                    return MESSAGE_MINE;
                } else {
                    return MESSAGE_MATCHES;
                }
            }
        };

        //Registers the listener to firebase and keeps track of number messages
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                    mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                    (positionStart >= (messageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);      //Attach the firebase recycler to our actual recycler view

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessage message =
                    new ChatMessage(mMessageEditText.getText().toString(), linker.getLoggedInUser());   //This is a message from the logged-in user to send

                mFirebaseDatabaseReference.child("Chats")
                    .child(FireBaseQueries.encodeKey(mChatKey))     //Key to chat room
                    .child("messages")
                    .push().setValue(message);
                mMessageEditText.setText("");       //reset the submit message text field at the bottom of screen
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

        //The below defines a custom layout for messages that I send to my match
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

        //The below defines a custom layout for messages that my match sends to me
        imgView.setImageBitmap(mMatchImg);
        layout.setBackgroundResource(R.drawable.im_match_chat_bubble);
        parent_layout.setGravity(Gravity.START);
        msg.setTextColor(Color.BLACK);
    }

    /**
     * View holders that hold either the layout for my messages or the layout for my matches
     * messages.
     **/
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
