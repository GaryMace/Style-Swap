package jameshassmallarms.com.styleswap.impl;

import android.graphics.Bitmap;

/**
 * Match:
 *
 *              Match objects are used when the logged in user finds a match with the app. This match
 *              object is populated and then pushed to firebase.
 *
 *              The emails for both users are then added to their respective matchedMe lists on firebase,
 *              a chat room is also set-up on firebase between the two users using an unique key
 *              according to the following:
 *              rule:
 *                  Email1Email2
 *                  e.g. Gary@live.ieAlan@live.ie
 *              Note that if User A likes User B and then User B likes User A, then user B's email is
 *              ALWAYS the first email in the unique key.
 *
 *              Both Users then gain access to the chat room key, it's inserted as an extra field in
 *              their bothMatched field.
 *
 *              This is then used by both users to get into their uniques chat room
 *
 */
public class Match {
    private String matchChatToken;    //ID's a unique firebase chat room for the users.
    private String matchName;
    private String matchMail;
    private Bitmap matchImage;
    private String matchNumber;
    private String matchBio;
    private byte[] byteArray;

    public Match() {
    }

    public String getMatchChatToken() {
        return matchChatToken;
    }

    public void setMatchChatToken(String matchChatToken) {
        this.matchChatToken = matchChatToken;
    }

    public String getMatchMail() {
        return matchMail;
    }

    public void setMatchMail(String matchMail) {
        this.matchMail = matchMail;
    }

    public Bitmap getMatchImage() {
        return matchImage;
    }

    public void setMatchImage(Bitmap matchImage) {
        this.matchImage = matchImage;
    }

    public String getMatchNumber() {
        return matchNumber;
    }

    public void setByteArray(byte[] img){this.byteArray = img;}

    public byte[] getByteArray(){return byteArray;}

    public void setMatchNumber(String matchNumber) {
        this.matchNumber = matchNumber;
    }

    public String toString(){
        return this.getMatchMail();
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public void setMatchBio(String matchBio){this.matchBio = matchBio;}

    public String getMatchBio(){return matchBio;}
}
