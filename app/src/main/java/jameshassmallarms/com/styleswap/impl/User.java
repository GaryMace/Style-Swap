package jameshassmallarms.com.styleswap.impl;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gary on 24/10/16.
 */

public class User {
    private String password;
    private double locationLat;
    private double locationLon;
    private String name;
    private String email;
    private Bitmap img;
    private int dressSize;
    private String phoneNum;
    private String bio;
    private int age;

    private List<Match> bothMatched = new ArrayList<>();
    private List<Match> matchedMe = new ArrayList<>();

    public User(String name, int age, String email, String password, int dressSize) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
        this.dressSize = dressSize;
        Match dummy = new Match();
        dummy.setMatchMail("dummy so list exists");
        this.bothMatched.add(dummy);
        this.matchedMe.add(dummy);
    }

    public User(String name, String password){
        this.name = name;
        this.password = password;
        Match dummy = new Match();
        dummy.setMatchMail("Dummy so list exists");
        this.bothMatched.add(dummy);
        this.matchedMe.add(dummy);
    }

    public User(){}


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

    public double getLocationLat() {
        return locationLat;
    }

    public double getLocationLon() {
        return locationLon;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public void setLocationLon(double locationLon) {
        this.locationLon = locationLon;
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

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public Match toMatch(){
        Match m = new Match();
        m.setMatchName(this.getName());
        //m.setbio = this.bio;
        m.setMatchMail(this.getEmail());
        m.setMatchBio(this.getBio());
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
