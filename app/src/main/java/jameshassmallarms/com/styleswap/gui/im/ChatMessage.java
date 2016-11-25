package jameshassmallarms.com.styleswap.gui.im;

/**
 * Created by gary on 24/11/16.
 */

public class ChatMessage {
    private String id;
    private String text;
    private String isMine;

    public ChatMessage() {

    }

    public ChatMessage(String text, String isMINE) {
        this.text = text;
        this.isMine = isMINE;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getIsMine() {
        return isMine;
    }

    public void setIsMine(String isMine) {
        this.isMine = isMine;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }
}

