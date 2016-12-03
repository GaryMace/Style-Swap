package finalProject.com.styleswap.gui.im;

/**
 * ChatMessage:
 *
 *              This class is the object that defines a chat message in the ChatIm fragment. When the
 *              user wants to send a message to his/her match then this object is pushed to firebase,
 *              downloaded to both clients and inflated to their views.
 *
 *               Created by gary on 15/10/16.
 */
public class ChatMessage {
    private String id;
    private String text;
    private String isMine;  //this is the email of the sender so we can check who sent the message later

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

