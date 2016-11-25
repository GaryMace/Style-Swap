package jameshassmallarms.com.styleswap.impl;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * User:
 *
 *              When a new user registers a User object is pushed to firebase with all the information
 *              they entered.
 *
 */
public class User {
    private String password;
    private String name;
    private String email;
    private int dressSize;
    private String phoneNum;
    private String bio;
    private int age;

    private List<Match> bothMatched = new ArrayList<>();
    private List<Match> matchedMe = new ArrayList<>();


    public User(String email, String password, String name, int age, int dressSize, String phoneNum){
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
        this.dressSize = dressSize;
        this.phoneNum = phoneNum;

        Match dummy = new Match();
        dummy.setMatchMail("Dummy so list exists");
        this.bothMatched.add(dummy);
        this.matchedMe.add(dummy);
    }

    public User(){

    }

    public User(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDressSize() {
        return dressSize;
    }

    public void setDressSize(int dressSize) {
        this.dressSize = dressSize;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getAge(){return age;    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Match toMatch(){
        Match m = new Match();
        m.setMatchName(this.getName());
        m.setMatchMail(this.getEmail());
        m.setMatchBio(this.getBio());
        m.setMatchNumber(this.getPhoneNum());

        return m;
    }

    public List<Match> getBothMatched() {
        return bothMatched;
    }

    public void setBothMatched(List<Match> bothMatched) {
        this.bothMatched = bothMatched;
    }

    public List<Match> getMatchedMe() {
        return matchedMe;
    }

    public void setMatchedMe(List<Match> matchedMe) {
        this.matchedMe = matchedMe;
    }

}
