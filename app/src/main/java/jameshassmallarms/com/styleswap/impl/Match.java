package jameshassmallarms.com.styleswap.impl;

import android.graphics.Bitmap;

/**
 * Created by gary on 13/10/16.
 */

public class Match {
    private String matchName;
    private String matchMail;
    private Bitmap matchImage;
    private String matchNumber;

    public Match() {
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
}
