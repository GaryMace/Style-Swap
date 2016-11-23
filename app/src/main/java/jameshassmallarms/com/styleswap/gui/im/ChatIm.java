package jameshassmallarms.com.styleswap.gui.im;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Random;

import jameshassmallarms.com.styleswap.R;

/**
 * Created by gary on 23/11/16.
 */

public class ChatIm extends Fragment implements View.OnClickListener {
    public static Bitmap img;
    private EditText msg_edittext;
    private String user1 = "khushi", user2 = "khushi1";
    private Random random;
    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    ListView msgListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_im_chat, container, false);
        random = new Random();
        img = BitmapFactory.decodeResource(getResources(), R.drawable.james);
        img = (Bitmap.createScaledBitmap(img,200, 150,true));

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
            final ChatMessage chatMessage = new ChatMessage(user1, user2,
                message, "" + random.nextInt(1000), true);
            chatMessage.setMsgID();
            chatMessage.body = message;
            chatMessage.Date = DateTime.getCurrentDate();
            chatMessage.Time = DateTime.getCurrentTime();
            msg_edittext.setText("");

            /** Test Messages*/
            /*final ChatMessage chatMessage1 = new ChatMessage(user1, user2,
                "Hello Joe! I like ducks", "" + random.nextInt(1000), true);
            chatMessage.setMsgID();
            chatMessage.body = message;
            chatMessage.Date = DateTime.getCurrentDate();
            chatMessage.Time = DateTime.getCurrentTime();

            final ChatMessage chatMessage3 = new ChatMessage(user1, user2,
                "What about you?", "" + random.nextInt(1000), true);
            chatMessage.setMsgID();
            chatMessage.body = message;
            chatMessage.Date = DateTime.getCurrentDate();
            chatMessage.Time = DateTime.getCurrentTime();

            final ChatMessage chatMessage2 = new ChatMessage(user1, user2,
                "FAAAACK, of course I like ducks,,,,", "" + random.nextInt(1000), false);
            chatMessage.setMsgID();
            chatMessage.body = message;
            chatMessage.Date = DateTime.getCurrentDate();
            chatMessage.Time = DateTime.getCurrentTime();
            chatAdapter.add(chatMessage1);
            chatAdapter.add(chatMessage3);
            chatAdapter.add(chatMessage2);*/

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
