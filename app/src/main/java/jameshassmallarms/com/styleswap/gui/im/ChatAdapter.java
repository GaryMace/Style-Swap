package jameshassmallarms.com.styleswap.gui.im;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jameshassmallarms.com.styleswap.R;

/**
 * Created by gary on 23/11/16.
 */

public class ChatAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private ArrayList<ChatIm.ChatMessage> chatMessageList;

    public ChatAdapter(Activity activity, ArrayList<ChatIm.ChatMessage> list) {
        chatMessageList = list;
        inflater = (LayoutInflater) activity
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatIm.ChatMessage message = chatMessageList.get(position);
        View vi;
        TextView msg;
        ImageView imgView;
        LinearLayout layout;
        LinearLayout parent_layout;

            if (message.isMine) {
                vi = inflater.inflate(R.layout.chat_bubble_me, null);
                msg = (TextView) vi.findViewById(R.id.fragment_im_my_msg);
                imgView = (ImageView) vi.findViewById(R.id.fragment_im_my_img);
                layout = (LinearLayout) vi
                    .findViewById(R.id.fragment_im_my_bubble);
                parent_layout = (LinearLayout) vi
                    .findViewById(R.id.fragment_im_my_bubble_parent);
                imgView.setImageBitmap(ChatIm.my_img);
            } else {
                vi = inflater.inflate(R.layout.chat_bubble_match, null);
                msg = (TextView) vi.findViewById(R.id.fragment_im_match_msg);
                imgView = (ImageView) vi.findViewById(R.id.fragment_im_match_img);
                layout = (LinearLayout) vi
                    .findViewById(R.id.fragment_im_match_bubble);
                parent_layout = (LinearLayout) vi
                    .findViewById(R.id.fragment_im_match_bubble_parent);
                imgView.setImageBitmap(ChatIm.match_img);
            }
        msg.setText(message.body);

        // if message is mine then align to right
        if (message.isMine) {
            layout.setBackgroundResource(R.drawable.im_my_chat_bubble);
            parent_layout.setGravity(Gravity.END);
            msg.setTextColor(Color.WHITE);
        }
        // If not mine then align to left
        else {
            layout.setBackgroundResource(R.drawable.im_match_chat_bubble);
            parent_layout.setGravity(Gravity.START);
            msg.setTextColor(Color.BLACK);
        }
        return vi;
    }

    public void add(ChatIm.ChatMessage object) {
        chatMessageList.add(object);
    }
}