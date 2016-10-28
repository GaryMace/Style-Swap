package jameshassmallarms.com.styleswap.impl;

/**
 * Created by gary on 13/10/16.
 */

public class Match {
    private String matchName;
    private int matchImageKey; //Needs refactoring
    private String matchNumber;

    public Match() {

    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public int getMatchImageKey() {
        return matchImageKey;
    }

    public void setMatchImageKey(int matchImageKey) {
        this.matchImageKey = matchImageKey;
    }

    public String getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(String matchNumber) {
        this.matchNumber = matchNumber;
    }
}
