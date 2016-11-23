package jameshassmallarms.com.styleswap.gui.im;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Random;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.infrastructure.DatabaseHandler;
import jameshassmallarms.com.styleswap.infrastructure.Linker;

/**
 * Created by gary on 23/11/16.
 */

public class ChatIm extends Fragment implements View.OnClickListener {
    public static Bitmap match_img;                         //Matched users img
    public static Bitmap my_img;                            //Logged in user img
    private Linker linker;
    private EditText msg_edittext;
    private String userMe;       //This is the logged in user
    private String userMatch;                               //This is the matched user's name
    private Random random;
    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    ListView msgListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_im_chat, container, false);
        linker = (Linker) getActivity();
        random = new Random();

        Log.d("TAG", "Arg size: "+getArguments().size());
        my_img = linker.getUserProfilePic();
        match_img = DatabaseHandler.getBitmapFromBlob(getArguments().getByteArray(MatchListFragment.ARGUMENT_MATCH_IMAGE));
        userMe = linker.getLoggedInUser();
        userMatch = getArguments().getString(MatchListFragment.ARGUMENT_MATCH_NAME);

        msg_edittext = (EditText) view.findViewById(R.id.messageEditText);
        msgListView = (ListView) view.findViewById(R.id.msgListView);
        ImageButton sendButton = (ImageButton) view
            .findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(this);

        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        chatlist = new ArrayList<>();
        chatAdapter = new ChatAdapter(getActivity(), chatlist);
        msgListView.setAdapter(chatAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    public void sendTextMessage(View v) {
        String message = msg_edittext.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            final ChatMessage chatMessage = new ChatMessage(userMe, userMatch,
                message, "" + random.nextInt(1000), true);
            chatMessage.setMsgID();
            chatMessage.body = message;
            chatMessage.Date = DateTime.getCurrentDate();
            chatMessage.Time = DateTime.getCurrentTime();
            msg_edittext.setText("");

            /** Test Messages*/
            final ChatMessage chatMessage1 = new ChatMessage(userMe, userMatch,
                "Hello Joe! I like ducks", "" + random.nextInt(1000), true);
            chatMessage.setMsgID();
            chatMessage.body = message;
            chatMessage.Date = DateTime.getCurrentDate();
            chatMessage.Time = DateTime.getCurrentTime();

            final ChatMessage chatMessage3 = new ChatMessage(userMe, userMatch,
                "What about you?", "" + random.nextInt(1000), true);
            chatMessage.setMsgID();
            chatMessage.body = message;
            chatMessage.Date = DateTime.getCurrentDate();
            chatMessage.Time = DateTime.getCurrentTime();

            final ChatMessage chatMessage2 = new ChatMessage(userMe, userMatch,
                "FAAAACK, of course I like ducks,,,,", "" + random.nextInt(1000), false);
            chatMessage.setMsgID();
            chatMessage.body = message;
            chatMessage.Date = DateTime.getCurrentDate();
            chatMessage.Time = DateTime.getCurrentTime();
            chatAdapter.add(chatMessage1);
            chatAdapter.add(chatMessage3);
            chatAdapter.add(chatMessage2);

            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMessageButton:
                sendTextMessage(v);

        }
    }
    public class ChatMessage {

        public String body, sender, receiver, senderName;
        public String Date, Time;
        public String msgid;
        public boolean isMine;// Did I send the message.

        public ChatMessage(String Sender, String Receiver, String messageString,
                           String ID, boolean isMINE) {
            body = messageString;
            isMine = isMINE;
            sender = Sender;
            msgid = ID;
            receiver = Receiver;
            senderName = sender;
        }

        public void setMsgID() {
            msgid += "-" + String.format("%02d", new Random().nextInt(100));
        }
    }

}
